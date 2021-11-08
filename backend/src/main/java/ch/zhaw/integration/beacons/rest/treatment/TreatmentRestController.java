package ch.zhaw.integration.beacons.rest.treatment;

import ch.zhaw.integration.beacons.utils.DateUtils;
import ch.zhaw.integration.beacons.entities.beacon.Beacon;
import ch.zhaw.integration.beacons.entities.beacon.BeaconRepository;
import ch.zhaw.integration.beacons.entities.person.doctor.Doctor;
import ch.zhaw.integration.beacons.entities.person.doctor.DoctorRepository;
import ch.zhaw.integration.beacons.entities.treatment.Treatment;
import ch.zhaw.integration.beacons.entities.treatment.TreatmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class TreatmentRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TreatmentRestController.class);

    private final TreatmentRepository treatmentRepository;
    private final DoctorRepository doctorRepository;
    private final BeaconRepository beaconRepository;

    public TreatmentRestController(TreatmentRepository treatmentRepository, DoctorRepository doctorRepository, BeaconRepository beaconRepository) {
        this.treatmentRepository = treatmentRepository;
        this.doctorRepository = doctorRepository;
        this.beaconRepository = beaconRepository;
    }

    @CrossOrigin
    @RequestMapping(value =  "/beacons/treatments/new", method = RequestMethod.PUT)
    public ResponseEntity storeNewTreatments(@RequestBody List<TreatmentDto> requestData){
        Doctor doctor;
        try {
            doctor = doctorRepository.getOne(requestData.get(0).getDoctorId());
        } catch (EntityNotFoundException e) {
            LOGGER.error("Doctor with id: " + requestData.get(0).getDoctorId() + " is not known by the system.");
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        for(TreatmentDto data : requestData) {
            Beacon beacon = beaconRepository.findBeaconByUidAndMajorAndMinor(data.getBeaconDto().getBeaconUid(), data.getBeaconDto().getMajor(), data.getBeaconDto().getMinor());
            if(beacon != null) {
                Treatment treatment = new Treatment();
                treatment.setDoctor(doctor);
                treatment.setPatient(beacon.getBed().getPatient());
                treatment.setStartTime(data.getTreatmentStart());
                treatment.setEndTime(data.getTreatmentEnd());
                treatmentRepository.save(treatment);
            } else {
                LOGGER.warn("Beacon with uid: " + data.getBeaconDto().getBeaconUid() + " and major: " + data.getBeaconDto().getMajor() + " and minor:" + data.getBeaconDto().getMinor() + " is not known by the system.");
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/beacons/treatments/all", method = RequestMethod.GET)
    public ResponseEntity<List<Treatment>> getAllTreatments(){
        List<Treatment> treatmentList = treatmentRepository.findAll();
        return new ResponseEntity<>(treatmentList, HttpStatus.OK);
    }

    @RequestMapping(value = "/beacons/treatments/{doctorId}/today", method = RequestMethod.GET)
    public ResponseEntity<List<Treatment>> getTodaysTreatmentsForDoctor(@PathVariable String doctorId){
        Optional<Doctor> doctor = doctorRepository.findById(new Long(doctorId));
        if(doctor.isPresent()) {
            List<Treatment> treatmentList = treatmentRepository.findAllByDoctorAndStartTimeAfter(doctor.get(), DateUtils.atStartOfDay(new Date()));
            return new ResponseEntity<>(treatmentList, HttpStatus.OK);
        } else {
            LOGGER.warn("No Doctor found with ID:" + doctorId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/beacons/treatments/{doctorId}", method = RequestMethod.GET)
    public ResponseEntity<List<Treatment>> getAllTreatmentsForDoctor(@PathVariable String doctorId){
        Optional<Doctor> doctor = doctorRepository.findById(new Long(doctorId));
        if(doctor.isPresent()) {
            List<Treatment> treatmentList = treatmentRepository.findAllByDoctor(doctor.get());
            return new ResponseEntity<>(treatmentList, HttpStatus.OK);
        } else {
            LOGGER.warn("No Doctor found with ID:" + doctorId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}