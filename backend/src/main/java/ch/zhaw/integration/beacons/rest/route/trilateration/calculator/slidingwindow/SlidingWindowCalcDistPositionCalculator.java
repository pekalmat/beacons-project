package ch.zhaw.integration.beacons.rest.route.trilateration.calculator.slidingwindow;

import ch.zhaw.integration.beacons.entities.signal.Signal;
import ch.zhaw.integration.beacons.rest.route.trilateration.calculator.AbstractPositionCalculator;
import ch.zhaw.integration.beacons.utils.CalculationMethod;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.stereotype.Component;

@Component
public class SlidingWindowCalcDistPositionCalculator extends AbstractPositionCalculator {

    @Override
    public CalculationMethod getCalculationMethod() {
        return CalculationMethod.TRILATERATION_SLIDING_WINDOW_CALCULATED_DISTANCE;
    }

    @Override
    public double getDistanceLeft(ImmutableTriple<Signal, Signal, Signal> partition) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public double getDistanceRight(ImmutableTriple<Signal, Signal, Signal> partition) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public double getDistanceMiddle(ImmutableTriple<Signal, Signal, Signal> partition) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
