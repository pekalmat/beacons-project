package ch.zhaw.integration.beacons.rest.route.trilateration;

import ch.zhaw.integration.beacons.entities.device.Device;
import ch.zhaw.integration.beacons.entities.position.Position;
import ch.zhaw.integration.beacons.entities.route.Route;
import ch.zhaw.integration.beacons.entities.route.RouteRepository;
import ch.zhaw.integration.beacons.entities.signal.Signal;
import ch.zhaw.integration.beacons.rest.route.trilateration.calculator.PositionCalculator;
import ch.zhaw.integration.beacons.rest.route.trilateration.preprocessing.TrilaterationSignalPartitioner;
import ch.zhaw.integration.beacons.rest.route.trilateration.preprocessing.TrilaterationSignalPreprocessor;
import ch.zhaw.integration.beacons.utils.CalculationMethod;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class TrilaterationRouteCalculator {

    private final RouteRepository routeRepository;
    private final TrilaterationSignalPreprocessor trilaterationPreprocessor;
    private final TrilaterationSignalPartitioner trilaterationSignalPartitioner;
    private final PositionCalculator positionCalculator;

    public TrilaterationRouteCalculator(
            RouteRepository routeRepository,
            TrilaterationSignalPreprocessor trilaterationPreprocessor,
            TrilaterationSignalPartitioner trilaterationSignalPartitioner,
            PositionCalculator positionCalculator){
        this.routeRepository = routeRepository;
        this.trilaterationPreprocessor = trilaterationPreprocessor;
        this.trilaterationSignalPartitioner = trilaterationSignalPartitioner;
        this.positionCalculator = positionCalculator;
    }

    public Route trilaterateRoute(CalculationMethod calculationMethod, Date triggerTime, Device device, List<Signal> rawSignals) {
        List<Signal> signals =  trilaterationPreprocessor.preprocess(rawSignals);
        // Partitions of 3 Signals of 3 Different Beacons for Trilateration
        List<ImmutableTriple<Signal, Signal, Signal>> partitionsOf3 = trilaterationSignalPartitioner.createPartitionsOf3ClosestSignalsOf3DifferentBeaconsForTrilateration(signals);
        // Calculate Route
        return calculateRoute(calculationMethod, device, triggerTime, partitionsOf3);
    }

    private Route calculateRoute(CalculationMethod calculationMethod, Device device, Date triggerTime, List<ImmutableTriple<Signal, Signal, Signal>> positionSignals) {
        Route route = new Route();
        route.setDevice(device);
        route.setCalculationMethod(calculationMethod);
        route.setCalculationTriggerTime(triggerTime);
        List<Position> routePositions = positionCalculator.calculatePositions(calculationMethod, positionSignals, route);
        route.setPositions(routePositions);
        route.setRouteStart(!routePositions.isEmpty() ? routePositions.get(0).getPositionTimestamp() : null);
        route.setRouteEnd(!routePositions.isEmpty() ? routePositions.get(routePositions.size()-1).getPositionTimestamp() : null);
        return routeRepository.save(route);
    }

}
