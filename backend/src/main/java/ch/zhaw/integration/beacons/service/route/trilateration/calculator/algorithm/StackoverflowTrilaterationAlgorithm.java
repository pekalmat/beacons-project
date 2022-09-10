package ch.zhaw.integration.beacons.service.route.trilateration.calculator.algorithm;

import ch.zhaw.integration.beacons.utils.Calculator;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class StackoverflowTrilaterationAlgorithm {

    private final Calculator calc;

    public StackoverflowTrilaterationAlgorithm(Calculator calc) {
        this.calc = calc;
    }

    // algorithm copied and adjusted from https://stackoverflow.com/questions/30336278/multi-point-trilateration-algorithm-in-java

    public Pair<BigDecimal, BigDecimal> trilateratePositionCoordinates(
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

}
