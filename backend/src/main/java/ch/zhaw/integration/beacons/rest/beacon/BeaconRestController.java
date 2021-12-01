package ch.zhaw.integration.beacons.rest.beacon;

import ch.zhaw.integration.beacons.entities.beacon.BeaconDto;
import ch.zhaw.integration.beacons.rest.ApiRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BeaconRestController implements ApiRestController {

    private static final String INTERNAL_BEACONS_PATH = API_INTERNAL_BASE_PATH + "/beacons";

    private final BeaconService beaconService;

    public BeaconRestController(BeaconService beaconService) {
        this.beaconService = beaconService;
    }

    @RequestMapping(value = INTERNAL_BEACONS_PATH, method = RequestMethod.GET)
    public ResponseEntity<List<BeaconDto>> getAllBeacons(){
        List<BeaconDto> beacons = beaconService.getAllBeacons();
        return new ResponseEntity<>(beacons, HttpStatus.OK);
    }
}
