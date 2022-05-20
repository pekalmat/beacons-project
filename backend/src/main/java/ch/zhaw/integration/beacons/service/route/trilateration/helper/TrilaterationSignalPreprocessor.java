package ch.zhaw.integration.beacons.service.route.trilateration.helper;

import ch.zhaw.integration.beacons.entities.beacon.Beacon;
import ch.zhaw.integration.beacons.entities.beacon.BeaconRepository;
import ch.zhaw.integration.beacons.entities.signal.Signal;
import ch.zhaw.integration.beacons.entities.signal.SignalRepository;
import ch.zhaw.integration.beacons.utils.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TrilaterationSignalPreprocessor {

    private final String[] floorsToIgnore;
    private final Double distCalcEnvironmentalFactor;
    private final int beaconSignalsMinCount;
    private final int beaconSignalsMinCountPeriod;
    private final BigDecimal beaconSignalMinDistance;
    private final BeaconRepository beaconRepository;
    private final SignalRepository signalRepository;

    public TrilaterationSignalPreprocessor(
            @Value("${beacons.trilateration.signals.ignore.floors}") String[] floorsToIgnore,
            @Value("${beacons.trilateration.distance.calculation.environmental.factor}") String distCalcEnvironmentalFactor,
            @Value("${beacons.trilateration.beacon.signal.min.count}")  String beaconSignalsMinCount,
            @Value("${beacons.trilateration.beacon.signal.min.count.period}") String beaconSignalsMinCountPeriod,
            @Value("${beacons.trilatration.beacon.min.distance}") String beaconSignalMinDistance,
            BeaconRepository beaconRepository,
            SignalRepository signalRepository) {
        this.floorsToIgnore = floorsToIgnore;
        this.distCalcEnvironmentalFactor = Double.parseDouble(distCalcEnvironmentalFactor);
        this.beaconSignalsMinCount = Integer.parseInt(beaconSignalsMinCount);
        this.beaconSignalsMinCountPeriod = Integer.parseInt(beaconSignalsMinCountPeriod);
        this.beaconSignalMinDistance = BigDecimal.valueOf(Double.parseDouble(beaconSignalMinDistance));
        this.beaconRepository = beaconRepository;
        this.signalRepository = signalRepository;
    }

    public Map<String, List<Signal>> preprocess(List<Signal> signals, Date routeStartTime, Date routeEndTime) {
        Map<String, List<Signal>> result = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.YYYY_MM_DD_HH_MM_SS_SSS);
        // Sort by SignalTimestamp
        Collections.sort(signals);
        // preprocess each beacon
        for(Signal signal : signals) {
            // calculate and set distances
            enrichSignalWithCalculatedDistance(signal);
            // connect Signal with beacon
            connectSignalsWithKnownSbbBeacons(signal);
            // if signal could be matched with beacon, add to result list
            if (signal.getBeacon() != null) {
                // only return signals if floor not excluded from analysis ( configured in application.properties)
                if(isFloorNotExcluded(signal.getBeacon())) {
                    // persist signal changes
                    signalRepository.save(signal);
                    // only return signals if beaconSignalMinCount reached
                    if(minBeaconSignalCountReachedInPeriod(signal, routeStartTime, routeEndTime)){
                        // only return signals if minCalcDistance is reached
                        if(signal.getCalculatedDistance().compareTo(beaconSignalMinDistance) >= 0 ) {
                            // grouping signals by signal-timestamp
                            if (result.containsKey(sdf.format(signal.getSignalTimestamp()))) {
                                result.get(sdf.format(signal.getSignalTimestamp())).add(signal);
                            } else {
                                ArrayList<Signal> newList = new ArrayList<>();
                                newList.add(signal);
                                result.put(sdf.format(signal.getSignalTimestamp()), newList);
                            }
                        }
                    }
                }
            }
         }
        return result;
    }

    public void connectSignalsWithKnownSbbBeacons(Signal signal) {
        Beacon matchingBeacon = beaconRepository.findBeaconByMajorAndMinor(signal.getMajor(), signal.getMinor());
        if(matchingBeacon != null) {
            signal.setBeacon(matchingBeacon);
        }
    }

    protected void enrichSignalWithCalculatedDistance(Signal signal) {
        // simple distance calculation
        BigDecimal calculatedDistance = calculateDistance(Double.valueOf(signal.getRssi()), signal.getTxPower());
        signal.setCalculatedDistance(calculatedDistance);
        // slidingWindow distance calculation
        BigDecimal calculatedDistanceSlidingWindow = calculateDistance(signal.getRunningAverageRssi(), signal.getTxPower());
        signal.setCalculatedDistanceSlidingWindow(calculatedDistanceSlidingWindow);
    }

    protected boolean isFloorNotExcluded(Beacon beacon) {
        for (String floor : floorsToIgnore) {
            if(beacon.getFloor().equals(floor)) {
                return false;
            }
        }
        return true;
    }

    protected boolean minBeaconSignalCountReachedInPeriod(Signal signal, Date routeStartTime, Date routeEndTime) {
        long count = 0;
        if (beaconSignalsMinCountPeriod == 0) {
            count = signalRepository.countByBeaconAndSignalTimestampBetween(signal.getBeacon(), routeStartTime, routeEndTime);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(signal.getSignalTimestamp());
            calendar.add(Calendar.SECOND, -(beaconSignalsMinCountPeriod/2));
            Date start = calendar.getTime();
            calendar.setTime(signal.getSignalTimestamp());
            calendar.add(Calendar.SECOND, beaconSignalsMinCountPeriod/2);
            Date end = calendar.getTime();
            count = signalRepository.countByBeaconAndSignalTimestampBetween(signal.getBeacon(), start, end);
        }
        return count >= beaconSignalsMinCount;
    }


    private BigDecimal calculateDistance(Double rssi, Integer txPower) {
        // source https://iotandelectronics.wordpress.com/2016/10/07/how-to-calculate-distance-from-the-rssi-value-of-the-ble-beacon/
        Double n = distCalcEnvironmentalFactor; // N (Constant depends on the Environmental factor. Range 2-4) -> in Indoor Environments usualy 4 due to lot of Noise
        return BigDecimal.valueOf(Math.pow(10, ((Double.valueOf(txPower) - rssi)) / (10.0 * n)));
    }

}
