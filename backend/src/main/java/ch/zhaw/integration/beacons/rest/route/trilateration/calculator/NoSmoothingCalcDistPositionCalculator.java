package ch.zhaw.integration.beacons.rest.route.trilateration.calculator;

import ch.zhaw.integration.beacons.entities.signal.Signal;
import ch.zhaw.integration.beacons.utils.CalculationMethod;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.stereotype.Component;

@Component
public class NoSmoothingCalcDistPositionCalculator  extends AbstractPositionCalculator {

    @Override
    CalculationMethod getCalculationMethod() {
        return CalculationMethod.TRILATERATION_NO_SMOOTHING_CALCULATED_DISTANCE;
    }

    @Override
    double getDistanceLeft(ImmutableTriple<Signal, Signal, Signal> partition) {
        return calculateDistanceOfSignal(partition.getLeft());
    }

    @Override
    double getDistanceRight(ImmutableTriple<Signal, Signal, Signal> partition) {
        return calculateDistanceOfSignal(partition.getRight());
    }

    @Override
    double getDistanceMiddle(ImmutableTriple<Signal, Signal, Signal> partition) {
        return calculateDistanceOfSignal(partition.getMiddle());
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

}
