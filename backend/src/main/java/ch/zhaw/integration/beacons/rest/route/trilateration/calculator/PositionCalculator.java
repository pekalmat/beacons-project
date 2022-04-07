package ch.zhaw.integration.beacons.rest.route.trilateration.calculator;

import ch.zhaw.integration.beacons.entities.position.Position;
import ch.zhaw.integration.beacons.entities.route.Route;
import ch.zhaw.integration.beacons.entities.signal.Signal;
import ch.zhaw.integration.beacons.utils.CalculationMethod;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PositionCalculator {

    private final Map<CalculationMethod, AbstractPositionCalculator> calculators;

    public PositionCalculator(Collection<AbstractPositionCalculator> calculators) {
        this.calculators = calculators.stream().collect((Collectors.toMap(AbstractPositionCalculator::getCalculationMethod, Function.identity())));
    }

    AbstractPositionCalculator getCalculator(CalculationMethod calculationMethod) {
        return calculators.get(calculationMethod);
    }

    public List<Position> calculatePositions(CalculationMethod calculationMethod, List<ImmutableTriple<Signal, Signal, Signal>> positionSignals, Route route) {
        AbstractPositionCalculator calculator = getCalculator(calculationMethod);
        return calculator.calculatePositions(positionSignals, route);
    }
}
