package ch.zhaw.integration.beacons.rest.device;

import ch.zhaw.integration.beacons.entities.device.Device;
import ch.zhaw.integration.beacons.entities.device.DeviceDto;
import ch.zhaw.integration.beacons.entities.device.DeviceRepository;
import ch.zhaw.integration.beacons.entities.device.DeviceToDeviceDtoMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceToDeviceDtoMapper deviceMapper;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
        this.deviceMapper = Mappers.getMapper(DeviceToDeviceDtoMapper.class);
    }

    DeviceDto createDevice(DeviceDto deviceDto) {
        Device device = deviceRepository.findByFingerPrint(deviceDto.getFingerPrint());
        if(device == null) {
            device = deviceMapper.mapDeviceDtoToDevice(deviceDto);
            deviceRepository.save(device);
        }
        return deviceMapper.mapDeviceToDeviceDto(device);
    }
}
