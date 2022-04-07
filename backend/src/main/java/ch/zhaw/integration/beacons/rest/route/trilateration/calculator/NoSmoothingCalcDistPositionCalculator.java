package ch.zhaw.integration.beacons.rest.route.trilateration.calculator;

import ch.zhaw.integration.beacons.entities.signal.Signal;
import ch.zhaw.integration.beacons.utils.CalculationMethod;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.stereotype.Component;

@Component
public class NoSmoothingCalcDistPositionCalculator  extends AbstractPositionCalculator {

    @Override
    CalculationMethod getCalculationMethod() {
        return CalculationMethod.TRILATERATION_NO_SMOOTHING_CALCULATED_DISTANCE;
    }

    @Override
    double getDistanceLeft(ImmutableTriple<Signal, Signal, Signal> partition) {
        return partition.getLeft().getCalculatedDistance();
    }

    @Override
    double getDistanceRight(ImmutableTriple<Signal, Signal, Signal> partition) {
        return partition.getRight().getCalculatedDistance();
    }

    @Override
    double getDistanceMiddle(ImmutableTriple<Signal, Signal, Signal> partition) {
        return partition.getMiddle().getCalculatedDistance();
    }



}
