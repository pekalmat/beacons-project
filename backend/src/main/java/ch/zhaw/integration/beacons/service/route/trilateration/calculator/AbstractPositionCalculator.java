package ch.zhaw.integration.beacons.service.route.trilateration.calculator;

import ch.zhaw.integration.beacons.entities.position.Position;
import ch.zhaw.integration.beacons.entities.route.Route;
import ch.zhaw.integration.beacons.entities.signal.Signal;
import ch.zhaw.integration.beacons.service.route.trilateration.calculator.algorithm.StackoverflowTrilaterationAlgorithm;
import ch.zhaw.integration.beacons.utils.CalculationMethod;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public abstract class AbstractPositionCalculator {

    private final StackoverflowTrilaterationAlgorithm stackoverflowTrilaterator;

    protected AbstractPositionCalculator(StackoverflowTrilaterationAlgorithm stackoverflowTrilaterator) {
        this.stackoverflowTrilaterator = stackoverflowTrilaterator;
    }

    public abstract CalculationMethod getCalculationMethod();
    public abstract BigDecimal getDistanceLeft(ImmutableTriple<Signal, Signal, Signal> partition);
    public abstract BigDecimal getDistanceRight(ImmutableTriple<Signal, Signal, Signal> partition);
    public abstract BigDecimal getDistanceMiddle(ImmutableTriple<Signal, Signal, Signal> partition);
    public abstract List<ImmutableTriple<Signal, Signal, Signal>> prepareSignalsTriplesForCalculation(Map<String, List<Signal>> signalsMap);

    public List<Position> calculatePositions(Map<String, List<Signal>> signals, Route route) {
        List<Position> positions = new ArrayList<>();
        // Applying filter and craete triples for calculation
        List<ImmutableTriple<Signal, Signal, Signal>> positionSignals = prepareSignalsTriplesForCalculation(signals);
        // Trilaterate positions
        for (ImmutableTriple<Signal, Signal, Signal> position : positionSignals) {
            Position calculatedPosition = new Position();

            // prepare trilateration variables
            Pair<BigDecimal, BigDecimal> location1 = Pair.of(position.getLeft().getBeacon().getxCoordinate(), position.getLeft().getBeacon().getyCoordinate());
            Pair<BigDecimal, BigDecimal> location2 = Pair.of(position.getMiddle().getBeacon().getxCoordinate(), position.getMiddle().getBeacon().getyCoordinate());
            Pair<BigDecimal, BigDecimal> location3 = Pair.of(position.getRight().getBeacon().getxCoordinate(), position.getRight().getBeacon().getyCoordinate());
            BigDecimal distance1 = getDistanceLeft(position);
            BigDecimal distance2 = getDistanceMiddle(position);
            BigDecimal distance3 = getDistanceRight(position);

            // calculate coordinates
            Pair<BigDecimal, BigDecimal> coordinates = stackoverflowTrilaterator.trilateratePositionCoordinates(location1, location2, location3, distance1, distance2, distance3);

            // set calculated coordinates on position
            calculatedPosition.setxCoordinate(coordinates.getFirst());
            calculatedPosition.setyCoordinate(coordinates.getSecond());
            // set other representational-data on position
            calculatedPosition.setPositionTimestamp(position.getLeft().getSignalTimestamp());
            calculatedPosition.setRoute(route);
            setFloorDetailsOnPosition(calculatedPosition, position);
            setReferenceSignalsData(calculatedPosition, position);

            positions.add(calculatedPosition);
        }
        Collections.sort(positions);
        return positions;
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

    private void setReferenceSignalsData(Position calculatedPosition, ImmutableTriple<Signal, Signal, Signal> position) {
        calculatedPosition.setSignal1(position.getLeft());
        calculatedPosition.setSignal2(position.getMiddle());
        calculatedPosition.setSignal3(position.getRight());
    }
}
