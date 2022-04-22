package ch.zhaw.integration.beacons.rest.route.trilateration.calculator;

import ch.zhaw.integration.beacons.entities.position.Position;
import ch.zhaw.integration.beacons.entities.route.Route;
import ch.zhaw.integration.beacons.entities.signal.Signal;
import ch.zhaw.integration.beacons.utils.CalculationMethod;
import ch.zhaw.integration.beacons.utils.Calculator;
import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public abstract class AbstractPositionCalculator {

    private final Calculator calc;

    protected AbstractPositionCalculator(Calculator calculator) {
        this.calc = calculator;
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
            Pair<BigDecimal, BigDecimal> coordinates = trilateratePositionCoordinates(location1, location2, location3, distance1, distance2, distance3);

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

    // 1. best trilateration calculation so far
    protected Pair<BigDecimal, BigDecimal> trilateratePositionCoordinates(
            Pair<BigDecimal, BigDecimal> location1,
            Pair<BigDecimal, BigDecimal> location2,
            Pair<BigDecimal, BigDecimal> location3,
            BigDecimal distance1,
            BigDecimal distance2,
            BigDecimal distance3) {
        //DECLARE VARIABLES
        BigDecimal[] P1   = new BigDecimal[2];
        BigDecimal[] P2   = new BigDecimal[2];
        BigDecimal[] P3   = new BigDecimal[2];
        BigDecimal[] ex   = new BigDecimal[2];
        BigDecimal[] ey   = new BigDecimal[2];
        BigDecimal[] p3p1 = new BigDecimal[2];
        BigDecimal jval  = BigDecimal.ZERO;
        BigDecimal temp  = BigDecimal.ZERO;
        BigDecimal ival  = BigDecimal.ZERO;
        BigDecimal p3p1i = BigDecimal.ZERO;
        BigDecimal triptx;
        BigDecimal tripty;
        BigDecimal xval;
        BigDecimal yval;
        BigDecimal t1;
        BigDecimal t2;
        BigDecimal t3;
        BigDecimal t;
        BigDecimal exx;
        BigDecimal d;
        BigDecimal eyy;

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
        distance1 =  calc.divide(distance1, BigDecimal.valueOf(100000));
        //DISTANCE BETWEEN POINT 2 AND MY LOCATION
        distance2 = calc.divide(distance2, BigDecimal.valueOf(100000));
        //DISTANCE BETWEEN POINT 3 AND MY LOCATION
        distance3 = calc.divide(distance3, BigDecimal.valueOf(100000));

        for (int i = 0; i < P1.length; i++) {
            t1   = P2[i];
            t2   = P1[i];
            t    = t1.subtract(t2);
            temp =  calc.add(temp, calc.multiply(t, t));
        }
        d = calc.sqrt(temp);
        for (int i = 0; i < P1.length; i++) {
            t1    = P2[i];
            t2    = P1[i];
            exx   = calc.divide(calc.subtract(t1, t2), calc.sqrt(temp));
            ex[i] = exx;
        }
        for (int i = 0; i < P3.length; i++) {
            t1      = P3[i];
            t2      = P1[i];
            t3      = calc.subtract(t1, t2);
            p3p1[i] = t3;
        }
        for (int i = 0; i < ex.length; i++) {
            t1 = ex[i];
            t2 = p3p1[i];
            ival = calc.add(ival, calc.multiply(t1,t2));
        }
        for (int  i = 0; i < P3.length; i++) {
            t1 = P3[i];
            t2 = P1[i];
            t3 = calc.multiply(ex[i], ival);
            t  = calc.subtract(calc.subtract(t1, t2),  t3);
            p3p1i = calc.add(p3p1i, calc.multiply(t, t));
        }
        for (int i = 0; i < P3.length; i++) {
            t1 = P3[i];
            t2 = P1[i];
            t3 = calc.multiply(ex[i], ival);
            eyy = calc.divide(calc.subtract(calc.subtract(t1, t2), t3), calc.sqrt(p3p1i));
            ey[i] = eyy;
        }
        for (int i = 0; i < ey.length; i++) {
            t1 = ey[i];
            t2 = p3p1[i];
            jval = calc.add(jval, calc.multiply(t1, t2));
        }
        xval = calc.divide(
                calc.add(
                        calc.subtract(calc.power(distance1, 2), calc.power(distance2, 2)),
                        calc.power(d, 2)),
                calc.multiply(BigDecimal.valueOf(2), d));
        yval = calc.subtract(
                        calc.divide(
                                calc.add(
                                        calc.add(
                                                calc.subtract(
                                                        calc.power(distance1, 2),
                                                        calc.power(distance3, 2)
                                                ),
                                                calc.power(ival, 2)),
                                        calc.power(jval, 2)
                                ),
                                calc.multiply(BigDecimal.valueOf(2), jval)
                        ),
                        calc.multiply(calc.divide(ival, jval), xval)
                );

        t1 = location1.getFirst();
        t2 = calc.multiply(ex[0], xval);
        t3 = calc.multiply(ey[0], yval);
        triptx = calc.add(calc.add(t1, t2), t3);

        t1 = location1.getSecond();
        t2 = calc.multiply(ex[1], xval);
        t3 = calc.multiply(ey[1], yval);
        tripty = calc.add(calc.add(t1, t2), t3);

        return Pair.of(triptx,tripty);
    }

    // other trilateration technique -> not working
    // Quelle: https://www.101computing.net/cell-phone-trilateration-algorithm/
    protected Pair<BigDecimal, BigDecimal> newTrill1(
            Pair<BigDecimal, BigDecimal> location1,
            Pair<BigDecimal, BigDecimal> location2,
            Pair<BigDecimal, BigDecimal> location3,
            BigDecimal distance1,
            BigDecimal distance2,
            BigDecimal distance3) {

        BigDecimal x1 = location1.getFirst();
        BigDecimal y1 = location1.getSecond();
        BigDecimal r1 = distance1;

        BigDecimal x2 = location2.getFirst();
        BigDecimal y2 = location2.getSecond();
        BigDecimal r2 = distance2;

        BigDecimal x3 = location3.getFirst();
        BigDecimal y3 = location3.getSecond();
        BigDecimal r3 = distance3;

        BigDecimal A = calc.subtract(calc.multiply(BigDecimal.valueOf(2), x2), calc.multiply(BigDecimal.valueOf(2), x1));
        BigDecimal B = calc.subtract(calc.multiply(BigDecimal.valueOf(2), y2), calc.multiply(BigDecimal.valueOf(2), y1));
        BigDecimal C = calc.add(calc.subtract(calc.add(calc.subtract(calc.subtract(calc.power(r1, 2), calc.power(r2, 2)), calc.power(x1, 2)), calc.power(x2, 2)), calc.power(y1, 2)), calc.power(y2, 2));
        BigDecimal D = calc.subtract(calc.multiply(BigDecimal.valueOf(2), x3), calc.multiply(BigDecimal.valueOf(2), x2));
        BigDecimal E = calc.subtract(calc.multiply(BigDecimal.valueOf(2), y3), calc.multiply(BigDecimal.valueOf(2), y2));
        BigDecimal F = calc.add(calc.subtract(calc.add(calc.subtract(calc.subtract(calc.power(r2, 2), calc.power(r3, 2)), calc.power(x2, 2)), calc.power(x3, 2)), calc.power(y2, 2)), calc.power(y3, 2));
        BigDecimal x = calc.divide(calc.subtract(calc.multiply(C, E), calc.multiply(F, B)), calc.subtract(calc.multiply(E, A), calc.multiply(B, D)));
        BigDecimal y = calc.divide(calc.subtract(calc.multiply(C, D), calc.multiply(A, F)), calc.subtract(calc.multiply(B, D), calc.multiply(A, E)));

        return Pair.of(x, y);
    }


    // other trilateration technique -> not working
    // QUelle: https://www.researchgate.net/figure/Trilateration-algorithm-for-object-localization-using-three-beacons-B-1-B-2-and-B-3_fig1_338241733
    protected Pair<BigDecimal, BigDecimal> newTrill2(
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

    // other trilateration technique -> not working
    // Trilateration-Library : https://github.com/lemmingapex/trilateration
    protected Pair<Double, Double> newTrill3Library(
            Pair<BigDecimal, BigDecimal> location1,
            Pair<BigDecimal, BigDecimal> location2,
            Pair<BigDecimal, BigDecimal> location3,
            BigDecimal distance1,
            BigDecimal distance2,
            BigDecimal distance3) {
        double[][] positions = new double[][] {
                { location1.getFirst().doubleValue(), location1.getSecond().doubleValue() },
                { location2.getFirst().doubleValue(), location2.getSecond().doubleValue() },
                { location3.getFirst().doubleValue(), location3.getSecond().doubleValue() }};
        double[] distances = new double[] { distance1.doubleValue(), distance2.doubleValue(), distance3.doubleValue()};

        NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
        LeastSquaresOptimizer.Optimum optimum = solver.solve();

        // the answer
        double[] centroid = optimum.getPoint().toArray();

        // error and geometry information; may throw SingularMatrixException depending the threshold argument provided
        RealVector standardDeviation = optimum.getSigma(0);
        RealMatrix covarianceMatrix = optimum.getCovariances(0);
        return Pair.of(centroid[0], centroid[1]);
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
