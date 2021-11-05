package ch.zhaw.integration.beacons.rest.login;

import ch.zhaw.integration.beacons.entities.person.Person;
import ch.zhaw.integration.beacons.entities.person.PersonRepository;
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
public class LoginRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginRestController.class);

    private final PersonRepository personRepository;
    private final SecureAuthenticationHelper secureAuthenticationHelper;

    public LoginRestController(PersonRepository personRepository, SecureAuthenticationHelper secureAuthenticationHelper) {
        this.personRepository = personRepository;
        this.secureAuthenticationHelper = secureAuthenticationHelper;
    }

    @CrossOrigin
    @RequestMapping(value = "/beacons/login", method = RequestMethod.PUT)
    public ResponseEntity loginUser(@RequestBody LoginRequestData requestData){
        Person person = personRepository.findByEmail(requestData.getEmail());
        boolean authenticated = false;
        if (person == null) {
            LOGGER.warn("Failed login with not existent user Email: " + requestData.getEmail());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else {
            try {
                authenticated = secureAuthenticationHelper.authenticateUser(person, requestData.getPassword());
                if(!authenticated) {
                    LOGGER.warn("Failed login with wrong password for user Email: " + requestData.getEmail());
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }else {
                    LOGGER.warn("Successfull login for user Email" + requestData.getEmail());
                    return new ResponseEntity<>(person, HttpStatus.OK);
                }
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                LOGGER.error("Failed login due to internal server error", e);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

}
