package ch.zhaw.integration.beacons.rest.device;

import ch.zhaw.integration.beacons.entities.device.Device;
import ch.zhaw.integration.beacons.entities.device.DeviceDto;
import ch.zhaw.integration.beacons.entities.device.DeviceRepository;
import ch.zhaw.integration.beacons.entities.device.DeviceToDeviceDtoMapper;
import ch.zhaw.integration.beacons.rest.device.importer.DeviceBackupDataImporter;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceToDeviceDtoMapper deviceMapper;
    private final DeviceBackupDataImporter deviceBackupDataImporter;

    public DeviceService(DeviceRepository deviceRepository, DeviceBackupDataImporter deviceBackupDataImporter) {
        this.deviceRepository = deviceRepository;
        this.deviceBackupDataImporter = deviceBackupDataImporter;
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

    List<DeviceDto> importBackupFromCsv(String resourceFilePath) {
        List<Device> devices = deviceBackupDataImporter.importBackupFromCsv(resourceFilePath);
        return deviceMapper.mapDeviceListToDeviceDtoList(devices);
    }
}
