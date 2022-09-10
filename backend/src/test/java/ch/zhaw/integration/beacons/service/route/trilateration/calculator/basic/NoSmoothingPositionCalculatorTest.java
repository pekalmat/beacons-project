package ch.zhaw.integration.beacons.service.route.trilateration.calculator.basic;

import ch.zhaw.integration.beacons.entities.beacon.Beacon;
import ch.zhaw.integration.beacons.entities.position.Position;
import ch.zhaw.integration.beacons.entities.route.Route;
import ch.zhaw.integration.beacons.entities.signal.Signal;
import ch.zhaw.integration.beacons.service.route.trilateration.calculator.algorithm.StackoverflowTrilaterationAlgorithm;
import ch.zhaw.integration.beacons.service.route.trilateration.helper.TrilaterationSignalPartitioner;
import ch.zhaw.integration.beacons.service.route.trilateration.helper.comparator.SignalCalculatedDistanceComparator;
import ch.zhaw.integration.beacons.utils.CalculationMethod;
import ch.zhaw.integration.beacons.utils.Calculator;
import ch.zhaw.integration.beacons.utils.DateUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NoSmoothingPositionCalculatorTest {

    private NoSmoothingPositionCalculator sut;

    @Mock
    private TrilaterationSignalPartitioner trilaterationSignalPartitioner;
    @Mock
    private SignalCalculatedDistanceComparator signalCalculatedDistanceComparator;

    private StackoverflowTrilaterationAlgorithm trilaterationAlgorithm;
    private Calculator calculator;
    @BeforeEach
    public void setUp() {
        calculator = new Calculator(30);
        trilaterationAlgorithm = new StackoverflowTrilaterationAlgorithm(calculator);
        sut = new NoSmoothingPositionCalculator(trilaterationAlgorithm, trilaterationSignalPartitioner, signalCalculatedDistanceComparator);
    }

    @Test
    public void calculatePositionsTESToptimumSimpleMockData() throws ParseException {
        // Given
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.YYYY_MM_DD_HH_MM_SS_SSS);
        Map<String, List<Signal>> signalsMap = new HashMap<>();
        // Beacon
        Beacon beacon1 = createBeacon(BigDecimal.valueOf(0.0), BigDecimal.valueOf(0.0));
        Beacon beacon2 = createBeacon(BigDecimal.valueOf(0.8), BigDecimal.valueOf(2.0));
        Beacon beacon3 = createBeacon(BigDecimal.valueOf(2.0), BigDecimal.valueOf(0.0));
        //Signals Position T1
        Date t1 = sdf.parse("2022-04-21 18:00:00.000");
        Signal signal1T1 = createSignal(beacon1, null,BigDecimal.valueOf(1.41421356237), null);  // Result   T1
        Signal signal2T1 = createSignal(beacon2, null, BigDecimal.valueOf(1.01980390272), null);  // Result   T1
        Signal signal3T1 = createSignal(beacon3, null, BigDecimal.valueOf(1.41421356237), null);  // Result   T1
        List<Signal> signalsT1 = Arrays.asList(signal1T1, signal2T1, signal3T1);
        signalsMap.put(sdf.format(t1), signalsT1);
        ImmutableTriple<Signal, Signal, Signal> triple = ImmutableTriple.of(signal1T1, signal2T1, signal3T1);
        given(trilaterationSignalPartitioner.createTriplesOfClosestSignals(signalsMap, signalCalculatedDistanceComparator)).willReturn(List.of(triple));
        // When
        List<Position> result = sut.calculatePositions(signalsMap, new Route());
        // Then
        assertEquals(1, result.size());
        //        assertEquals(new BigDecimal(1.0, calculator.getMathContext()), result.get(0).getxCoordinate());
        //        assertEquals(new BigDecimal(1.0, calculator.getMathContext()), result.get(0).getyCoordinate());
    }

    @Test
    public void prepareSignalsTriplesForCalculationTEST() {
        // Given
        Map<String, List<Signal>> signalsMap = new HashMap<>();
        // When
        sut.prepareSignalsTriplesForCalculation(signalsMap);
        // Then
        verify(trilaterationSignalPartitioner, times(1)).createTriplesOfClosestSignals(signalsMap, signalCalculatedDistanceComparator);
    }

    @Test
    public void getCalculationMethodTEST() {
        // Given
        // When
        CalculationMethod result = sut.getCalculationMethod();
        // Then
        assertEquals(CalculationMethod.TRILATERATION_NO_SMOOTHING, result);
    }

    @Test
    public void getDistanceLeftTEST() {
        // Given
        Signal left = createSignal(null, BigDecimal.valueOf(1.11), BigDecimal.valueOf(2.22), BigDecimal.valueOf(3.33));
        Signal middle = createSignal(null, BigDecimal.valueOf(4.44), BigDecimal.valueOf(5.55), BigDecimal.valueOf(6.66));
        Signal right = createSignal(null, BigDecimal.valueOf(7.77), BigDecimal.valueOf(8.88), BigDecimal.valueOf(9.99));
        ImmutableTriple<Signal, Signal, Signal> triple = ImmutableTriple.of(left, middle, right);
        // When
        BigDecimal result = sut.getDistanceLeft(triple);
        // Then
        assertEquals(BigDecimal.valueOf(2.22), result);
    }

    @Test
    public void getDistanceRightTEST() {
        // Given
        Signal left = createSignal(null, BigDecimal.valueOf(1.11), BigDecimal.valueOf(2.22), BigDecimal.valueOf(3.33));
        Signal middle = createSignal(null, BigDecimal.valueOf(4.44), BigDecimal.valueOf(5.55), BigDecimal.valueOf(6.66));
        Signal right = createSignal(null, BigDecimal.valueOf(7.77), BigDecimal.valueOf(8.88), BigDecimal.valueOf(9.99));
        ImmutableTriple<Signal, Signal, Signal> triple = ImmutableTriple.of(left, middle, right);
        // When
        BigDecimal result = sut.getDistanceRight(triple);
        // Then
        assertEquals(BigDecimal.valueOf(8.88), result);
    }

    @Test
    public void getDistanceMiddleTEST() {
        // Given
        Signal left = createSignal(null,BigDecimal.valueOf(1.11), BigDecimal.valueOf(2.22), BigDecimal.valueOf(3.33));
        Signal middle = createSignal(null, BigDecimal.valueOf(4.44), BigDecimal.valueOf(5.55), BigDecimal.valueOf(6.66));
        Signal right = createSignal(null,BigDecimal.valueOf(7.77), BigDecimal.valueOf(8.88), BigDecimal.valueOf(9.99));
        ImmutableTriple<Signal, Signal, Signal> triple = ImmutableTriple.of(left, middle, right);
        // When
        BigDecimal result = sut.getDistanceMiddle(triple);
        // Then
        assertEquals(BigDecimal.valueOf(5.55), result);
    }

    private Signal createSignal(Beacon beacon, BigDecimal distanceLibrary, BigDecimal distanceSimpleRssi, BigDecimal distanceSlidingWindow) {
        Signal signal = new Signal();
        signal.setBeacon(beacon);
        signal.setDistance(distanceLibrary);
        signal.setCalculatedDistance(distanceSimpleRssi);
        signal.setCalculatedDistanceSlidingWindow(distanceSlidingWindow);
        return signal;
    }

    private Beacon createBeacon(BigDecimal xCoordinate, BigDecimal yCoordinate) {
        Beacon beacon = new Beacon();
        beacon.setFloor("-2");
        beacon.setxCoordinate(xCoordinate);
        beacon.setyCoordinate(yCoordinate);
        return beacon;
    }

}
