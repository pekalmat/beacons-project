package ch.zhaw.integration.beacons.rest.route;

import ch.zhaw.integration.beacons.entities.route.RouteDto;
import ch.zhaw.integration.beacons.rest.ApiRestController;
import ch.zhaw.integration.beacons.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
public class RouteRestController implements ApiRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteRestController.class);
    protected static final String INTERNAL_ROUTES_PATH = API_INTERNAL_BASE_PATH + "/routes";

    private final RouteService routeService;

    public RouteRestController(RouteService routeService) {
        this.routeService = routeService;
    }

    /**
     *  API for calculating route for given time-period
     *          -> matches Signals with Beacons, enriches Signals with calculatedDistance & persist changes
     *          -> calculated routes stored in out/routes-folder
     *
     *  @url:           "<host>"/beacons/api/internal/routes/calculate
     *  @method:        GET
     *  @query-param:   routeStart: String (e.g. format: 2022-04-07 20:00:00)
     *  @query-param:   routeEnd: String   (e.g. format: 2022-04-09 20:26:00)
     *  @query-param:   type: String (Point/Line)
     *  @returns:       HttpStatus = 200, JSON-Array of calculated routes
     *
     * */
    @RequestMapping(value =  INTERNAL_ROUTES_PATH + "/calculate", method = RequestMethod.GET)
    public ResponseEntity<List<RouteDto>> calculateRoutes(
            @RequestParam("routeStart") @DateTimeFormat(pattern = DateUtils.YYYY_MM_DD_HH_MM_SS) Date routeStart,
            @RequestParam("routeEnd") @DateTimeFormat(pattern = DateUtils.YYYY_MM_DD_HH_MM_SS) Date routeEnd,
            @RequestParam String type) {
        LOGGER.info("Routes calculation running...");
        List<RouteDto> calculatedRoutes = routeService.calculateRoutes(routeStart, routeEnd, type);
        LOGGER.info("Routes Calculated  count: " + calculatedRoutes.size());
        return new ResponseEntity<>(calculatedRoutes, HttpStatus.OK);
    }

    /**
     *  API exporting already calculated routes to csv file in out/routes-folder
     *
     *  @url:           "<host>"/beacons/api/internal/routes/export
     *  @method:        GET
     *  @query-param:   routeId: Long
     *  @query-param:   type: String (Point/Line)
     *  @returns:       HttpStatus = 200, JSON-Object of exported route
     *
     * */
    @RequestMapping(value =  INTERNAL_ROUTES_PATH + "/export", method = RequestMethod.GET)
    public ResponseEntity<RouteDto> exportRoute(@RequestParam Long routeId, @RequestParam String type) {
        RouteDto exportedRoute = routeService.exportRoute(routeId, type);
        LOGGER.info("Route exported to csv: " + exportedRoute);
        return new ResponseEntity<>(exportedRoute, HttpStatus.OK);
    }

}
