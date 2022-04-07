package ch.zhaw.integration.beacons.entities.beacon;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface BeaconToBeaconDtoMapper {

    BeaconDto mapBeaconToBeaconDto(Beacon beacon);

    List<BeaconDto> mapBeaconListToBeaconDtoList(List<Beacon> beaconList);

    Beacon mapBeaconDtoToBeacon(BeaconDto beaconDto);

    List<Beacon> mapBeaconDtoListToBeaconList(List<BeaconDto> beaconDtoList);
    
}
