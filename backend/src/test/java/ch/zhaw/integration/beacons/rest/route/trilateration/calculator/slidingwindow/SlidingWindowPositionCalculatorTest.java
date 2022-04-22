package ch.zhaw.integration.beacons.rest.route.trilateration.calculator.slidingwindow;

import ch.zhaw.integration.beacons.entities.beacon.Beacon;
import ch.zhaw.integration.beacons.entities.position.Position;
import ch.zhaw.integration.beacons.entities.route.Route;
import ch.zhaw.integration.beacons.entities.signal.Signal;
import ch.zhaw.integration.beacons.rest.route.trilateration.helper.TrilaterationSignalPartitioner;
import ch.zhaw.integration.beacons.rest.route.trilateration.helper.comparator.SignalCalculatedDistanceSlidingWindowComparator;
import ch.zhaw.integration.beacons.utils.CalculationMethod;
import ch.zhaw.integration.beacons.utils.DateUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
public class SlidingWindowPositionCalculatorTest {

    private SlidingWindowPositionCalculator sut;

    @Mock
    private TrilaterationSignalPartitioner trilaterationSignalPartitioner;
    @Mock
    private SignalCalculatedDistanceSlidingWindowComparator signalCalculatedDistanceSlidingWindowComparator;

    @BeforeEach
    public void setUp() {
        sut = new SlidingWindowPositionCalculator(trilaterationSignalPartitioner, signalCalculatedDistanceSlidingWindowComparator);
    }

    @Test
    public void calculatePositionsTESToptimumSimpleMockData() throws ParseException {
        // Given
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.YYYY_MM_DD_HH_MM_SS_SSS);
        Map<String, List<Signal>> signalsMap = new HashMap<>();
        // Beacon
        Beacon beacon1 = createBeacon(0.0, 0.0);
        Beacon beacon2 = createBeacon(0.8, 2.0);
        Beacon beacon3 = createBeacon(2.0, 0.0);
        //Signals Position T1
        Date t1 = sdf.parse("2022-04-21 18:00:00.000");
        Signal signal1T1 = createSignal(beacon1, null,null, 1.41421356237);  // Result   T1
        Signal signal2T1 = createSignal(beacon2, null, null, 1.01980390272);  // Result   T1
        Signal signal3T1 = createSignal(beacon3, null, null, 1.41421356237);  // Result   T1
        List<Signal> signalsT1 = Arrays.asList(signal1T1, signal2T1, signal3T1);
        signalsMap.put(sdf.format(t1), signalsT1);
        ImmutableTriple<Signal, Signal, Signal> triple = ImmutableTriple.of(signal1T1, signal2T1, signal3T1);
        given(trilaterationSignalPartitioner.createTriplesOfClosestSignals(signalsMap, signalCalculatedDistanceSlidingWindowComparator)).willReturn(List.of(triple));
        // When
        List<Position> result = sut.calculatePositions(signalsMap, new Route());
        // Then
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getxCoordinate());
        assertEquals(1, result.get(0).getxCoordinate());
    }

    @Test
    public void prepareSignalsTriplesForCalculationTEST() {
        // Given
        Map<String, List<Signal>> signalsMap = new HashMap<>();
        // When
        sut.prepareSignalsTriplesForCalculation(signalsMap);
        // Then
        verify(trilaterationSignalPartitioner, times(1)).createTriplesOfClosestSignals(signalsMap, signalCalculatedDistanceSlidingWindowComparator);
    }

    @Test
    public void getCalculationMethodTEST() {
        // Given
        // When
        CalculationMethod result = sut.getCalculationMethod();
        // Then
        assertEquals(CalculationMethod.TRILATERATION_SLIDING_WINDOW, result);
    }

    @Test
    public void getDistanceLeftTEST() {
        // Given
        Signal left = createSignal(null, 1.11, 2.22, 3.33);
        Signal middle = createSignal(null, 4.44, 5.55, 6.66);
        Signal right = createSignal(null, 7.77, 8.88, 9.99);
        ImmutableTriple<Signal, Signal, Signal> triple = ImmutableTriple.of(left, middle, right);
        // When
        double result = sut.getDistanceLeft(triple);
        // Then
        assertEquals(3.33, result);
    }

    @Test
    public void getDistanceRightTEST() {
        // Given
        Signal left = createSignal(null, 1.11, 2.22, 3.33);
        Signal middle = createSignal(null, 4.44, 5.55, 6.66);
        Signal right = createSignal(null, 7.77, 8.88, 9.99);
        ImmutableTriple<Signal, Signal, Signal> triple = ImmutableTriple.of(left, middle, right);
        // When
        double result = sut.getDistanceRight(triple);
        // Then
        assertEquals(9.99, result);
    }

    @Test
    public void getDistanceMiddleTEST() {
        // Given
        Signal left = createSignal(null, 1.11, 2.22, 3.33);
        Signal middle = createSignal(null, 4.44, 5.55, 6.66);
        Signal right = createSignal(null, 7.77, 8.88, 9.99);
        ImmutableTriple<Signal, Signal, Signal> triple = ImmutableTriple.of(left, middle, right);
        // When
        double result = sut.getDistanceMiddle(triple);
        // Then
        assertEquals(6.66, result);
    }

    private Signal createSignal(Beacon beacon, Double distanceLibrary, Double distanceSimpleRssi, Double distanceSlidingWindow) {
        Signal signal = new Signal();
        signal.setBeacon(beacon);
        signal.setDistance(distanceLibrary);
        signal.setCalculatedDistance(distanceSimpleRssi);
        signal.setCalculatedDistanceSlidingWindow(distanceSlidingWindow);
        return signal;
    }

    private Beacon createBeacon(Double xCoordinate, Double yCoordinate) {
        Beacon beacon = new Beacon();
        beacon.setFloor("-2");
        beacon.setxCoordinate(xCoordinate);
        beacon.setyCoordinate(yCoordinate);
        return beacon;
    }

}
