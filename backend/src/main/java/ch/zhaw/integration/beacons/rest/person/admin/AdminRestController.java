package ch.zhaw.integration.beacons.rest.person.admin;

import ch.zhaw.integration.beacons.entities.person.admin.Admin;
import ch.zhaw.integration.beacons.entities.person.admin.AdminRepository;
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
public class AdminRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminRestController.class);

    private final AdminRepository adminRepository;
    private final SecureAuthenticationHelper secureAuthenticationHelper;

    public AdminRestController(AdminRepository adminRepository, SecureAuthenticationHelper secureAuthenticationHelper) {
        this.adminRepository = adminRepository;
        this.secureAuthenticationHelper = secureAuthenticationHelper;
    }

    @CrossOrigin
    @RequestMapping(value = "/beacons/admin/new", method = RequestMethod.PUT)
    public ResponseEntity createNewAdminUser(@RequestBody NewUserRequestData requestData) {
        Admin admin = adminRepository.findByEmail(requestData.getEmail());
        if(admin != null) {
            LOGGER.warn("User already exists");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            createAndPersistNewAdmin(requestData);
        } catch (NoSuchAlgorithmException|InvalidKeySpecException e) {
            LOGGER.error("Failed login due to internal server error", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);

    }

    private void createAndPersistNewAdmin(NewUserRequestData data) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Admin admin = new Admin();
        admin.setFirstName(data.getFirstName());
        admin.setSurname(data.getSurname());
        admin.setEmail(data.getEmail());
        String salt = secureAuthenticationHelper.getNewSalt();
        admin.setUserSalt(salt);
        String encryptedPassword = secureAuthenticationHelper.getEncryptedPassword(data.getPassword(), salt);
        admin.setPassword(encryptedPassword);
        adminRepository.save(admin);
    }

}
