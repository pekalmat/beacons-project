package ch.zhaw.integration.beacons.rest.route.trilateration.calculator;

import ch.zhaw.integration.beacons.rest.route.trilateration.calculator.basic.NoSmoothingPositionCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AbstractPositionCalculatorTest {

    private AbstractPositionCalculator sut;

    @BeforeEach
    public void setUp() {
        sut = new NoSmoothingPositionCalculator(null, null);
    }

    @Test
    public void trilateratePositionCoordinatesTEST() {
        // TODO test and investigate different calculation methods
    }


}
