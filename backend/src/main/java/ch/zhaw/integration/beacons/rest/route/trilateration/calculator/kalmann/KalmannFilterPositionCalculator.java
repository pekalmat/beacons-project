package ch.zhaw.integration.beacons.rest.route.trilateration.calculator.kalmann;

import ch.zhaw.integration.beacons.entities.signal.Signal;
import ch.zhaw.integration.beacons.rest.route.trilateration.calculator.AbstractPositionCalculator;
import ch.zhaw.integration.beacons.utils.CalculationMethod;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class KalmannFilterPositionCalculator extends AbstractPositionCalculator {

    @Override
    public CalculationMethod getCalculationMethod() {
        return CalculationMethod.TRILATERATION_KALMANN_FILTER;
    }

    @Override
    public List<ImmutableTriple<Signal, Signal, Signal>> prepareSignalsTriplesForCalculation(Map<String, List<Signal>> signalsMap) {
        throw new UnsupportedOperationException("Not implemented yet");
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
