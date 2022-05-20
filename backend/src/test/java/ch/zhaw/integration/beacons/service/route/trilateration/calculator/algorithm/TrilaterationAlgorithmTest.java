package ch.zhaw.integration.beacons.service.route.trilateration.calculator.algorithm;

import ch.zhaw.integration.beacons.utils.Calculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

import java.math.BigDecimal;

public class TrilaterationAlgorithmTest {

    private StackoverflowTrilaterationAlgorithm sut1;
    private CellPhoneTrilaterationAlgorithm sut2;
    private TrilaterationAlgorithm sut3;
    private TrilaterationLibraryAlgorithm sut4;

    private Calculator calculator;

    @BeforeEach
    public void setUp() {
        calculator = new Calculator(30);
        sut1 = new StackoverflowTrilaterationAlgorithm(calculator);
        sut2 = new CellPhoneTrilaterationAlgorithm(calculator);
        sut3 = new TrilaterationAlgorithm(calculator);
        sut4 = new TrilaterationLibraryAlgorithm(calculator);
    }

    @Test
    public void trilateratePositionCoordinatesTESTwithDifferentAlgorithmsAndLogResultsForComparison() {
        // Test Case 1          -> simple, mock-data, optimum (genau ein genauer schnittpunkt der 3 kreise)
        checkTestCase(BigDecimal.valueOf(1.0) , BigDecimal.valueOf(1.0),
                BigDecimal.valueOf(1.41421356237), BigDecimal.valueOf(0.0), BigDecimal.valueOf(0.0),
                BigDecimal.valueOf(1.01980390272), BigDecimal.valueOf(0.8), BigDecimal.valueOf(2.0),
                BigDecimal.valueOf(1.41421356237), BigDecimal.valueOf(2.0), BigDecimal.valueOf(0.0));

        // Test Case 2
        /* checkTestCase(BigDecimal.valueOf(4.0) , BigDecimal.valueOf(3.99462782549),
                BigDecimal.valueOf(1.41421356237), BigDecimal.valueOf(3.0), BigDecimal.valueOf(3.0),
                BigDecimal.valueOf(1.41421356237), BigDecimal.valueOf(5.0), BigDecimal.valueOf(3.0),
                BigDecimal.valueOf(1.4), BigDecimal.valueOf(4.0), BigDecimal.valueOf(5.4));
        */
    }

    private void checkTestCase(BigDecimal xExpected, BigDecimal yExpected,
                               BigDecimal distLoc1, BigDecimal xLoc1, BigDecimal yLoc1,
                               BigDecimal distLoc2, BigDecimal xLoc2, BigDecimal yLoc2,
                               BigDecimal distLoc3, BigDecimal xLoc3, BigDecimal yLoc3) {
        Pair<BigDecimal, BigDecimal> location1 = Pair.of(xLoc1, yLoc1);
        Pair<BigDecimal, BigDecimal> location2 = Pair.of(xLoc2, yLoc2);
        Pair<BigDecimal, BigDecimal> location3 = Pair.of(xLoc3, yLoc3);

        System.out.println("###########################################################");
        System.out.println("################## TEST-RESULTS ###########################");
        System.out.println("###########################################################");
        System.out.println("input Location1 --->  x = " + xLoc1 + " | y = " + yLoc1 + " | distance = " + distLoc1);
        System.out.println("input Location2 --->  x = " + xLoc2 + " | y = " + yLoc2 + " | distance = " + distLoc2);
        System.out.println("input Location3 --->  x = " + xLoc3 + " | y = " + yLoc3 + " | distance = " + distLoc3);
        // 1. best (currently) calculation
        Pair<BigDecimal, BigDecimal> resultCurrent = sut1.trilateratePositionCoordinates(location1, location2, location3, distLoc1, distLoc2, distLoc3);
        System.out.println();
        System.out.println("######  trilateratePositionCoodinates()  ->  (current) #####");
        System.out.println(" xExpected: " + xExpected + "   |   yExpected: " + yExpected);
        System.out.println(" xResult:   " + resultCurrent.getFirst() + "   |   yResult:   " + resultCurrent.getSecond());

        // 2. calculation - Quelle: https://www.101computing.net/cell-phone-trilateration-algorithm/
        Pair<BigDecimal, BigDecimal> resultNewTrill1 = sut2.trilateratePositionCoordinates(location1, location2, location3, distLoc1 ,distLoc2, distLoc3);
        System.out.println();
        System.out.println("######  newTrill1() #####");
        System.out.println(" xExpected: " + xExpected + "   |   yExpected: " + yExpected);
        System.out.println(" xResult:   " + resultNewTrill1.getFirst() + "   |   yResult:   " + resultNewTrill1.getSecond());


        // 3. calculation -  Quelle: https://www.researchgate.net/figure/Trilateration-algorithm-for-object-localization-using-three-beacons-B-1-B-2-and-B-3_fig1_338241733
        /*
        Pair<BigDecimal, BigDecimal> resultNewTrill2 = sut3.trilateratePositionCoordinates(location1, location2, location3, distLoc1, distLoc2, distLoc3);
        System.out.println();
        System.out.println("######  newTrill2() #####");
        System.out.println(" xExpected: " + xExpected + "   |   yExpected: " + yExpected);
        System.out.println(" xResult:   " + resultNewTrill2.getFirst() + "   |   yResult:   " + resultNewTrill2.getSecond());
        */
        // 4. calculation - Quelle: https://github.com/lemmingapex/trilateration
        Pair<Double, Double> resultNewTrill3Library = sut4.trilateratePositionCoordinates(location1, location2, location3, distLoc1, distLoc2, distLoc3);
        System.out.println();
        System.out.println("######  newTrill3Library() (double based, less rounding precision) #####");
        System.out.println(" xExpected: " + xExpected + "   |   yExpected: " + yExpected);
        System.out.println(" xResult:   " + resultNewTrill3Library.getFirst() + "   |   yResult:   " + resultNewTrill3Library.getSecond());


    }


}
