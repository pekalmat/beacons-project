package ch.zhaw.integration.beacons.rest.route.trilateration.preprocessing;

import ch.zhaw.integration.beacons.entities.beacon.Beacon;
import ch.zhaw.integration.beacons.entities.beacon.BeaconRepository;
import ch.zhaw.integration.beacons.entities.signal.Signal;
import ch.zhaw.integration.beacons.entities.signal.SignalRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class TrilaterationSignalPreprocessor {

    private final String distanceCalculationType;
    private final BeaconRepository beaconRepository;
    private final SignalRepository signalRepository;

    public TrilaterationSignalPreprocessor(
            @Value("${beacons.trilateration.signals.distance.calculation.type}") String distanceCalculationType,
            BeaconRepository beaconRepository,
            SignalRepository signalRepository) {
        this.distanceCalculationType = distanceCalculationType;
        this.beaconRepository = beaconRepository;
        this.signalRepository = signalRepository;
    }

    public List<Signal> preprocess(List<Signal> signals) {
        // Sort by SignalTimestamp
        Collections.sort(signals);
        // Connect Signals with Known Beacons
        List<Signal> matchedSignals = new ArrayList<>();
        for(Signal signal : signals) {
            connectSignalsWithKnownSbbBeacons(signal).ifPresent(matchedSignals::add);
            // calculate and set distance
            enrichSignalWithCalculatedDistance(signal);

        }

        return signalRepository.saveAll(matchedSignals);
    }

    private void enrichSignalWithCalculatedDistance(Signal signal) {
        // source https://iotandelectronics.wordpress.com/2016/10/07/how-to-calculate-distance-from-the-rssi-value-of-the-ble-beacon/
        int n = 4; // N (Constant depends on the Environmental factor. Range 2-4) -> in Indoor Environments usualy 4 due to lot of Noise
        double rssi = distanceCalculationType.equals("RunningAverageRssi") ? signal.getRunningAverageRssi() : Double.valueOf(signal.getRssi());
        double distance2 = Math.pow(10, ((Double.valueOf(signal.getTxPower()) - rssi)) / (10 * n));
        signal.setCalculatedDistance(distance2);
    }

    public Optional<Signal> connectSignalsWithKnownSbbBeacons(Signal signal) {
        Beacon matchingBeacon = beaconRepository.findBeaconByMajorAndMinor(signal.getMajor(), signal.getMinor());
        if(matchingBeacon != null) {
            signal.setBeacon(matchingBeacon);
            return Optional.of(signal);
        }
        return Optional.empty();
    }

}
