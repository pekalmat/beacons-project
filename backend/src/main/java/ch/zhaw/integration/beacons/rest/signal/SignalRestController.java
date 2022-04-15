package ch.zhaw.integration.beacons.rest.signal;

import ch.zhaw.integration.beacons.entities.signal.SignalDto;
import ch.zhaw.integration.beacons.rest.ApiRestController;
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
public class SignalRestController implements ApiRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignalRestController.class);
    private static final String INTERNAL_SIGNALS_PATH = API_INTERNAL_BASE_PATH + "/signals";

    private final SignalService signalService;

    public SignalRestController(SignalService signalService) {
        this.signalService = signalService;
    }

    /**
     *  API for storing detected beacon-signals by the android-client
     *
     *  @url:           "<host>"/beacons/api/internal/signals
     *  @method:        POST
     *  @body-param:    signalDtoList: JSON-Array
     *  @returns:       HttpStatus = 200, JSON-Array of persisted signals
     *
     * */
    @RequestMapping(value =  INTERNAL_SIGNALS_PATH, method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<List<SignalDto>> storeNewSignals(@RequestBody List<SignalDto> signalDtoList) {
        List<SignalDto> newSignalDtoList = signalService.storeNewSignals(signalDtoList);
        LOGGER.debug("Persisted new Signal: count: " + newSignalDtoList.size() + " of " + signalDtoList.size());
        return new ResponseEntity<>(newSignalDtoList, HttpStatus.OK);
    }


    /**
     *  API to trigger Signal-Data-Backup import
     *
     *  @url:           "<host>"/beacons/api/internal/signals/import_backup
     *  @method:        POST
     *  @body-param:    resourceFilePath: String (point to file in resources eg.: backup/xxx.csv
     *  @returns:       HttpStatus = 200, JSON-Array of imported signals
     *
     * */
    @RequestMapping(value =  INTERNAL_SIGNALS_PATH + "/import_backup", method = RequestMethod.POST)
    public ResponseEntity<List<SignalDto>> triggerImportBackupFromCsv(@RequestBody String resourceFilePath) {
        List<SignalDto> newSignalDtoList = signalService.importBackupFromCsv(resourceFilePath);
        LOGGER.info("Persisted new Signal: count: " + newSignalDtoList.size());
        return new ResponseEntity<>(newSignalDtoList, HttpStatus.OK);
    }

    /**
     *  API for matching/connecting detected beacon-signals with the existing sbb beacons
     *
     *  @url:           "<host>"/beacons/api/internal/signals/match_beacons
     *  @method:        POST
     *  @body-param:    none
     *  @returns:       HttpStatus = 200, JSON-Array of matched signals
     *
     * */
    @RequestMapping(value =  INTERNAL_SIGNALS_PATH + "/match_beacons", method = RequestMethod.POST)
    public ResponseEntity<List<SignalDto>> matchSignalsWithBeacons() {
        List<SignalDto> matchedSignals = signalService.matchSignalsWithBeacons();
        LOGGER.info("Matched Signals: count: " + matchedSignals.size());
        return new ResponseEntity<>(matchedSignals, HttpStatus.OK);
    }

}
