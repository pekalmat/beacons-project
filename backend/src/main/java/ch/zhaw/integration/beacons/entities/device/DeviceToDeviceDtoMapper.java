package ch.zhaw.integration.beacons.entities.device;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface DeviceToDeviceDtoMapper {

    DeviceDto mapDeviceToDeviceDto(Device device);

    List<DeviceDto> mapDeviceListToDeviceDtoList(List<Device> deviceList);

    @Mapping(target = "routes", ignore = true)
    @Mapping(target = "signals", ignore = true)
    Device mapDeviceDtoToDevice(DeviceDto deviceDto);

    @Mapping(target = "routes", ignore = true)
    @Mapping(target = "signals", ignore = true)
    List<Device> mapDeviceDtoListToDeviceList(List<DeviceDto> deviceDtoList);
}
