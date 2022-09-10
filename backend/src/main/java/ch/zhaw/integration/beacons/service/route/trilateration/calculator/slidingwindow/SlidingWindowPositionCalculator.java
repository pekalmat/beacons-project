package ch.zhaw.integration.beacons.service.route.trilateration.calculator.slidingwindow;

import ch.zhaw.integration.beacons.entities.signal.Signal;
import ch.zhaw.integration.beacons.service.route.trilateration.calculator.AbstractPositionCalculator;
import ch.zhaw.integration.beacons.service.route.trilateration.calculator.algorithm.StackoverflowTrilaterationAlgorithm;
import ch.zhaw.integration.beacons.service.route.trilateration.helper.TrilaterationSignalPartitioner;
import ch.zhaw.integration.beacons.service.route.trilateration.helper.comparator.SignalCalculatedDistanceSlidingWindowComparator;
import ch.zhaw.integration.beacons.utils.CalculationMethod;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
public class SlidingWindowPositionCalculator extends AbstractPositionCalculator {

    private final TrilaterationSignalPartitioner trilaterationSignalPartitioner;
    private final SignalCalculatedDistanceSlidingWindowComparator signalCalculatedDistanceSlidingWindowComparator;

    public SlidingWindowPositionCalculator(StackoverflowTrilaterationAlgorithm trilaterationAlgorithm, TrilaterationSignalPartitioner trilaterationSignalPartitioner, SignalCalculatedDistanceSlidingWindowComparator signalCalculatedDistanceSlidingWindowComparator) {
        super(trilaterationAlgorithm);
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
    public BigDecimal getDistanceLeft(ImmutableTriple<Signal, Signal, Signal> triple) {
        return triple.getLeft().getCalculatedDistanceSlidingWindow();
    }

    @Override
    public BigDecimal getDistanceRight(ImmutableTriple<Signal, Signal, Signal> triple) {
        return triple.getRight().getCalculatedDistanceSlidingWindow();
    }

    @Override
    public BigDecimal getDistanceMiddle(ImmutableTriple<Signal, Signal, Signal> triple) {
        return triple.getMiddle().getCalculatedDistanceSlidingWindow();
    }

}
