package ch.zhaw.integration.beacons.rest.route.trilateration.calculator.slidingwindow;

import ch.zhaw.integration.beacons.entities.signal.Signal;
import ch.zhaw.integration.beacons.rest.route.trilateration.calculator.AbstractPositionCalculator;
import ch.zhaw.integration.beacons.rest.route.trilateration.helper.comparator.SignalCalculatedDistanceSlidingWindowComparator;
import ch.zhaw.integration.beacons.rest.route.trilateration.helper.TrilaterationSignalPartitioner;
import ch.zhaw.integration.beacons.utils.CalculationMethod;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SlidingWindowPositionCalculator extends AbstractPositionCalculator {

    private final TrilaterationSignalPartitioner trilaterationSignalPartitioner;
    private final SignalCalculatedDistanceSlidingWindowComparator signalCalculatedDistanceSlidingWindowComparator;

    public SlidingWindowPositionCalculator(TrilaterationSignalPartitioner trilaterationSignalPartitioner, SignalCalculatedDistanceSlidingWindowComparator signalCalculatedDistanceSlidingWindowComparator) {
        this.trilaterationSignalPartitioner = trilaterationSignalPartitioner;
        this.signalCalculatedDistanceSlidingWindowComparator = signalCalculatedDistanceSlidingWindowComparator;
    }

    @Override
    public List<ImmutableTriple<Signal, Signal, Signal>> prepareSignalsTriplesForCalculation(Map<String, List<Signal>> signalsMap) {
        // Partitions of 3 Signals of 3 Different Beacons at same time for Trilateration
        return trilaterationSignalPartitioner.createTriplesOfClosestSignals(signalsMap,signalCalculatedDistanceSlidingWindowComparator );
    }

    @Override
    public CalculationMethod getCalculationMethod() {
        return CalculationMethod.TRILATERATION_SLIDING_WINDOW;
    }

    @Override
    public double getDistanceLeft(ImmutableTriple<Signal, Signal, Signal> triple) {
        return triple.getLeft().getCalculatedDistanceSlidingWindow();
    }

    @Override
    public double getDistanceRight(ImmutableTriple<Signal, Signal, Signal> triple) {
        return triple.getRight().getCalculatedDistanceSlidingWindow();
    }

    @Override
    public double getDistanceMiddle(ImmutableTriple<Signal, Signal, Signal> triple) {
        return triple.getMiddle().getCalculatedDistanceSlidingWindow();
    }

}
