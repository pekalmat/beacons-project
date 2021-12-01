package ch.zhaw.integration.beacons.entities.beacon;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface BeaconToBeaconDtoMapper {

    BeaconDto mapBeaconToBeaconDto(Beacon beacon);

    List<BeaconDto> mapBeaconListToBeaconDtoList(List<Beacon> beaconList);

    @Mapping(target = "bed", ignore = true)
    Beacon mapBeaconDtoToBeacon(BeaconDto beaconDto);

    @Mapping(target = "bed", ignore = true)
    List<Beacon> mapBeaconDtoListToBeaconList(List<BeaconDto> beaconDtoList);
    
}
