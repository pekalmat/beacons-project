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

    @RequestMapping(value =  INTERNAL_SIGNALS_PATH, method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<List<SignalDto>> storeNewTreatments(@RequestBody List<SignalDto> signalDtoList) {
        List<SignalDto> newSignalDtoList = signalService.storeNewSignals(signalDtoList);
        LOGGER.debug("Persisted new Signal: count: " + newSignalDtoList.size() + " of " + signalDtoList.size());
        return new ResponseEntity<>(newSignalDtoList, HttpStatus.OK);
    }
}
