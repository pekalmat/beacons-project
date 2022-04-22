package ch.zhaw.integration.beacons.rest.route;

import ch.zhaw.integration.beacons.entities.device.Device;
import ch.zhaw.integration.beacons.entities.device.DeviceRepository;
import ch.zhaw.integration.beacons.entities.route.Route;
import ch.zhaw.integration.beacons.entities.route.RouteDto;
import ch.zhaw.integration.beacons.entities.route.RouteRepository;
import ch.zhaw.integration.beacons.entities.route.RouteToRouteDtoMapper;
import ch.zhaw.integration.beacons.rest.route.exporter.RouteDataCsvExporter;
import ch.zhaw.integration.beacons.rest.route.trilateration.TrilaterationRouteCalculator;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class RouteService {

    private final DeviceRepository deviceRepository;
    private final RouteRepository routeRepository;
    private RouteToRouteDtoMapper routeMapper;
    private final TrilaterationRouteCalculator trilaterationRouteCalculator;
    private final RouteDataCsvExporter routeDataCsvExporter;

    public RouteService(
            DeviceRepository deviceRepository,
            RouteRepository routeRepository,
            TrilaterationRouteCalculator trilaterationRouteCalculator,
            RouteDataCsvExporter routeDataCsvExporter) {
        this.deviceRepository = deviceRepository;
        this.routeRepository = routeRepository;
        this.trilaterationRouteCalculator = trilaterationRouteCalculator;
        this.routeDataCsvExporter = routeDataCsvExporter;
        this.routeMapper = Mappers.getMapper(RouteToRouteDtoMapper.class);
    }

    List<RouteDto> calculateRoutes(Date routeStartTime, Date routeEndTime, String type) {
        List<Route> routes = new ArrayList<>();
        Date triggerTime = Calendar.getInstance().getTime();
        List<Device> devices = deviceRepository.findAll();
        for (Device device : devices) {
            List<Route> deviceRoutes = trilaterationRouteCalculator.calculateRoutesForDevice(device, routeStartTime, routeEndTime, triggerTime);
            routeDataCsvExporter.createCsvPerRoute(deviceRoutes, type);
            routes.addAll(deviceRoutes);
        }
        return routeMapper.mapRouteListToRouteDtoList(routes);
    }


    public RouteDto exportRoute(Long routeId, String type) {
        Optional<Route> route = routeRepository.findById(routeId);
        if (route.isPresent()) {
            routeDataCsvExporter.createCsvPerRoute(List.of(route.get()), type);
            return routeMapper.mapRouteToRouteDto(route.get());
        }
        return null;
    }

    protected void setRouteMapper(RouteToRouteDtoMapper routeMapper) {
        this.routeMapper = routeMapper;
    }
}
