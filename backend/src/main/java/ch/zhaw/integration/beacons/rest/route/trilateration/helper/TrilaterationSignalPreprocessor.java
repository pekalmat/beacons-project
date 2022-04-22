package ch.zhaw.integration.beacons.rest.route.trilateration.helper;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TrilaterationSignalPreprocessor {

    private final String[] floorsToIgnore;
    private final int distCalcEnvironmentalFactor;
    private final BeaconRepository beaconRepository;
    private final SignalRepository signalRepository;

    public TrilaterationSignalPreprocessor(
            @Value("${beacons.trilateration.signals.ignore.floors}") String[] floorsToIgnore,
            @Value("${beacons.trilateration.distance.calculation.environmental.factor}") String distCalcEnvironmentalFactor,
            BeaconRepository beaconRepository,
            SignalRepository signalRepository) {
        this.floorsToIgnore = floorsToIgnore;
        this.distCalcEnvironmentalFactor = Integer.parseInt(distCalcEnvironmentalFactor);
        this.beaconRepository = beaconRepository;
        this.signalRepository = signalRepository;
    }

    public Map<String, List<Signal>> preprocess(List<Signal> signals) {
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

    private BigDecimal calculateDistance(Double rssi, Integer txPower) {
        // source https://iotandelectronics.wordpress.com/2016/10/07/how-to-calculate-distance-from-the-rssi-value-of-the-ble-beacon/
        int n = distCalcEnvironmentalFactor; // N (Constant depends on the Environmental factor. Range 2-4) -> in Indoor Environments usualy 4 due to lot of Noise
        return BigDecimal.valueOf(Math.pow(10, ((Double.valueOf(txPower) - rssi)) / (10 * n)));
    }

}
