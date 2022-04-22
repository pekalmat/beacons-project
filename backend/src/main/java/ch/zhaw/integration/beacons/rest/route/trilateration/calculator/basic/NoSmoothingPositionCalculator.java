package ch.zhaw.integration.beacons.rest.route.trilateration.calculator.basic;

import ch.zhaw.integration.beacons.entities.signal.Signal;
import ch.zhaw.integration.beacons.rest.route.trilateration.calculator.AbstractPositionCalculator;
import ch.zhaw.integration.beacons.rest.route.trilateration.helper.TrilaterationSignalPartitioner;
import ch.zhaw.integration.beacons.rest.route.trilateration.helper.comparator.SignalCalculatedDistanceComparator;
import ch.zhaw.integration.beacons.utils.CalculationMethod;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class NoSmoothingPositionCalculator extends AbstractPositionCalculator {

    private final TrilaterationSignalPartitioner trilaterationSignalPartitioner;
    private final SignalCalculatedDistanceComparator signalCalculatedDistanceComparator;

    public NoSmoothingPositionCalculator(TrilaterationSignalPartitioner trilaterationSignalPartitioner, SignalCalculatedDistanceComparator signalCalculatedDistanceComparator) {
        this.trilaterationSignalPartitioner = trilaterationSignalPartitioner;
        this.signalCalculatedDistanceComparator = signalCalculatedDistanceComparator;
    }

    @Override
    public List<ImmutableTriple<Signal, Signal, Signal>> prepareSignalsTriplesForCalculation(Map<String, List<Signal>> signalsMap) {
        // Partitions of 3 Signals of 3 Different Beacons at same time for Trilateration
        return trilaterationSignalPartitioner.createTriplesOfClosestSignals(signalsMap, signalCalculatedDistanceComparator);
    }

    @Override
    public CalculationMethod getCalculationMethod() {
        return CalculationMethod.TRILATERATION_NO_SMOOTHING;
    }

    @Override
    public double getDistanceLeft(ImmutableTriple<Signal, Signal, Signal> triple) {
        return triple.getLeft().getCalculatedDistance();
    }

    @Override
    public double getDistanceRight(ImmutableTriple<Signal, Signal, Signal> triple) {
        return triple.getRight().getCalculatedDistance();
    }

    @Override
    public double getDistanceMiddle(ImmutableTriple<Signal, Signal, Signal> triple) {
        return triple.getMiddle().getCalculatedDistance();
    }

}
