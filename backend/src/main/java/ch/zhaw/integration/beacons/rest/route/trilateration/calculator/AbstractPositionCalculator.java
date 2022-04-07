package ch.zhaw.integration.beacons.rest.route.trilateration.calculator;

import ch.zhaw.integration.beacons.entities.position.Position;
import ch.zhaw.integration.beacons.entities.route.Route;
import ch.zhaw.integration.beacons.entities.signal.Signal;
import ch.zhaw.integration.beacons.utils.CalculationMethod;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public abstract class AbstractPositionCalculator {

    abstract CalculationMethod getCalculationMethod();
    abstract double getDistanceLeft(ImmutableTriple<Signal, Signal, Signal> partition);
    abstract double getDistanceRight(ImmutableTriple<Signal, Signal, Signal> partition);
    abstract double getDistanceMiddle(ImmutableTriple<Signal, Signal, Signal> partition);

    List<Position> calculatePositions(List<ImmutableTriple<Signal, Signal, Signal>> positionSignals, Route route) {
        List<Position> positions = new ArrayList<>();
        for (ImmutableTriple<Signal, Signal, Signal> position : positionSignals) {
            Position calculatedPosition = new Position();
            setFloorDetailsOnPosition(calculatedPosition, position);

            // calculate coordinates
            Pair<Double, Double> location1 = Pair.of(position.getLeft().getBeacon().getxCoordinate(), position.getLeft().getBeacon().getyCoordinate());
            Pair<Double, Double> location2 = Pair.of(position.getMiddle().getBeacon().getxCoordinate(), position.getMiddle().getBeacon().getyCoordinate());
            Pair<Double, Double> location3 = Pair.of(position.getRight().getBeacon().getxCoordinate(), position.getRight().getBeacon().getyCoordinate());
            double distance1 = getDistanceLeft(position);
            double distance2 = getDistanceMiddle(position);
            double distance3 = getDistanceRight(position);

            Pair<Double, Double> coordinates = trilateratePositionCoordinates(location1, location2, location3, distance1, distance2, distance3);
            calculatedPosition.setxCoordinate(coordinates.getFirst());
            calculatedPosition.setyCoordinate(coordinates.getSecond());
            calculatedPosition.setPositionTimestamp(getSignalTimestampMeanForPositionTimestamp(position));
            calculatedPosition.setRoute(route);

            positions.add(calculatedPosition);
        }
        return positions;
    }

    private Date getSignalTimestampMeanForPositionTimestamp(ImmutableTriple<Signal, Signal, Signal> position) {
        BigInteger total = BigInteger.ZERO;
        List<Date> signalDates = List.of(position.getLeft().getSignalTimestamp(), position.getMiddle().getSignalTimestamp(), position.getRight().getSignalTimestamp());
        for (Date date : signalDates) {
            total = total.add(BigInteger.valueOf(date.getTime()));
        }
        BigInteger averageMillis = total.divide(BigInteger.valueOf(signalDates.size()));
        return new Date(averageMillis.longValue());
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
}