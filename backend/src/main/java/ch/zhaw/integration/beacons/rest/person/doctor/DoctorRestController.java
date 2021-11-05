package ch.zhaw.integration.beacons.rest.person.doctor;

import ch.zhaw.integration.beacons.entities.person.doctor.Doctor;
import ch.zhaw.integration.beacons.entities.person.doctor.DoctorRepository;
import ch.zhaw.integration.beacons.rest.person.NewUserRequestData;
import ch.zhaw.integration.beacons.security.SecureAuthenticationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RestController
public class DoctorRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorRestController.class);

    private final DoctorRepository doctorRepository;
    private final SecureAuthenticationHelper secureAuthenticationHelper;

    public DoctorRestController(DoctorRepository doctorRepository, SecureAuthenticationHelper secureAuthenticationHelper) {
        this.doctorRepository = doctorRepository;
        this.secureAuthenticationHelper = secureAuthenticationHelper;
    }

    @CrossOrigin
    @RequestMapping(value = "/beacons/doctor/new", method = RequestMethod.PUT)
    public ResponseEntity createNewDoctorUser(@RequestBody NewUserRequestData requestData) {
        Doctor doctor = doctorRepository.findByEmail(requestData.getEmail());
        if(doctor != null) {
            LOGGER.warn("User already exists");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            createAndPersistNewDoctor(requestData);
        } catch (NoSuchAlgorithmException|InvalidKeySpecException e) {
            LOGGER.error("Failed login due to internal server error", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);

    }

    private void createAndPersistNewDoctor(NewUserRequestData data) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Doctor doctor = new Doctor();
        doctor.setFirstName(data.getFirstName());
        doctor.setSurname(data.getSurname());
        doctor.setEmail(data.getEmail());
        String salt = secureAuthenticationHelper.getNewSalt();
        doctor.setUserSalt(salt);
        String encryptedPassword = secureAuthenticationHelper.getEncryptedPassword(data.getPassword(), salt);
        doctor.setPassword(encryptedPassword);
        doctorRepository.save(doctor);
    }

}
