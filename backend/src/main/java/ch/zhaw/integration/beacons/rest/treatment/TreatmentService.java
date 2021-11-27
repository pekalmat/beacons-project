package ch.zhaw.integration.beacons.rest.treatment;

import ch.zhaw.integration.beacons.entities.beacon.Beacon;
import ch.zhaw.integration.beacons.entities.beacon.BeaconRepository;
import ch.zhaw.integration.beacons.entities.person.doctor.Doctor;
import ch.zhaw.integration.beacons.entities.person.doctor.DoctorRepository;
import ch.zhaw.integration.beacons.entities.treatment.Treatment;
import ch.zhaw.integration.beacons.entities.treatment.TreatmentDto;
import ch.zhaw.integration.beacons.entities.treatment.TreatmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;

@Component
public class TreatmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TreatmentService.class);

    private final TreatmentRepository treatmentRepository;
    private final DoctorRepository doctorRepository;
    private final BeaconRepository beaconRepository;

    public TreatmentService(
            TreatmentRepository treatmentRepository,
            DoctorRepository doctorRepository,
            BeaconRepository beaconRepository) {
        this.treatmentRepository = treatmentRepository;
        this.doctorRepository = doctorRepository;
        this.beaconRepository = beaconRepository;
    }


    public ResponseEntity<TreatmentDto> storeNewTreatment(TreatmentDto treatmentDto) {
        Doctor doctor;
        try {
            doctor = doctorRepository.getOne(treatmentDto.getDoctorId());
            Beacon beacon = beaconRepository.findBeaconByUidAndMajorAndMinor(treatmentDto.getBeaconDto().getBeaconUid(), treatmentDto.getBeaconDto().getMajor(), treatmentDto.getBeaconDto().getMinor());
            if(beacon != null) {
                Treatment treatment = new Treatment();
                treatment.setDoctor(doctor);
                treatment.setPatient(beacon.getBed().getPatient());
                treatment.setStartTime(treatmentDto.getTreatmentStart());
                treatment.setEndTime(treatmentDto.getTreatmentEnd());
                Treatment newTreatment = treatmentRepository.save(treatment);
                LOGGER.info("Persisted new Treatment: ID: " + newTreatment.getId());
                return new ResponseEntity("Successfully persisted.", HttpStatus.OK);
            } else {
                String message = "Beacon with uid: " + treatmentDto.getBeaconDto().getBeaconUid() + " and major: " + treatmentDto.getBeaconDto().getMajor() + " and minor:" + treatmentDto.getBeaconDto().getMinor() + " is not known by the system.";
                LOGGER.warn(message);
                return new ResponseEntity(message, HttpStatus.BAD_REQUEST);
            }
        } catch (EntityNotFoundException e) {
            String message = "Doctor with id: " + treatmentDto.getDoctorId() + " is not known by the system.";
            LOGGER.error(message);
            return new ResponseEntity(message, HttpStatus.BAD_REQUEST);
        }

    }

}
