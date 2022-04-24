package ch.zhaw.integration.beacons.rest.route.trilateration.helper;

import ch.zhaw.integration.beacons.entities.beacon.Beacon;
import ch.zhaw.integration.beacons.entities.beacon.BeaconRepository;
import ch.zhaw.integration.beacons.entities.signal.Signal;
import ch.zhaw.integration.beacons.entities.signal.SignalRepository;
import ch.zhaw.integration.beacons.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TrilaterationSignalPreprocessorTest {

    private TrilaterationSignalPreprocessor sut;

    @Mock
    private BeaconRepository beaconRepository;
    @Mock
    private SignalRepository signalRepository;

    private SimpleDateFormat sdf;

    @BeforeEach
    public void setUp() {
        sut = new TrilaterationSignalPreprocessor(new String[]{"0", "-1", "-3"}, "4", "2", "0", beaconRepository, signalRepository);
        sdf = new SimpleDateFormat(DateUtils.YYYY_MM_DD_HH_MM_SS_SSS);
    }

    @Test
    public void preprocessTEST() throws ParseException {
        // Given
        List<Signal> rawSignals = new ArrayList<>();
        Date t1 = sdf.parse("2022-04-21 18:00:00.000");
        Date t2 = sdf.parse("2022-04-21 18:00:00.200");
        Date t3 = sdf.parse("2022-04-21 18:00:00.400");
        Date t4 = sdf.parse("2022-04-21 18:00:00.600");
        Date t5 = sdf.parse("2022-04-21 18:00:00.800");

        // Signals T1
        Signal signalT1Sbb1Ug2 = createSignal(rawSignals, t1, "1", "1", "-2", true, false, t1, t5);  // Result   T1
        Signal signalT1Sbb2Ug2 = createSignal(rawSignals, t1, "1", "2", "-2", true, false, t1, t5);  // Result   T1
        Signal signalT1Sbb3Ug2 = createSignal(rawSignals, t1, "1", "3", "-2", true, false, t1, t5);  // Result   T1
        createSignal(rawSignals, t1, "1", "4", "-3", true, false, t1, t5);  // Ignored  T1  Not Matching floor
        createSignal(rawSignals, t1, "9", "999", null, false, false, t1, t5);  // Ignored  T1  Not Sbb Beacon
        createSignal(rawSignals, t1, "9", "998", null, false, false, t1, t5);  // Ignored  T1  Not Sbb Beacon
        // Signals T2 -> Result = 3
        Signal signalT2Sbb1Ug2 = createSignal(rawSignals, t2, "1", "11", "-2", true, false, t1, t5);  // Result   T2
        Signal signalT2Sbb2Ug2 = createSignal(rawSignals, t2, "1", "22", "-2", true, false, t1, t5);  // Result   T2
        Signal signalT2Sbb3Ug2 = createSignal(rawSignals, t2, "1", "33", "-2", true, false, t1, t5);  // Result   T2
        createSignal(rawSignals, t2, "1", "44", "-1", true, false, t1, t5);  // Ignored  T2  Not Matching floor
        createSignal(rawSignals, t2, "99", "999", null, false, false, t1, t5);  // Ignored  T2  Not Sbb Beacon
        createSignal(rawSignals, t2, "99", "998", null, false, false, t1, t5);  // Ignored  T2  Not Sbb Beacon
        // Signals T3 -> Result = 5
        Signal signalT3Sbb1Ug2 = createSignal(rawSignals, t3, "1", "111", "-2", true, false, t1, t5);  // Result   T3
        Signal signalT3Sbb2Ug2 = createSignal(rawSignals, t3, "1", "222", "-2", true, false, t1, t5);  // Result   T3
        Signal signalT3Sbb3Ug2 = createSignal(rawSignals, t3, "1", "333", "-2", true, false, t1, t5);  // Result   T3
        Signal signalT3Sbb4Ug2 = createSignal(rawSignals, t3, "1", "555", "-2", true, false, t1, t5);  // Result   T3
        Signal signalT3Sbb5Ug2 = createSignal(rawSignals, t3, "1", "666", "-2", true, false, t1, t5);  // Result   T3
        createSignal(rawSignals, t3, "1", "444", "0", true, false, t1, t5);  // Ignored  T3  Not Matching floor
        createSignal(rawSignals, t2, "999", "999", null, false, false, t1, t5);  // Ignored  T3  Not Sbb Beacon
        createSignal(rawSignals, t2, "999", "998", null, false, false, t1, t5);  // Ignored  T3  Not Sbb Beacon
        // Signals T4 -> Result = 2
        Signal signalT4Sbb1Ug2 = createSignal(rawSignals, t4, "1", "1111", "-2", true, false, t1, t5);  // Result   T4
        Signal signalT4Sbb2Ug2 = createSignal(rawSignals, t4, "1", "2222", "-2", true, false, t1, t5);  // Result   T4
        createSignal(rawSignals, t3, "1", "4444", "0", true, false, t1, t5);  // Ignored  T3  Not Matching floor
        createSignal(rawSignals, t4, "9999", "999", null, false, false, t1, t5);  // Ignored  T3  Not Sbb Beacon
        createSignal(rawSignals, t4, "9999", "998", null, false, false, t1, t5);  // Ignored  T3  Not Sbb Beacon
        // Signals T5 -> Result = 0
        createSignal(rawSignals, t5, "1", "44444", "0", true, false, t1, t5);  // Ignored  T3  Not Matching floor
        createSignal(rawSignals, t5, "9999", "999", null, false, false, t1, t5);  // Ignored  T3  Not Sbb Beacon
        createSignal(rawSignals, t5, "9999", "998", null, false, false, t1, t5);  // Ignored  T3  Not Sbb Beacon
        // When
        Map<String, List<Signal>> result = sut.preprocess(rawSignals, t1, t5);
        // Then
        assertEquals(4, result.entrySet().size()); // assert 4 groups of signals grouped by timestamp T1, T2, T3, T4
        // assert result 3 signals for T1
        List<Signal> t1Result = result.get(sdf.format(t1));
        assertEquals(3, t1Result.size());
        assertTrue(t1Result.containsAll(List.of(signalT1Sbb1Ug2, signalT1Sbb2Ug2, signalT1Sbb3Ug2)));
        // assert result 3 signals for T2
        List<Signal> t2Result = result.get(sdf.format(t2));
        assertEquals(3, t2Result.size());
        assertTrue(t2Result.containsAll(List.of(signalT2Sbb1Ug2, signalT2Sbb2Ug2, signalT2Sbb3Ug2)));
        // assert result 5 signals for T3
        List<Signal> t3Result = result.get(sdf.format(t3));
        assertEquals(5, t3Result.size());
        assertTrue(t3Result.containsAll(List.of(signalT3Sbb1Ug2, signalT3Sbb2Ug2, signalT3Sbb3Ug2, signalT3Sbb4Ug2, signalT3Sbb5Ug2)));
        // assert result 2 signals for T4
        List<Signal> t4Result = result.get(sdf.format(t4));
        assertEquals(2, t4Result.size());
        assertTrue(t4Result.containsAll(List.of(signalT4Sbb1Ug2,signalT4Sbb2Ug2)));
    }

    @Test
    public void connectSignalsWithKnownSbbBeaconsTESTmatch() {
        // Given
        String major = "123";
        String minor = "321";
        Signal signal = new Signal();
        signal.setMajor(major);
        signal.setMinor(minor);
        Beacon beacon = mock(Beacon.class);
        given(beaconRepository.findBeaconByMajorAndMinor(major, minor)).willReturn(beacon);
        // When
        sut.connectSignalsWithKnownSbbBeacons(signal);
        // Then
        assertEquals(beacon, signal.getBeacon());
    }

    @Test
    public void connectSignalsWithKnownSbbBeaconsTESTnoMatch() {
        // Given
        String major = "123";
        String minor = "321";
        Signal signal = new Signal();
        signal.setMajor(major);
        signal.setMinor(minor);
        given(beaconRepository.findBeaconByMajorAndMinor(major, minor)).willReturn(null);
        // When
        sut.connectSignalsWithKnownSbbBeacons(signal);
        // Then
        assertNull(signal.getBeacon());
    }

    @Test
    public void enrichSignalWithCalculatedDistanceTESTenvironmentalFactor2() {
        // Given
        sut = new TrilaterationSignalPreprocessor(new String[]{"0", "-1", "-3"}, "2", "1", "20", beaconRepository, signalRepository);
        Integer txPower = -69;

        Signal signal035meter = new Signal();
        signal035meter.setRssi(-60);
        signal035meter.setRunningAverageRssi(-60.0);
        signal035meter.setTxPower(txPower);

        Signal signal1meter = new Signal();
        signal1meter.setRssi(-69);
        signal1meter.setRunningAverageRssi(-69.0);
        signal1meter.setTxPower(txPower);

        Signal signal354meter = new Signal();
        signal354meter.setRssi(-80);
        signal354meter.setRunningAverageRssi(-80.0);
        signal354meter.setTxPower(txPower);
        // When
        sut.enrichSignalWithCalculatedDistance(signal035meter);
        sut.enrichSignalWithCalculatedDistance(signal1meter);
        sut.enrichSignalWithCalculatedDistance(signal354meter);
        // Then
        assertEquals(BigDecimal.valueOf(0.35481338923357547), signal035meter.getCalculatedDistance());
        assertEquals(BigDecimal.valueOf(0.35481338923357547), signal035meter.getCalculatedDistanceSlidingWindow());
        assertEquals(BigDecimal.valueOf(1.0), signal1meter.getCalculatedDistance());
        assertEquals(BigDecimal.valueOf(1.0), signal1meter.getCalculatedDistanceSlidingWindow());
        assertEquals(BigDecimal.valueOf(3.548133892335755), signal354meter.getCalculatedDistance());
        assertEquals(BigDecimal.valueOf(3.548133892335755), signal354meter.getCalculatedDistanceSlidingWindow());
    }

    @Test
    public void isFloorNotExcludedTESTignoreEgUg1Ug3() {
        // Given
        Beacon beaconEg = new Beacon();
        beaconEg.setFloor("0");
        Beacon beaconUg1 = new Beacon();
        beaconUg1.setFloor("-1");
        Beacon beaconUg2 = new Beacon();
        beaconUg2.setFloor("-2");
        Beacon beaconUg3 = new Beacon();
        beaconUg3.setFloor("-3");
        // When
        boolean resultEg = sut.isFloorNotExcluded(beaconEg);
        boolean resultUg1 = sut.isFloorNotExcluded(beaconUg1);
        boolean resultUg2 = sut.isFloorNotExcluded(beaconUg2);
        boolean resultUg3 = sut.isFloorNotExcluded(beaconUg3);
        // Then
        assertFalse(resultEg);
        assertFalse(resultUg1);
        assertTrue(resultUg2);
        assertFalse(resultUg3);
    }

    @Test
    public void minBeaconSignalCountReachedInPeriodTESTperiodPropertyNot0() throws ParseException {
        // Given
        sut = new TrilaterationSignalPreprocessor(new String[]{"0", "-1", "-3"}, "4", "2", "20", beaconRepository, signalRepository);
        Date routeStart = sdf.parse("2022-04-21 18:27:00.000");
        Date routeEnd = sdf.parse("2022-04-21 18:30:00.000");
        Date signalTimestamp =  sdf.parse("2022-04-21 18:28:30.200");
        Date t1 =  sdf.parse("2022-04-21 18:28:20.200");
        Date t2 =  sdf.parse("2022-04-21 18:28:40.200");
        Signal signal = mock(Signal.class);
        Beacon beacon = mock(Beacon.class);
        given(signal.getBeacon()).willReturn(beacon);
        given(signal.getSignalTimestamp()).willReturn(signalTimestamp);
        given(signalRepository.countByBeaconAndSignalTimestampBetween(beacon, t1, t2)).willReturn(1L);
        // When
        boolean result = sut.minBeaconSignalCountReachedInPeriod(signal, routeStart, routeEnd);
        // Then
        verify(signalRepository, times(1)).countByBeaconAndSignalTimestampBetween(beacon, t1, t2);
        assertFalse(result);
    }

    @Test
    public void minBeaconSignalCountReachedInPeriodTESTperiodProperty0() throws ParseException {
        // Given
        sut = new TrilaterationSignalPreprocessor(new String[]{"0", "-1", "-3"}, "4", "2", "0", beaconRepository, signalRepository);
        Date routeStart = sdf.parse("2022-04-21 18:27:00.000");
        Date routeEnd = sdf.parse("2022-04-21 18:30:00.000");
        Signal signal = mock(Signal.class);
        Beacon beacon = mock(Beacon.class);
        given(signal.getBeacon()).willReturn(beacon);
        given(signalRepository.countByBeaconAndSignalTimestampBetween(beacon, routeStart, routeEnd)).willReturn(3L);
        // When
        boolean result = sut.minBeaconSignalCountReachedInPeriod(signal, routeStart, routeEnd);
        // Then
        verify(signalRepository, times(1)).countByBeaconAndSignalTimestampBetween(beacon, routeStart, routeEnd);
        assertTrue(result);
    }

    private Signal createSignal(List<Signal> signals, Date signalTimestamp, String major, String minor, String floor, boolean isSbbBeacon, boolean withBeacon, Date routeStart, Date routeEnd) {
        Signal signal = new Signal();
        signal.setSignalTimestamp(signalTimestamp);
        signal.setTxPower(-69);
        signal.setRssi(-60);
        signal.setRunningAverageRssi(-60.0);
        signal.setMajor(major);
        signal.setMinor(minor);
        if(isSbbBeacon) {
            Beacon beacon = new Beacon();
            beacon.setFloor(floor);
            beacon.setMajor(major);
            beacon.setMinor(minor);
            if(withBeacon) {
                beacon.setId(Long.valueOf(minor));
                signal.setBeacon(beacon);
            }
            given(beaconRepository.findBeaconByMajorAndMinor(major, minor)).willReturn(beacon);
            if (floor.equals("-2")) {
                given(signalRepository.countByBeaconAndSignalTimestampBetween(beacon, routeStart, routeEnd)).willReturn(3L);
            }
        } else {
            given(beaconRepository.findBeaconByMajorAndMinor(major, minor)).willReturn(null);
        }
        signals.add(signal);
        return signal;
    }
}
