package ch.zhaw.integration.beacons.service.route.trilateration.calculator.algorithm;

import ch.zhaw.integration.beacons.utils.Calculator;
import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TrilaterationLibraryAlgorithm {

    private Calculator calc;

    public TrilaterationLibraryAlgorithm(Calculator calculator) {
        this.calc = calculator;
    }

    // Trilateration-Library : https://github.com/lemmingapex/trilateration
    public Pair<Double, Double> trilateratePositionCoordinates(
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
}
