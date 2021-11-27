package ch.zhaw.integration.beacons.rest.beacon;

import ch.zhaw.integration.beacons.entities.beacon.Beacon;
import ch.zhaw.integration.beacons.entities.beacon.BeaconDto;
import ch.zhaw.integration.beacons.entities.beacon.BeaconRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class BeaconRestController {

    private final BeaconRepository beaconRepository;

    public BeaconRestController(BeaconRepository beaconRepository) {
        this.beaconRepository = beaconRepository;
    }

    @RequestMapping(value = "/tracking/beacons", method = RequestMethod.GET)
    public ResponseEntity<List<BeaconDto>> getAllBeacons(){
        List<Beacon> beacons = beaconRepository.findAll();
        List<BeaconDto> beaconDtoList = new ArrayList<>();
        for(Beacon beacon : beacons) {
            beaconDtoList.add(new BeaconDto(beacon.getUid(), beacon.getMajor(), beacon.getMinor()));
        }
        return new ResponseEntity<>(beaconDtoList, HttpStatus.OK);
    }
}
