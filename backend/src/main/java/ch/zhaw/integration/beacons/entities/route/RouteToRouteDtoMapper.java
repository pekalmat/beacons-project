package ch.zhaw.integration.beacons.entities.route;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface RouteToRouteDtoMapper {

    RouteDto mapRouteToRouteDto(Route route);

    List<RouteDto> mapRouteListToRouteDtoList(List<Route> routeList);

    @Mapping(target = "device", ignore = true)
    @Mapping(target = "positions", ignore = true)
    Route mapRouteDtoToRoute(RouteDto routeDto);

    @Mapping(target = "device", ignore = true)
    @Mapping(target = "positions", ignore = true)
    List<Route> mapRouteDtoListToRouteList(List<RouteDto> routes);
}
