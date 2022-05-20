package ch.zhaw.integration.beacons.service.route.trilateration.calculator.algorithm;

import ch.zhaw.integration.beacons.utils.Calculator;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TrilaterationAlgorithm {

    private Calculator calc;

    public TrilaterationAlgorithm(Calculator calculator) {
        this.calc = calculator;
    }

    // implementation based on  https://www.researchgate.net/figure/Trilateration-algorithm-for-object-localization-using-three-beacons-B-1-B-2-and-B-3_fig1_338241733
    public Pair<BigDecimal, BigDecimal> trilateratePositionCoordinates(
            Pair<BigDecimal, BigDecimal> location1,
            Pair<BigDecimal, BigDecimal> location2,
            Pair<BigDecimal, BigDecimal> location3,
            BigDecimal distance1,
            BigDecimal distance2,
            BigDecimal distance3 ) {
        BigDecimal xCoBaseBeaconLoc1 = location1.getFirst();
        BigDecimal yCoBaseBeaconLoc1 = location2.getSecond();

        BigDecimal xCoBeaconLoc2 = location2.getFirst();
        BigDecimal yCoBeaconLoc2 = location2.getSecond();

        BigDecimal xCoBeaconLoc3 = location3.getFirst();
        BigDecimal yCoBeaconLoc3 = location3.getSecond();

        // Equation 1 -> calculate = V^2
        BigDecimal vPower2 = calc.add(calc.power(xCoBeaconLoc3, 2), calc.power(yCoBeaconLoc3, 2));

        // Equastion 2 -> calculate U = distance between Beacon1 and Beaco 2
        //BigDecimal distBaceLoc1Loc2 = calc.multiply(calc.sqrt(calc.add(calc.power(calc.subtract(xCoBeaconLoc2, xCoBaseBeaconLoc1),2), calc.power(calc.subtract(yCoBeaconLoc2, yCoBaseBeaconLoc1),2))), BigDecimal.valueOf(10000));
        BigDecimal distBaceLoc1Loc2 = calc.sqrt(calc.add(calc.power(calc.subtract(xCoBeaconLoc2, xCoBaseBeaconLoc1),2), calc.power(calc.subtract(yCoBeaconLoc2, yCoBaseBeaconLoc1),2)));

        BigDecimal radiusCircleBaseLoc1 = distance1;
        BigDecimal radiusCircleBeacLoc2 = distance2;
        BigDecimal radiusCircleBeacLoc3 = distance3;

        // Equation 3 -> calculate xCoordinate
        BigDecimal x = calc.divide(calc.add(calc.subtract(calc.power(radiusCircleBaseLoc1, 2), calc.power(radiusCircleBeacLoc2, 2)), calc.power(distBaceLoc1Loc2, 2)), calc.multiply(BigDecimal.valueOf(2), distBaceLoc1Loc2));

        // Equation 3 -> calculate yCoordinate
        BigDecimal y = calc.divide(
                calc.subtract(
                        calc.add(
                                calc.subtract(
                                        calc.power(radiusCircleBaseLoc1, 2),
                                        calc.power(radiusCircleBeacLoc3, 2)),
                                vPower2),
                        calc.multiply(
                                calc.multiply(BigDecimal.valueOf(2), xCoBeaconLoc3), x)
                ),
                calc.multiply(BigDecimal.valueOf(2), yCoBeaconLoc3));
        return Pair.of(x, y);
    }


}
