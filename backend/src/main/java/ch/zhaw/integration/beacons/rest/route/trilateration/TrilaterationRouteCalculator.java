package ch.zhaw.integration.beacons.rest.route.trilateration;

import ch.zhaw.integration.beacons.entities.device.Device;
import ch.zhaw.integration.beacons.entities.position.Position;
import ch.zhaw.integration.beacons.entities.route.Route;
import ch.zhaw.integration.beacons.entities.route.RouteRepository;
import ch.zhaw.integration.beacons.entities.signal.Signal;
import ch.zhaw.integration.beacons.entities.signal.SignalRepository;
import ch.zhaw.integration.beacons.rest.route.trilateration.calculator.PositionCalculator;
import ch.zhaw.integration.beacons.rest.route.trilateration.helper.TrilaterationSignalPreprocessor;
import ch.zhaw.integration.beacons.utils.CalculationMethod;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class TrilaterationRouteCalculator {

    private final SignalRepository signalRepository;
    private final RouteRepository routeRepository;
    private final TrilaterationSignalPreprocessor trilaterationPreprocessor;
    private final PositionCalculator positionCalculator;

    public TrilaterationRouteCalculator(
            SignalRepository signalRepository,
            RouteRepository routeRepository,
            TrilaterationSignalPreprocessor trilaterationPreprocessor,
            PositionCalculator positionCalculator){
        this.signalRepository = signalRepository;
        this.routeRepository = routeRepository;
        this.trilaterationPreprocessor = trilaterationPreprocessor;
        this.positionCalculator = positionCalculator;
    }

    public List<Route> calculateRoutesForDevice(Device device, Date routeStartTime, Date routeEndTime, Date triggerTime) {
        List<Route> result = new ArrayList<>();

        // read all Signals collected by device in given time period
        List<Signal> rawSignals = signalRepository.findAllByDeviceAndSignalTimestampBetween(device, routeStartTime, routeEndTime);
        // Calculated Distances and connect signal with known beacons -> only return connected signals
        Map<String, List<Signal>> signals =  trilaterationPreprocessor.preprocess(rawSignals, routeStartTime, routeEndTime);

        // 1 Calculate Route WITHOUT SIGNAL-SMOOTHING based on CALCULATED-DISTANCE using own formula based on RSSI & TXPOWER
        trilaterateRoute(CalculationMethod.TRILATERATION_NO_SMOOTHING, triggerTime, device, signals).ifPresent(result::add);
        // 2 Calculate Route WITH SLIDING-WINDOW Signal-Smoothing based on CALCULATED-DISTANCE using own formula based on RSSI & TXPOWER
        trilaterateRoute(CalculationMethod.TRILATERATION_SLIDING_WINDOW, triggerTime, device, signals).ifPresent(result::add);
        // TODO
            /*
            *  CALCULATORS NOT IMPLEMENTED YET
            *
            // 3 Calculate Route WITH KALMANN-FILTER Signal-Smoothing based on CALCULATED-DISTANCE using own formula based on RSSI & TXPOWER
            result.add(trilaterationRouteCalculator.trilaterateRoute(CalculationMethod.TRILATERATION_KALMANN_FILTER_CALCULATED_DISTANCE, triggerTime, device, signals));
            // 4 Calculate Route WITH SLIDING-WINDOW && KALMANN-FILTER Signal-Smoothing based on CALCULATED-DISTANCE using own formula based on RSSI & TXPOWER
            result.add(trilaterationRouteCalculator.trilaterateRoute(CalculationMethod.TRILATERATION_SLDING_WINDOW_AND_KALMANN_FILTER_CALCULATED_DISTANCE, triggerTime, device, signals));
             *
             * */
        return result;
    }

    private Optional<Route> trilaterateRoute(CalculationMethod calculationMethod, Date triggerTime, Device device, Map<String, List<Signal>> signals) {
        Route route = new Route();

        List<Position> routePositions = positionCalculator.calculatePositions(calculationMethod, signals, route);

        if(!routePositions.isEmpty()) {
            route.setDevice(device);
            route.setCalculationMethod(calculationMethod);
            route.setCalculationTriggerTime(triggerTime);
            route.setPositions(routePositions);
            route.setRouteStart(!routePositions.isEmpty() ? routePositions.get(0).getPositionTimestamp() : null);
            route.setRouteEnd(!routePositions.isEmpty() ? routePositions.get(routePositions.size()-1).getPositionTimestamp() : null);
            return Optional.of(routeRepository.save(route));
        }
        
        return Optional.empty();
    }

}
