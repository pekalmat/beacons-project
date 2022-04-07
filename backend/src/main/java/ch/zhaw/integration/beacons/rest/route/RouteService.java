package ch.zhaw.integration.beacons.rest.route;

import ch.zhaw.integration.beacons.entities.beacon.BeaconRepository;
import ch.zhaw.integration.beacons.entities.device.Device;
import ch.zhaw.integration.beacons.entities.device.DeviceRepository;
import ch.zhaw.integration.beacons.entities.position.PositionRepository;
import ch.zhaw.integration.beacons.entities.route.Route;
import ch.zhaw.integration.beacons.entities.route.RouteDto;
import ch.zhaw.integration.beacons.entities.route.RouteToRouteDtoMapper;
import ch.zhaw.integration.beacons.entities.signal.Signal;
import ch.zhaw.integration.beacons.entities.signal.SignalRepository;
import ch.zhaw.integration.beacons.rest.route.trilateration.TrilaterationRouteCalculator;
import ch.zhaw.integration.beacons.utils.CalculationMethod;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class RouteService {

    private final BeaconRepository beaconRepository;
    private final PositionRepository positionRepository;
    private final SignalRepository signalRepository;
    private final DeviceRepository deviceRepository;
    private final RouteToRouteDtoMapper routeMapper;
    private final TrilaterationRouteCalculator trilaterationRouteCalculator;

    public RouteService(
            BeaconRepository beaconRepository,
            PositionRepository positionRepository,
            SignalRepository signalRepository,
            DeviceRepository deviceRepository,
            TrilaterationRouteCalculator trilaterationRouteCalculator) {
        this.beaconRepository = beaconRepository;
        this.positionRepository = positionRepository;
        this.signalRepository = signalRepository;
        this.deviceRepository = deviceRepository;
        this.trilaterationRouteCalculator = trilaterationRouteCalculator;
        this.routeMapper = Mappers.getMapper(RouteToRouteDtoMapper.class);
    }

    List<RouteDto> calculateRoutes(Date routeStartTime, Date routeEndTime) {
        List<Route> routes = new ArrayList<>();
        Date triggerTime = Calendar.getInstance().getTime();
        List<Device> devices = deviceRepository.findAll();
        for (Device device : devices) {
            List<Signal> signals = signalRepository.findAllByDeviceAndSignalTimestampBetween(device, routeStartTime, routeEndTime);
            // 1.1. Calculate Route WITHOUT SIGNAL-SMOOTHING based on AndroidBeacons-LIBRARY-DISTANCE
            routes.add(trilaterationRouteCalculator.trilaterateRoute(CalculationMethod.TRILATERATION_NO_SMOOTHING_LIBRARY_DISTANCE, triggerTime, device, signals));
            // 1.2. Calculate Route WITHOUT SIGNAL-SMOOTHING based on CALCULATED-DISTANCE using own formula based on RSSI & TXPOWER
            routes.add(trilaterationRouteCalculator.trilaterateRoute(CalculationMethod.TRILATERATION_NO_SMOOTHING_CALCULATED_DISTANCE, triggerTime, device, signals));
            /*
            *  CALCULATORS NOT IMPLEMENTED YET
            *
            // 2.1. Calculate Route WITH SLIDING-WINDOW  Signal-Smoothing based on AndroidBeacons-LIBRARY-DISTANCE
            routes.add(trilaterationRouteCalculator.trilaterateRoute(CalculationMethod.TRILATERATION_SLIDING_WINDOW_LIBRARY_DISTANCE, triggerTime, device, signals));
            // 2.2. Calculate Route WITH SLIDING-WINDOW Signal-Smoothing based on CALCULATED-DISTANCE using own formula based on RSSI & TXPOWER
            routes.add(trilaterationRouteCalculator.trilaterateRoute(CalculationMethod.TRILATERATION_SLIDING_WINDOW_CALCULATED_DISTANCE, triggerTime, device, signals));
            // 3.1. Calculate Route WITH KALMANN-FILTER  Signal-Smoothing based on AndroidBeacons-LIBRARY-DISTANCE
            routes.add(trilaterationRouteCalculator.trilaterateRoute(CalculationMethod.TRILATERATION_KALMANN_FILTER_LIBRARY_DISTANCE, triggerTime, device, signals));
            // 3.2. Calculate Route WITH KALMANN-FILTER Signal-Smoothing based on CALCULATED-DISTANCE using own formula based on RSSI & TXPOWER
            routes.add(trilaterationRouteCalculator.trilaterateRoute(CalculationMethod.TRILATERATION_KALMANN_FILTER_CALCULATED_DISTANCE, triggerTime, device, signals));
            // 4.1. Calculate Route WITH SLIDING-WINDOW && KALMANN-FILTER  Signal-Smoothing based on AndroidBeacons-LIBRARY-DISTANCE
            routes.add(trilaterationRouteCalculator.trilaterateRoute(CalculationMethod.TRILATERATION_SLDING_WINDOW_AND_KALMANN_FILTER_LIBRARY_DISTANCE, triggerTime, device, signals));
            // 4.2. Calculate Route WITH SLIDING-WINDOW && KALMANN-FILTER Signal-Smoothing based on CALCULATED-DISTANCE using own formula based on RSSI & TXPOWER
            routes.add(trilaterationRouteCalculator.trilaterateRoute(CalculationMethod.TRILATERATION_SLDING_WINDOW_AND_KALMANN_FILTER_CALCULATED_DISTANCE, triggerTime, device, signals));
             *
             * */
        }
        return routeMapper.mapRouteListToRouteDtoList(routes);
    }


}
