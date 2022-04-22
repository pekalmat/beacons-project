package ch.zhaw.integration.beacons.rest.route.trilateration.helper;

import ch.zhaw.integration.beacons.entities.signal.Signal;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component
public class TrilaterationSignalPartitioner {

    public List<ImmutableTriple<Signal, Signal, Signal>> createTriplesOfClosestSignals(Map<String, List<Signal>> signalsMap, Comparator<Signal> signalDistanceComparator) {
        List<ImmutableTriple<Signal, Signal, Signal>> resultTriples = new ArrayList<>();
        for (Map.Entry<String, List<Signal>> entry : signalsMap.entrySet()) {
            // if min. 3 signals with same timestamp are present create triple of signals with shortest distance, else ignore
            if (entry.getValue().size() >= 3) {
                ImmutableTriple<Signal, Signal, Signal> resultTriple = null;
                Signal resultSignal1 = null;
                Signal resultSignal2 = null;
                Signal resultSignal3 = null;

                // sort signals by distance
                List<Signal> signalsSortedByDistance = entry.getValue();
                signalsSortedByDistance.sort(signalDistanceComparator);

                for (Signal signal : signalsSortedByDistance) {
                    if (resultSignal1 == null) {
                        resultSignal1 = signal;
                    } else if (resultSignal2 == null && isDifferentBeacon(signal, resultSignal1)) {
                        resultSignal2 = signal;
                    } else if (resultSignal3 == null && isDifferentBeacon(signal, resultSignal1) && isDifferentBeacon(signal, resultSignal2)) {
                        resultSignal3 = signal;
                        resultTriples.add(ImmutableTriple.of(resultSignal1, resultSignal2, resultSignal3));
                    }
                }
            }
        }

        return resultTriples;
    }

    private boolean isDifferentBeacon(Signal signal1, Signal signal2) {
        return !signal1.getBeacon().getMajor().equals(signal2.getBeacon().getMajor())
                || !signal1.getBeacon().getMinor().equals(signal2.getBeacon().getMinor());
    }

}
