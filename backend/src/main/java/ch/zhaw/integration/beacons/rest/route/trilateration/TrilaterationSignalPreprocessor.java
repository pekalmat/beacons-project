package ch.zhaw.integration.beacons.rest.route.trilateration;

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

    List<Signal> preprocess(List<Signal> signals) {
        // Sort by SignalTimestamp
        Collections.sort(signals);
        // Connect Signals with Known Beacons
        return connectSignalsWithKnownSbbBeacons(signals);
    }

    private List<Signal> connectSignalsWithKnownSbbBeacons(List<Signal> signals) {
        List<Signal> matchedSignals = new ArrayList<>();
        for(Signal signal : signals) {
            Beacon matchingBeacon = beaconRepository.findBeaconByUuidAndMajorAndMinor(signal.getUuid(), signal.getMajor(), signal.getMinor());
            if(matchingBeacon != null) {
                signal.setBeacon(matchingBeacon);
                matchedSignals.add(signal);
            }
        }
        return signalRepository.saveAll(matchedSignals);
    }

}
