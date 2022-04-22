package ch.zhaw.integration.beacons.rest.route.trilateration.helper;

import ch.zhaw.integration.beacons.entities.beacon.Beacon;
import ch.zhaw.integration.beacons.entities.signal.Signal;
import ch.zhaw.integration.beacons.rest.route.trilateration.helper.comparator.SignalCalculatedDistanceComparator;
import ch.zhaw.integration.beacons.utils.DateUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class TrilaterationSignalPartitionerTest {

    private TrilaterationSignalPartitioner sut;

    @BeforeEach
    public void setUp() {
        sut = new TrilaterationSignalPartitioner();
    }

    @Test
    public void createTriplesOfClosestSignalsTEST() throws ParseException {
        // Given
        SignalCalculatedDistanceComparator distanceComparator = new SignalCalculatedDistanceComparator();
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.YYYY_MM_DD_HH_MM_SS_SSS);
        Map<String, List<Signal>> signalsMap = new HashMap<>();
        //Beacons
        Beacon beacon1 = createBeacon("1", "1");
        Beacon beacon2 = createBeacon("1", "2");
        Beacon beacon3 = createBeacon("1", "3");
        Beacon beacon4 = createBeacon("2", "1");
        Beacon beacon5 = createBeacon("2", "2");
        //Signals T1 -> Result Triple 3 of 3
        Date t1 = sdf.parse("2022-04-21 18:00:00.000");
        Signal signal1T1 = createSignal(beacon1, 3.0);  // Result   T1
        Signal signal2T1 = createSignal(beacon2, 3.0);  // Result   T1
        Signal signal3T1 = createSignal(beacon3, 3.0);  // Result   T1
        List<Signal> signalsT1 = Arrays.asList(signal1T1, signal2T1, signal3T1);
        signalsMap.put(sdf.format(t1), signalsT1);
        //Signals T2 -> Result Triple 3 of 5 (all different beacons -> selection by distance)
        Date t2 = sdf.parse("2022-04-21 18:00:00.200");
        Signal signal1T2 = createSignal(beacon1, 10.0);  // Ignored
        Signal signal2T2 = createSignal(beacon2, 1.0);  // Result   T2
        Signal signal3T2 = createSignal(beacon3, 2.0);  // Result   T2
        Signal signal4T2 = createSignal(beacon4, 4.0);  // Ignored
        Signal signal5T2 = createSignal(beacon5, 3.0);  // Result   T2
        List<Signal> signalsT2 = Arrays.asList(signal1T2, signal2T2, signal3T2, signal4T2, signal5T2);
        signalsMap.put(sdf.format(t2), signalsT2);
        //Signals T3 -> Result Triple 0 of 3 (2 different beacons -> no trilateration possible)
        Date t3 = sdf.parse("2022-04-21 18:00:00.400");
        Signal signal1T3 = createSignal(beacon1, 3.0);  // Result   T1
        Signal signal2T3 = createSignal(beacon2, 3.0);  // Result   T1
        Signal signal3T3 = createSignal(beacon2, 3.0);  // Result   T1
        List<Signal> signalsT3 = Arrays.asList(signal1T3, signal2T3, signal3T3);
        signalsMap.put(sdf.format(t3), signalsT3);
        // When
        List<ImmutableTriple<Signal, Signal, Signal>> result = sut.createTriplesOfClosestSignals(signalsMap, distanceComparator);
        // Then
        assertEquals(2, result.size());
        // assert triple for T1
        assertEquals(signal1T1, result.get(0).getLeft());
        assertEquals(signal2T1, result.get(0).getMiddle());
        assertEquals(signal3T1, result.get(0).getRight());
        // assert triple for T2
        assertEquals(signal2T2, result.get(1).getLeft());
        assertEquals(signal3T2, result.get(1).getMiddle());
        assertEquals(signal5T2, result.get(1).getRight());
    }

    private Signal createSignal(Beacon beacon, Double calculatedDistance) {
        Signal signal = new Signal();
        signal.setMajor(beacon.getMajor());
        signal.setMinor(beacon.getMinor());
        signal.setCalculatedDistance(calculatedDistance);
        signal.setBeacon(beacon);
        return signal;
    }

    private Beacon createBeacon(String major, String minor) {
        Beacon beacon = new Beacon();
        beacon.setMajor(major);
        beacon.setMinor(minor);
        return beacon;
    }
}
