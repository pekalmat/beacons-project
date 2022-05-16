package ch.zhaw.integration.beacons.service.route.trilateration.calculator.kalmann;

import ch.zhaw.integration.beacons.entities.signal.Signal;
import ch.zhaw.integration.beacons.service.route.trilateration.calculator.AbstractPositionCalculator;
import ch.zhaw.integration.beacons.utils.CalculationMethod;
import ch.zhaw.integration.beacons.utils.Calculator;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
public class KalmannFilterPositionCalculator extends AbstractPositionCalculator {

    public KalmannFilterPositionCalculator(Calculator calculator) {
        super(calculator);
    }

    @Override
    public CalculationMethod getCalculationMethod() {
        return CalculationMethod.TRILATERATION_KALMANN_FILTER;
    }

    @Override
    public List<ImmutableTriple<Signal, Signal, Signal>> prepareSignalsTriplesForCalculation(Map<String, List<Signal>> signalsMap) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public BigDecimal getDistanceLeft(ImmutableTriple<Signal, Signal, Signal> partition) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public BigDecimal getDistanceRight(ImmutableTriple<Signal, Signal, Signal> partition) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public BigDecimal getDistanceMiddle(ImmutableTriple<Signal, Signal, Signal> partition) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
