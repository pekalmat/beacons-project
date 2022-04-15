package ch.zhaw.integration.beacons.rest.route.trilateration.preprocessing;

import ch.zhaw.integration.beacons.entities.beacon.Beacon;
import ch.zhaw.integration.beacons.entities.signal.Signal;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class TrilaterationSignalPartitioner {

    private final Double maxAcceptableDifferenceInSeconds;
    private final String[] floorsToIgnore;

    public TrilaterationSignalPartitioner(
            @Value("${beacons.trilateration.signals.timestamp.difference.max}") String maxAcceptableDifferenceInSecondsString,
            @Value("${beacons.trilateration.signals.ignore.floors}") String[] floorsToIgnore) {
        this.maxAcceptableDifferenceInSeconds = Double.parseDouble(maxAcceptableDifferenceInSecondsString);
        this.floorsToIgnore = floorsToIgnore;
    }

    public List<ImmutableTriple<Signal, Signal, Signal>> createPartitionsOf3ClosestSignalsOf3DifferentBeaconsForTrilateration(List<Signal> signals) {
        List<ImmutableTriple<Signal, Signal, Signal>> partitionsOf3 = new ArrayList<>();
        for(Signal signal : signals) {
            if(!ignoreFoor(signal.getBeacon())) {
                Signal signal2 = getClosestSignalOfAnotherBeacon(signals, List.of(signal));
                if (signal2 != null) {
                    Signal signal3 = getClosestSignalOfAnotherBeacon(signals, List.of(signal, signal2));
                    if (signal3 != null) {
                        if (!signalTimestampDifferenceBiggerThanSeconds(signal, signal2) && !signalTimestampDifferenceBiggerThanSeconds(signal, signal3) && !signalTimestampDifferenceBiggerThanSeconds(signal2, signal3)) {
                            ImmutableTriple<Signal, Signal, Signal> triple = ImmutableTriple.of(signal, signal2, signal3);
                            partitionsOf3.add(triple);
                        }
                    }
                }
            }
        }
        return distinctDuplicateTriples(partitionsOf3);
    }

    private boolean signalTimestampDifferenceBiggerThanSeconds(Signal signal, Signal signal2) {
        Date d1 = signal.getSignalTimestamp();
        Date d2 = signal2.getSignalTimestamp();
        double seconds = (d2.getTime()-d1.getTime())/1000.0;
        return seconds > maxAcceptableDifferenceInSeconds || seconds < (-maxAcceptableDifferenceInSeconds);
    }


    private Signal getClosestSignalOfAnotherBeacon(List<Signal> signals, List<Signal> signalsToIgnore) {
        for(Signal signal : signals) {
            if(!ignoreSignal(signal, signalsToIgnore) && !ignoreFoor(signal.getBeacon())){
                    return signal;
            }
        }
        return null;
    }

    private boolean ignoreSignal(Signal signal, List<Signal> signalsToIgnore) {
        for(Signal toIgnore : signalsToIgnore) {
            if(sameBeacon(signal, toIgnore) || signalTimestampDifferenceBiggerThanSeconds(signal, toIgnore)) {
                return true;
            }
        }
        return false;
    }

    private boolean ignoreFoor(Beacon beacon) {
        for (String floor : floorsToIgnore) {
            if(beacon.getFloor().equals(floor)) {
                return true;
            }
        }
        return false;
    }

    private boolean sameBeacon(Signal signal1, Signal signal2) {
        return signal1.getBeacon().equals(signal2.getBeacon());
    }

    private List<ImmutableTriple<Signal, Signal, Signal>> distinctDuplicateTriples(List<ImmutableTriple<Signal, Signal, Signal>> triples) {
        List<ImmutableTriple<Signal, Signal, Signal>> result = new ArrayList<>();
        for(ImmutableTriple<Signal, Signal, Signal> triple : triples) {
            boolean existsInResultList = false;
            for(ImmutableTriple<Signal, Signal, Signal> resultEntry : result) {
                List<Signal> resultEntryList = List.of(resultEntry.getLeft(), resultEntry.getMiddle(), resultEntry.getRight());
                if (resultEntryList.contains(triple.getLeft()) && resultEntryList.contains(triple.getMiddle()) && resultEntryList.contains(triple.getRight())) {
                    existsInResultList = true;
                    break;
                }
            }
            if(!existsInResultList) {
                result.add(triple);
            }
        }
        return result;
    }
}