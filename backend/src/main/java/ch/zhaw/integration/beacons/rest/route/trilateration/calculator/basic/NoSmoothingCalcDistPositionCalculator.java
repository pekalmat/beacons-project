package ch.zhaw.integration.beacons.rest.route.trilateration.calculator.basic;

import ch.zhaw.integration.beacons.entities.signal.Signal;
import ch.zhaw.integration.beacons.rest.route.trilateration.calculator.AbstractPositionCalculator;
import ch.zhaw.integration.beacons.utils.CalculationMethod;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.stereotype.Component;

@Component
public class NoSmoothingCalcDistPositionCalculator  extends AbstractPositionCalculator {

    @Override
    public CalculationMethod getCalculationMethod() {
        return CalculationMethod.TRILATERATION_NO_SMOOTHING_CALCULATED_DISTANCE;
    }

    @Override
    public double getDistanceLeft(ImmutableTriple<Signal, Signal, Signal> partition) {
        return partition.getLeft().getCalculatedDistance();
    }

    @Override
    public double getDistanceRight(ImmutableTriple<Signal, Signal, Signal> partition) {
        return partition.getRight().getCalculatedDistance();
    }

    @Override
    public double getDistanceMiddle(ImmutableTriple<Signal, Signal, Signal> partition) {
        return partition.getMiddle().getCalculatedDistance();
    }



}
