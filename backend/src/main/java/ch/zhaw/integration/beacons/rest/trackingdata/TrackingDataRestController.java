package ch.zhaw.integration.beacons.rest.trackingdata;

import ch.zhaw.integration.beacons.entities.beacon.Beacon;
import ch.zhaw.integration.beacons.entities.beacon.BeaconRepository;
import ch.zhaw.integration.beacons.entities.doctor.Doctor;
import ch.zhaw.integration.beacons.entities.doctor.DoctorRepository;
import ch.zhaw.integration.beacons.entities.trackingdata.TrackingData;
import ch.zhaw.integration.beacons.entities.trackingdata.TrackingDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;

@RestController
public class TrackingDataRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackingDataRestController.class);

    private final DoctorRepository doctorRepository;
    private final BeaconRepository beaconRepository;
    private final TrackingDataRepository trackingDataRepository;

    public TrackingDataRestController(DoctorRepository doctorRepository, BeaconRepository beaconRepository, TrackingDataRepository trackingDataRepository) {
        this.doctorRepository = doctorRepository;
        this.beaconRepository = beaconRepository;
        this.trackingDataRepository = trackingDataRepository;
    }

    @CrossOrigin
    @RequestMapping(value = "/beacons/storeTrackingData", method = RequestMethod.PUT)
    public ResponseEntity sendTrackingData(@RequestBody TrackingPutRequestData requestData){
        Doctor doctor;
        try {
            doctor = doctorRepository.getOne(requestData.getDoctorId());
        } catch (EntityNotFoundException e) {
            LOGGER.error("Doctor with id: " + requestData.getDoctorId() + " is not known by the system.");
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        for(BeaconTrackingData data : requestData.getBeaconTrackingDataList()) {
            Beacon beacon = beaconRepository.findBeaconByUidAndMajorAndMinor(data.getBeaconUid(), data.getMajor(), data.getMinor());
            if(beacon != null) {
                TrackingData newTrackingData = new TrackingData();
                newTrackingData.setDoctor(doctor);
                newTrackingData.setBeacon(beacon);
                newTrackingData.setTreatmentStartDate(data.getTreatmentStart());
                newTrackingData.setTreatmentEndDate(data.getTreatmentEnd());
                trackingDataRepository.save(newTrackingData);
            } else {
                LOGGER.warn("Beacon with uid: " + data.getBeaconUid() + " and major: " + data.getMajor() + " and minor:" + data.getMinor() + " is not known by the system.");
            }
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}