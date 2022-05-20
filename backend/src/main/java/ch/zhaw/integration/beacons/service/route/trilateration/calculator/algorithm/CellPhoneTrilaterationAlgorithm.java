package ch.zhaw.integration.beacons.service.route.trilateration.calculator.algorithm;

import ch.zhaw.integration.beacons.utils.Calculator;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CellPhoneTrilaterationAlgorithm {

    private Calculator calc;

    public CellPhoneTrilaterationAlgorithm(Calculator calculator) {
        this.calc = calculator;
    }

    // implementation based on https://www.101computing.net/cell-phone-trilateration-algorithm/

    public Pair<BigDecimal, BigDecimal> trilateratePositionCoordinates(
            Pair<BigDecimal, BigDecimal> location1,
            Pair<BigDecimal, BigDecimal> location2,
            Pair<BigDecimal, BigDecimal> location3,
            BigDecimal distance1,
            BigDecimal distance2,
            BigDecimal distance3) {

        BigDecimal x1 = location1.getFirst();
        BigDecimal y1 = location1.getSecond();
        BigDecimal r1 = distance1;

        BigDecimal x2 = location2.getFirst();
        BigDecimal y2 = location2.getSecond();
        BigDecimal r2 = distance2;

        BigDecimal x3 = location3.getFirst();
        BigDecimal y3 = location3.getSecond();
        BigDecimal r3 = distance3;

        BigDecimal A = calc.subtract(calc.multiply(BigDecimal.valueOf(2), x2), calc.multiply(BigDecimal.valueOf(2), x1));
        BigDecimal B = calc.subtract(calc.multiply(BigDecimal.valueOf(2), y2), calc.multiply(BigDecimal.valueOf(2), y1));
        BigDecimal C = calc.add(calc.subtract(calc.add(calc.subtract(calc.subtract(calc.power(r1, 2), calc.power(r2, 2)), calc.power(x1, 2)), calc.power(x2, 2)), calc.power(y1, 2)), calc.power(y2, 2));
        BigDecimal D = calc.subtract(calc.multiply(BigDecimal.valueOf(2), x3), calc.multiply(BigDecimal.valueOf(2), x2));
        BigDecimal E = calc.subtract(calc.multiply(BigDecimal.valueOf(2), y3), calc.multiply(BigDecimal.valueOf(2), y2));
        BigDecimal F = calc.add(calc.subtract(calc.add(calc.subtract(calc.subtract(calc.power(r2, 2), calc.power(r3, 2)), calc.power(x2, 2)), calc.power(x3, 2)), calc.power(y2, 2)), calc.power(y3, 2));
        BigDecimal x = calc.divide(calc.subtract(calc.multiply(C, E), calc.multiply(F, B)), calc.subtract(calc.multiply(E, A), calc.multiply(B, D)));
        BigDecimal y = calc.divide(calc.subtract(calc.multiply(C, D), calc.multiply(A, F)), calc.subtract(calc.multiply(B, D), calc.multiply(A, E)));

        return Pair.of(x, y);
    }
}
