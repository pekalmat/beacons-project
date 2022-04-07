package ch.zhaw.integration.beacons.rest.signal;

import ch.zhaw.integration.beacons.entities.beacon.Beacon;
import ch.zhaw.integration.beacons.entities.beacon.BeaconRepository;
import ch.zhaw.integration.beacons.entities.device.Device;
import ch.zhaw.integration.beacons.entities.device.DeviceRepository;
import ch.zhaw.integration.beacons.entities.position.Position;
import ch.zhaw.integration.beacons.entities.position.PositionRepository;
import ch.zhaw.integration.beacons.entities.signal.Signal;
import ch.zhaw.integration.beacons.entities.signal.SignalDto;
import ch.zhaw.integration.beacons.entities.signal.SignalRepository;
import ch.zhaw.integration.beacons.entities.signal.SignalToSignalDtoMapper;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class SignalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignalService.class);

    private final SignalRepository signalRepository;
    private final BeaconRepository beaconRepository;
    private final PositionRepository positionRepository;
    private final DeviceRepository deviceRepository;
    private final SignalToSignalDtoMapper signalMapper;

    public SignalService(
            SignalRepository signalRepository,
            BeaconRepository beaconRepository,
            PositionRepository positionRepository,
            DeviceRepository deviceRepository) {
        this.signalRepository = signalRepository;
        this.beaconRepository = beaconRepository;
        this.positionRepository = positionRepository;
        this.deviceRepository = deviceRepository;
        this.signalMapper = Mappers.getMapper(SignalToSignalDtoMapper.class);
    }

    List<SignalDto> storeNewSignals(List<SignalDto> signalDtoList) {
        List<Signal> newSignals = new ArrayList<>();
        for(SignalDto signalDto : signalDtoList) {
            Signal signal = signalMapper.mapSignalDtoToSignal(signalDto);
            Device device = deviceRepository.findByFingerPrint(signalDto.getDeviceFingerPrint());
            signal.setDevice(device);
            newSignals.add(signal);
        }
        List<Signal> persisted = signalRepository.saveAll(newSignals);
        return signalMapper.mapSignalListToSignalDtoList(persisted);
    }

    // TRILATERATION
    void matchSignalsWithBeaconsAndCalculateRoute() {
        Date triggerTime = Calendar.getInstance().getTime();
        // Get All Signals
        List<Signal> signals = signalRepository.findAll();
        // Sort by SignalTimestamp
        Collections.sort(signals);
        // Connect Signals with Known Beacons
        signals = connectSignalsWithKnownSbbBeacons(signals);
        // Partitions of 3 Signals of 3 Different Beacons for Trilateration
        List<ImmutableTriple<Signal, Signal, Signal>> partitionsOf3 = createPartitionsOf3ClosestSignalsOf3DifferentBeaconsForTrilateration(signals);
        // Do Trilateration -> Calculate Position for Each Partition
        List<Position> positions = new ArrayList<>();
        for (ImmutableTriple<Signal, Signal, Signal> partition : partitionsOf3) {
            Position position = new Position();
            position.setCalculationTriggerTime(triggerTime);
            setFloorDetailsOnPosition(position, partition);
            Pair<Double, Double> estimatedXAndYPositionBasedOnLibraryDistance = trilateratePositionCoordinatesBasedOnLibraryDistance(partition);
            position.setxCoordinateBasedOnLibraryDistance(estimatedXAndYPositionBasedOnLibraryDistance.getFirst());
            position.setyCoordinateBasedOnLibraryDistance(estimatedXAndYPositionBasedOnLibraryDistance.getSecond());
            Pair<Double, Double> estimatedXAndYPositionBasedOnOwnDistanceCalculation = trilateratePositionCoordinatesBasedOnOwnDistanceCalculation(partition);
            position.setxCoordinate(estimatedXAndYPositionBasedOnOwnDistanceCalculation.getFirst());
            position.setyCoordinate(estimatedXAndYPositionBasedOnOwnDistanceCalculation.getSecond());
            position.setCalculationMethod("no signal smoothing - simple trilateration");
            positions.add(position);
        }
        signalRepository.saveAll(signals);
        positionRepository.saveAll(positions);
        LOGGER.info(positions.size() + " Positions Generated and Persisted");
    }

    private Pair<Double, Double> trilateratePositionCoordinatesBasedOnOwnDistanceCalculation(ImmutableTriple<Signal, Signal, Signal> partition) {
        Pair<Double, Double> location1 = Pair.of(partition.getLeft().getBeacon().getxCoordinate(), partition.getLeft().getBeacon().getyCoordinate());
        double distance1 = calculateDistanceOfSignal(partition.getLeft());
        Pair<Double, Double> location2 = Pair.of(partition.getMiddle().getBeacon().getxCoordinate(), partition.getMiddle().getBeacon().getyCoordinate());
        double distance2 = calculateDistanceOfSignal(partition.getMiddle());
        Pair<Double, Double> location3 = Pair.of(partition.getRight().getBeacon().getxCoordinate(), partition.getRight().getBeacon().getyCoordinate());
        double distance3 = calculateDistanceOfSignal(partition.getRight());
        return trilateratePositionCoordinates(location1, location2, location3, distance1, distance2, distance3);
    }

    private double calculateDistanceOfSignal(Signal signal) {
        //TODO: check which method is correct and adjust Rssi-Distance calculation Documentation
        // 1. first Simple (as in documentation)
        double distance1 = Double.parseDouble(String.valueOf(signal.getTxPower())) / Double.parseDouble(String.valueOf(signal.getRssi()));
        signal.setCalculatedDistance(distance1);
        // 2. second (as in Excel SpreadSheet) source https://iotandelectronics.wordpress.com/2016/10/07/how-to-calculate-distance-from-the-rssi-value-of-the-ble-beacon/
        int n = 2; // N (Constant depends on the Environmental factor. Range 2-4)
        double distance2 = Math.pow(10, ( Double.parseDouble(String.valueOf(Math.subtractExact(signal.getTxPower(), signal.getRssi()))) / (10 * n)));
        return distance2;
    }

    private Pair<Double, Double> trilateratePositionCoordinatesBasedOnLibraryDistance(ImmutableTriple<Signal, Signal, Signal> partition) {
        Pair<Double, Double> location1 = Pair.of(partition.getLeft().getBeacon().getxCoordinate(), partition.getLeft().getBeacon().getyCoordinate());
        double distance1 = partition.getLeft().getDistance();
        Pair<Double, Double> location2 = Pair.of(partition.getMiddle().getBeacon().getxCoordinate(), partition.getMiddle().getBeacon().getyCoordinate());
        double distance2 = partition.getMiddle().getDistance();
        Pair<Double, Double> location3 = Pair.of(partition.getRight().getBeacon().getxCoordinate(), partition.getRight().getBeacon().getyCoordinate());
        double distance3 = partition.getRight().getDistance();
        return trilateratePositionCoordinates(location1, location2, location3, distance1, distance2, distance3);
    }


    private Pair<Double, Double> trilateratePositionCoordinates(
            Pair<Double, Double> location1,
            Pair<Double, Double> location2,
            Pair<Double, Double> location3,
            double distance1,
            double distance2,
            double distance3) {
        //DECLARE VARIABLES
        double[] P1   = new double[2];
        double[] P2   = new double[2];
        double[] P3   = new double[2];
        double[] ex   = new double[2];
        double[] ey   = new double[2];
        double[] p3p1 = new double[2];
        double jval  = 0;
        double temp  = 0;
        double ival  = 0;
        double p3p1i = 0;
        double triptx;
        double tripty;
        double xval;
        double yval;
        double t1;
        double t2;
        double t3;
        double t;
        double exx;
        double d;
        double eyy;

        //TRANSALTE POINTS TO VECTORS
        //POINT 1
        P1[0] = location1.getFirst();
        P1[1] = location1.getSecond();
        //POINT 2
        P2[0] = location2.getFirst();
        P2[1] = location2.getSecond();
        //POINT 3
        P3[0] = location3.getFirst();
        P3[1] = location3.getSecond();

        //TRANSFORM THE METERS VALUE FOR THE MAP UNIT
        //DISTANCE BETWEEN POINT 1 AND MY LOCATION
        distance1 = (distance1 / 100000);
        //DISTANCE BETWEEN POINT 2 AND MY LOCATION
        distance2 = (distance2 / 100000);
        //DISTANCE BETWEEN POINT 3 AND MY LOCATION
        distance3 = (distance3 / 100000);

        for (int i = 0; i < P1.length; i++) {
            t1   = P2[i];
            t2   = P1[i];
            t    = t1 - t2;
            temp += (t*t);
        }
        d = Math.sqrt(temp);
        for (int i = 0; i < P1.length; i++) {
            t1    = P2[i];
            t2    = P1[i];
            exx   = (t1 - t2)/(Math.sqrt(temp));
            ex[i] = exx;
        }
        for (int i = 0; i < P3.length; i++) {
            t1      = P3[i];
            t2      = P1[i];
            t3      = t1 - t2;
            p3p1[i] = t3;
        }
        for (int i = 0; i < ex.length; i++) {
            t1 = ex[i];
            t2 = p3p1[i];
            ival += (t1*t2);
        }
        for (int  i = 0; i < P3.length; i++) {
            t1 = P3[i];
            t2 = P1[i];
            t3 = ex[i] * ival;
            t  = t1 - t2 -t3;
            p3p1i += (t*t);
        }
        for (int i = 0; i < P3.length; i++) {
            t1 = P3[i];
            t2 = P1[i];
            t3 = ex[i] * ival;
            eyy = (t1 - t2 - t3)/Math.sqrt(p3p1i);
            ey[i] = eyy;
        }
        for (int i = 0; i < ey.length; i++) {
            t1 = ey[i];
            t2 = p3p1[i];
            jval += (t1*t2);
        }
        xval = (Math.pow(distance1, 2) - Math.pow(distance2, 2) + Math.pow(d, 2))/(2*d);
        yval = ((Math.pow(distance1, 2) - Math.pow(distance3, 2) + Math.pow(ival, 2) + Math.pow(jval, 2))/(2*jval)) - ((ival/jval)*xval);

        t1 = location1.getFirst();
        t2 = ex[0] * xval;
        t3 = ey[0] * yval;
        triptx = t1 + t2 + t3;

        t1 = location1.getSecond();
        t2 = ex[1] * xval;
        t3 = ey[1] * yval;
        tripty = t1 + t2 + t3;

        return Pair.of(triptx,tripty);
    }

    private void setFloorDetailsOnPosition(Position position, ImmutableTriple<Signal, Signal, Signal> triple) {
        String beaconSignalFloors = triple.getLeft().getBeacon().getFloor() + ","
                + triple.getMiddle().getBeacon().getFloor() + ","
                + triple.getRight().getBeacon().getFloor() + ",";
        double floorSum = Double.parseDouble(triple.getLeft().getBeacon().getFloor())
                + Double.parseDouble(triple.getMiddle().getBeacon().getFloor())
                + Double.parseDouble(triple.getLeft().getBeacon().getFloor());
        // floors are always 0 or negative therefore just adding floors and dividing after is ok in this szenario
        double estimatedFloor = floorSum / Double.parseDouble(String.valueOf(3));
        Integer estimatedFloorValue = (int) Math.round(estimatedFloor);
        position.setEstimatedFloor(estimatedFloorValue);
        position.setFloors(beaconSignalFloors);
    }

    private List<Signal> connectSignalsWithKnownSbbBeacons(List<Signal> signals) {
        List<Signal> matchedSignals = new ArrayList<>();
        for(Signal signal : signals) {
            Beacon matchingBeacon = beaconRepository.findBeaconByUuidAndMajorAndMinor(signal.getUuid(), signal.getMajor(), signal.getMinor());
            if(matchingBeacon != null) {
                signal.setBeacon(matchingBeacon);
                matchedSignals.add(signal);
            }
        }
        return matchedSignals;
    }

    private List<ImmutableTriple<Signal, Signal, Signal>> createPartitionsOf3ClosestSignalsOf3DifferentBeaconsForTrilateration(List<Signal> signals) {
        List<ImmutableTriple<Signal, Signal, Signal>> partitionsOf3 = new ArrayList<>();
        for(Signal signal : signals) {
            Signal signal2 = getClosestSignalOfAnotherBeacon(signals, List.of(signal));
            if (signal2 == null) {
                break;
            }
            Signal signal3 = getClosestSignalOfAnotherBeacon(signals, List.of(signal, signal2));
            if(signal3 == null) {
                break;
            }
            ImmutableTriple<Signal, Signal, Signal> triple = ImmutableTriple.of(signal, signal2, signal3);
            partitionsOf3.add(triple);
        }
        return distinctDuplicateTriples(partitionsOf3);
    }

    private List<ImmutableTriple<Signal, Signal, Signal>> distinctDuplicateTriples(List<ImmutableTriple<Signal, Signal, Signal>> triples) {
        List<ImmutableTriple<Signal, Signal, Signal>> result = new ArrayList<>();
        for(ImmutableTriple<Signal, Signal, Signal> triple : triples) {
            List<Signal> tripleList = List.of(triple.getLeft(), triple.getMiddle(), triple.getRight());
            boolean existsInResultList = false;
            for(ImmutableTriple<Signal, Signal, Signal> resultEntry : result) {
                List<Signal> resultEntryList = List.of(resultEntry.getLeft(), resultEntry.getMiddle(), resultEntry.getRight());
                if(resultEntryList.contains(triple.getLeft()) && resultEntryList.contains(triple.getMiddle()) && resultEntryList.contains(triple.getRight())) {
                    existsInResultList = true;
                }
            }
            if(!existsInResultList) {
                result.add(triple);
            }
        }
        return result;
    }

    private Signal getClosestSignalOfAnotherBeacon(List<Signal> signals, List<Signal> signalsToIgnore) {
        for(Signal signal : signals) {
            if(!ignoreSignal(signal, signalsToIgnore)){
                return signal;
            }
        }
        return null;
    }

    private boolean ignoreSignal(Signal signal, List<Signal> signalsToIgnore) {
        for(Signal toIgnore : signalsToIgnore) {
            if(sameBeacon(signal, toIgnore)) {
                return true;
            }
        }
        return false;
    }

    private boolean sameBeacon(Signal signal1, Signal signal2) {
        return signal1.getBeacon().equals(signal2.getBeacon());
    }
}
