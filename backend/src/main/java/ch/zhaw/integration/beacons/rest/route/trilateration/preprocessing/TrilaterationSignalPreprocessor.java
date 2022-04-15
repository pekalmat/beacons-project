package ch.zhaw.integration.beacons.rest.route.trilateration.preprocessing;

import ch.zhaw.integration.beacons.entities.beacon.Beacon;
import ch.zhaw.integration.beacons.entities.beacon.BeaconRepository;
import ch.zhaw.integration.beacons.entities.signal.Signal;
import ch.zhaw.integration.beacons.entities.signal.SignalRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class TrilaterationSignalPreprocessor {

    private final BeaconRepository beaconRepository;
    private final SignalRepository signalRepository;

    public TrilaterationSignalPreprocessor(
            BeaconRepository beaconRepository,
            SignalRepository signalRepository) {
        this.beaconRepository = beaconRepository;
        this.signalRepository = signalRepository;
    }

    public List<Signal> preprocess(List<Signal> signals) {
        // Sort by SignalTimestamp
        Collections.sort(signals);
        // Connect Signals with Known Beacons
        enrichSignalWithCalculatedDistance(signals);
        return connectSignalsWithKnownSbbBeacons(signals);
    }

    private void enrichSignalWithCalculatedDistance(List<Signal> signals) {
        for(Signal signal : signals) {
            // source https://iotandelectronics.wordpress.com/2016/10/07/how-to-calculate-distance-from-the-rssi-value-of-the-ble-beacon/
            int n = 2; // N (Constant depends on the Environmental factor. Range 2-4)
            double distance2 = Math.pow(10, (Double.parseDouble(String.valueOf(Math.subtractExact(signal.getTxPower(), signal.getRssi()))) / (10 * n)));
            signal.setCalculatedDistance(distance2);
        }
    }

    public List<Signal> connectSignalsWithKnownSbbBeacons(List<Signal> signals) {
        List<Signal> matchedSignals = new ArrayList<>();
        for(Signal signal : signals) {
            Beacon matchingBeacon = beaconRepository.findBeaconByMajorAndMinor(signal.getMajor(), signal.getMinor());
            if(matchingBeacon != null) {
                signal.setBeacon(matchingBeacon);
                matchedSignals.add(signal);
            }
        }
        return signalRepository.saveAll(matchedSignals);
    }

}