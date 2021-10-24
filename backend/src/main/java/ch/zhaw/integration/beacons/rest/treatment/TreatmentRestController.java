package ch.zhaw.integration.beacons.rest.treatment;

import ch.zhaw.integration.beacons.DateUtils;
import ch.zhaw.integration.beacons.entities.doctor.Doctor;
import ch.zhaw.integration.beacons.entities.doctor.DoctorRepository;
import ch.zhaw.integration.beacons.entities.treatment.Treatment;
import ch.zhaw.integration.beacons.entities.treatment.TreatmentRepository;
import ch.zhaw.integration.beacons.scheduler.TrackingDataProcessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class TreatmentRestController {

    private final TrackingDataProcessor trackingDataProcessor;
    private final TreatmentRepository treatmentRepository;
    private final DoctorRepository doctorRepository;

    public TreatmentRestController(TreatmentRepository treatmentRepository, TrackingDataProcessor trackingDataProcessor, DoctorRepository doctorRepository) {
        this.treatmentRepository = treatmentRepository;
        this.trackingDataProcessor = trackingDataProcessor;
        this.doctorRepository = doctorRepository;
    }

    @RequestMapping(value = "/beacons/treatments", method = RequestMethod.GET)
    public ResponseEntity<List<Treatment>> getAllTreatments(){
        List<Treatment> treatmentList = treatmentRepository.findAll();
        return new ResponseEntity<>(treatmentList, HttpStatus.OK);
    }

    @RequestMapping(value = "/beacons/treatments/{doctorId}", method = RequestMethod.GET)
    public ResponseEntity<List<Treatment>> getTodaysTreatmentsForDoctor(@PathVariable String doctorId){
        Optional<Doctor> doctor = doctorRepository.findById(new Long(doctorId));
        if(doctor.isPresent()) {
            trackingDataProcessor.processTrackingDataForDoctor(doctor.get());
            List<Treatment> treatmentList = treatmentRepository.findAllByDoctorAndStartTimeAfter(DateUtils.atStartOfDay(new Date()));
            return new ResponseEntity<>(treatmentList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}