package ch.zhaw.integration.beacons.rest.treatment;

import ch.zhaw.integration.beacons.entities.treatment.TreatmentDto;
import ch.zhaw.integration.beacons.error.exception.BadRequestException;
import ch.zhaw.integration.beacons.rest.ApiRestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TreatmentRestController implements ApiRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TreatmentRestController.class);
    private static final String INTERNAL_TREATMENTS_PATH = API_INTERNAL_BASE_PATH + "/treatments";

    private final TreatmentService treatmentService;

    public TreatmentRestController(TreatmentService treatmentService) {
        this.treatmentService = treatmentService;
    }

    @RequestMapping(value =  INTERNAL_TREATMENTS_PATH, method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<TreatmentDto> storeNewTreatment(@RequestBody TreatmentDto treatmentDto) throws BadRequestException {
        TreatmentDto newTreatmentDto = treatmentService.storeNewTreatment(treatmentDto);
        LOGGER.info("storeNewTreatment-Api requested - POST " + INTERNAL_TREATMENTS_PATH + " , ResponseStatus: " + HttpStatus.OK);
        return new ResponseEntity<>(newTreatmentDto, HttpStatus.OK);
    }

    @RequestMapping(value =  INTERNAL_TREATMENTS_PATH + "/list", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<List<TreatmentDto>> storeNewTreatments(@RequestBody List<TreatmentDto> treatmentDtoList) throws BadRequestException {
        List<TreatmentDto> newTreatmentDtoList = treatmentService.storeNewTreatments(treatmentDtoList);
        LOGGER.debug("Persisted new Treatment: count: " + newTreatmentDtoList.size());
        return new ResponseEntity<>(newTreatmentDtoList, HttpStatus.OK);
    }

    @RequestMapping(value = INTERNAL_TREATMENTS_PATH, method = RequestMethod.GET)
    public ResponseEntity<List<TreatmentDto>> getAllTreatments(){
        List<TreatmentDto> treatments = treatmentService.getAllTreatments();
        return new ResponseEntity<>(treatments, HttpStatus.OK);
    }

    @RequestMapping(value = INTERNAL_TREATMENTS_PATH + "/{doctorId}/today", method = RequestMethod.GET)
    public ResponseEntity<List<TreatmentDto>> getTreatmentsForDoctorAsOfToday(@PathVariable String doctorId) throws BadRequestException {
        List<TreatmentDto> treatments = treatmentService.getTreatmentsForDoctorAsOfToday(Long.valueOf(doctorId));
        return new ResponseEntity<>(treatments, HttpStatus.OK);
    }

    @RequestMapping(value = INTERNAL_TREATMENTS_PATH + "/{doctorId}", method = RequestMethod.GET)
    public ResponseEntity<List<TreatmentDto>> getAllTreatmentsForDoctor(@PathVariable String doctorId) throws BadRequestException {
        List<TreatmentDto> treatments = treatmentService.getAllTreatmentsForDoctor(Long.valueOf(doctorId));
        return new ResponseEntity<>(treatments, HttpStatus.OK);
    }

}