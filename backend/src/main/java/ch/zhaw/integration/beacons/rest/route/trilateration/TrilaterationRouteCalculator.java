package ch.zhaw.integration.beacons.rest.route.trilateration;

import ch.zhaw.integration.beacons.entities.device.Device;
import ch.zhaw.integration.beacons.entities.position.Position;
import ch.zhaw.integration.beacons.entities.route.Route;
import ch.zhaw.integration.beacons.entities.route.RouteRepository;
import ch.zhaw.integration.beacons.entities.signal.Signal;
import ch.zhaw.integration.beacons.entities.signal.SignalRepository;
import ch.zhaw.integration.beacons.rest.route.trilateration.calculator.PositionCalculator;
import ch.zhaw.integration.beacons.rest.route.trilateration.preprocessing.TrilaterationSignalPartitioner;
import ch.zhaw.integration.beacons.rest.route.trilateration.preprocessing.TrilaterationSignalPreprocessor;
import ch.zhaw.integration.beacons.utils.CalculationMethod;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class TrilaterationRouteCalculator {

    private final SignalRepository signalRepository;
    private final RouteRepository routeRepository;
    private final TrilaterationSignalPreprocessor trilaterationPreprocessor;
    private final TrilaterationSignalPartitioner trilaterationSignalPartitioner;
    private final PositionCalculator positionCalculator;

    public TrilaterationRouteCalculator(
            SignalRepository signalRepository,
            RouteRepository routeRepository,
            TrilaterationSignalPreprocessor trilaterationPreprocessor,
            TrilaterationSignalPartitioner trilaterationSignalPartitioner,
            PositionCalculator positionCalculator){
        this.signalRepository = signalRepository;
        this.routeRepository = routeRepository;
        this.trilaterationPreprocessor = trilaterationPreprocessor;
        this.trilaterationSignalPartitioner = trilaterationSignalPartitioner;
        this.positionCalculator = positionCalculator;
    }

    public List<Route> calculateRoutesForDevice(Device device, Date routeStartTime, Date routeEndTime, Date triggerTime) {
        List<Route> result = new ArrayList<>();
        List<Signal> rawSignals = signalRepository.findAllByDeviceAndSignalTimestampBetween(device, routeStartTime, routeEndTime);
        List<ImmutableTriple<Signal, Signal, Signal>> signals = prepareSignals(rawSignals);
        // 1.1. Calculate Route WITHOUT SIGNAL-SMOOTHING based on AndroidBeacons-LIBRARY-DISTANCE
        trilaterateRoute(CalculationMethod.TRILATERATION_NO_SMOOTHING_LIBRARY_DISTANCE, triggerTime, device, signals).ifPresent(result::add);
        // 1.2. Calculate Route WITHOUT SIGNAL-SMOOTHING based on CALCULATED-DISTANCE using own formula based on RSSI & TXPOWER
        trilaterateRoute(CalculationMethod.TRILATERATION_NO_SMOOTHING_CALCULATED_DISTANCE, triggerTime, device, signals).ifPresent(result::add);
        // TODO
            /*
            *  CALCULATORS NOT IMPLEMENTED YET
            *
            // 2.1. Calculate Route WITH SLIDING-WINDOW  Signal-Smoothing based on AndroidBeacons-LIBRARY-DISTANCE
            result.add(trilaterationRouteCalculator.trilaterateRoute(CalculationMethod.TRILATERATION_SLIDING_WINDOW_LIBRARY_DISTANCE, triggerTime, device, signals));
            // 2.2. Calculate Route WITH SLIDING-WINDOW Signal-Smoothing based on CALCULATED-DISTANCE using own formula based on RSSI & TXPOWER
            result.add(trilaterationRouteCalculator.trilaterateRoute(CalculationMethod.TRILATERATION_SLIDING_WINDOW_CALCULATED_DISTANCE, triggerTime, device, signals));
            // 3.1. Calculate Route WITH KALMANN-FILTER  Signal-Smoothing based on AndroidBeacons-LIBRARY-DISTANCE
            result.add(trilaterationRouteCalculator.trilaterateRoute(CalculationMethod.TRILATERATION_KALMANN_FILTER_LIBRARY_DISTANCE, triggerTime, device, signals));
            // 3.2. Calculate Route WITH KALMANN-FILTER Signal-Smoothing based on CALCULATED-DISTANCE using own formula based on RSSI & TXPOWER
            result.add(trilaterationRouteCalculator.trilaterateRoute(CalculationMethod.TRILATERATION_KALMANN_FILTER_CALCULATED_DISTANCE, triggerTime, device, signals));
            // 4.1. Calculate Route WITH SLIDING-WINDOW && KALMANN-FILTER  Signal-Smoothing based on AndroidBeacons-LIBRARY-DISTANCE
            result.add(trilaterationRouteCalculator.trilaterateRoute(CalculationMethod.TRILATERATION_SLDING_WINDOW_AND_KALMANN_FILTER_LIBRARY_DISTANCE, triggerTime, device, signals));
            // 4.2. Calculate Route WITH SLIDING-WINDOW && KALMANN-FILTER Signal-Smoothing based on CALCULATED-DISTANCE using own formula based on RSSI & TXPOWER
            result.add(trilaterationRouteCalculator.trilaterateRoute(CalculationMethod.TRILATERATION_SLDING_WINDOW_AND_KALMANN_FILTER_CALCULATED_DISTANCE, triggerTime, device, signals));
             *
             * */
        return result;
    }

    private List<ImmutableTriple<Signal, Signal, Signal>> prepareSignals(List<Signal> rawSignals) {
        List<Signal> signals =  trilaterationPreprocessor.preprocess(rawSignals);
        // Partitions of 3 Signals of 3 Different Beacons for Trilateration
        return trilaterationSignalPartitioner.createPartitionsOf3ClosestSignalsOf3DifferentBeaconsForTrilateration(signals);
    }

    private Optional<Route> trilaterateRoute(CalculationMethod calculationMethod, Date triggerTime, Device device, List<ImmutableTriple<Signal, Signal, Signal>> positionSignals) {
        Route route = new Route();
        route.setDevice(device);
        route.setCalculationMethod(calculationMethod);
        route.setCalculationTriggerTime(triggerTime);
        List<Position> routePositions = positionCalculator.calculatePositions(calculationMethod, positionSignals, route);
        route.setPositions(routePositions);
        route.setRouteStart(!routePositions.isEmpty() ? routePositions.get(0).getPositionTimestamp() : null);
        route.setRouteEnd(!routePositions.isEmpty() ? routePositions.get(routePositions.size()-1).getPositionTimestamp() : null);
        if(!routePositions.isEmpty()) {
            return Optional.of(routeRepository.save(route));
        }
        return Optional.empty();
    }

}
