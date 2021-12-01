package ch.zhaw.integration.beacons.rest.beacon;

import ch.zhaw.integration.beacons.entities.beacon.Beacon;
import ch.zhaw.integration.beacons.entities.beacon.BeaconDto;
import ch.zhaw.integration.beacons.entities.beacon.BeaconRepository;
import ch.zhaw.integration.beacons.entities.beacon.BeaconToBeaconDtoMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BeaconService {

    private final BeaconRepository beaconRepository;
    private final BeaconToBeaconDtoMapper beaconMapper;

    public BeaconService(BeaconRepository beaconRepository) {
        this.beaconRepository = beaconRepository;
        this.beaconMapper = Mappers.getMapper(BeaconToBeaconDtoMapper.class);
    }

    List<BeaconDto> getAllBeacons() {
        List<Beacon> beacons = beaconRepository.findAll();
        return beaconMapper.mapBeaconListToBeaconDtoList(beacons);
    }

}
