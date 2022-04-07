package ch.zhaw.integration.beacons.rest.route.trilateration.calculator;

import ch.zhaw.integration.beacons.entities.signal.Signal;
import ch.zhaw.integration.beacons.utils.CalculationMethod;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.stereotype.Component;

@Component
public class KalmannFilterLibraryDistPositionCalculator extends AbstractPositionCalculator{

    @Override
    CalculationMethod getCalculationMethod() {
        return CalculationMethod.TRILATERATION_KALMANN_FILTER_LIBRARY_DISTANCE;
    }

    @Override
    double getDistanceLeft(ImmutableTriple<Signal, Signal, Signal> partition) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    double getDistanceRight(ImmutableTriple<Signal, Signal, Signal> partition) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    double getDistanceMiddle(ImmutableTriple<Signal, Signal, Signal> partition) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
