package ch.zhaw.integration.beacons.rest;

import ch.zhaw.integration.beacons.entities.device.DeviceDto;
import ch.zhaw.integration.beacons.service.device.DeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DeviceRestController implements ApiRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRestController.class);

    private static final String INTERNAL_DEVICES_PATH = API_INTERNAL_BASE_PATH + "/devices";

    private final DeviceService deviceService;

    public DeviceRestController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }


    /**
     *  API for creating new device if not registered yet
     *
     *  @url:           "<host>"/beacons/api/internal/devices
     *  @method:        POST
     *  @body-param:    deviceDto: JSON-Object
     *  @returns:       HttpStatus = 200, JSON-Object of persisted device
     *
     * */
    @RequestMapping(value = INTERNAL_DEVICES_PATH, method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<DeviceDto> registerDeviceDevice(@RequestBody DeviceDto deviceDto){
        DeviceDto newDeviceDto = deviceService.createDevice(deviceDto);
       LOGGER.info("registerDevice-Api requested - POST " + INTERNAL_DEVICES_PATH + " , ResponseStatus: " +  HttpStatus.OK);
        return new ResponseEntity<>(newDeviceDto, HttpStatus.OK);
    }

    /**
     *  API to trigger Device-Data-Backup import
     *
     *  @url:           "<host>"/beacons/api/internal/devices/import_backup
     *  @method:        POST
     *  @body-param:    resourceFilePath: String (point to file in resources eg.: backup/xxx.csv
     *  @returns:       HttpStatus = 200, JSON-Array of imported devices
     *
     * */
    @RequestMapping(value =  INTERNAL_DEVICES_PATH + "/import_backup", method = RequestMethod.POST)
    public ResponseEntity<List<DeviceDto>> triggerImportBackupFromCsv(@RequestBody String resourceFilePath) {
        List<DeviceDto> newDevicesDtoList = deviceService.importBackupFromCsv(resourceFilePath);
        LOGGER.info("Persisted new Devices: count: " + newDevicesDtoList.size());
        return new ResponseEntity<>(newDevicesDtoList, HttpStatus.OK);
    }

}
