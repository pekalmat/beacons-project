package ch.zhaw.integration.beacons.rest.route;

import ch.zhaw.integration.beacons.entities.route.RouteDto;
import ch.zhaw.integration.beacons.rest.ApiRestController;
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
    private static final String INTERNAL_SIGNALS_PATH = API_INTERNAL_BASE_PATH + "/routes";

    private final RouteService routeService;

    public RouteRestController(RouteService routeService) {
        this.routeService = routeService;
    }

    @RequestMapping(value =  INTERNAL_SIGNALS_PATH + "/calculate", method = RequestMethod.GET)
    public ResponseEntity<List<RouteDto>> calculateRoutes(
            @RequestParam("routeStart") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date routeStart,
            @RequestParam("routeEnd") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date routeEnd) {
        List<RouteDto> calculatedRoutes = routeService.calculateRoutes(routeStart, routeEnd);
        LOGGER.info("Routes Calculated  count: " + calculatedRoutes.size());
        return new ResponseEntity<>(calculatedRoutes, HttpStatus.OK);
    }
}
