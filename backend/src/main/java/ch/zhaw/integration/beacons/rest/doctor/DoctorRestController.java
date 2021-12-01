package ch.zhaw.integration.beacons.rest.doctor;

import ch.zhaw.integration.beacons.entities.doctor.DoctorDto;
import ch.zhaw.integration.beacons.error.exception.EmailInUseException;
import ch.zhaw.integration.beacons.rest.ApiRestController;
import ch.zhaw.integration.beacons.security.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DoctorRestController implements ApiRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorRestController.class);
    private static final String PUBLIC_DOCTORS_PATH = API_PUBLIC_BASE_PATH + "/doctors";

    private final DoctorService doctorService;
    private final JwtTokenUtil jwtTokenUtil;

    public DoctorRestController(DoctorService doctorService, JwtTokenUtil jwtTokenUtil) {
        this.doctorService = doctorService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @RequestMapping(value = PUBLIC_DOCTORS_PATH + "/login", method = RequestMethod.POST, consumes="application/json")
    public ResponseEntity<DoctorDto> loginUser(@RequestBody DoctorDto doctorDto){
        DoctorDto responseDto = doctorService.authenticateLogin(doctorDto);
        LOGGER.debug("User: " + doctorDto.getEmail() + " Response: " + HttpStatus.OK);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, jwtTokenUtil.generateToken(doctorDto.getEmail()))
                .body(responseDto);
    }

    @RequestMapping(value = PUBLIC_DOCTORS_PATH + "/signup", method = RequestMethod.POST, consumes="application/json")
    public ResponseEntity signUp(@RequestBody DoctorDto doctorDto) throws EmailInUseException {
        doctorService.createNewUserAccount(doctorDto);
        LOGGER.debug("User: " + doctorDto.getEmail() + " Response: " + HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
