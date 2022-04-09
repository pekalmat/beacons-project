package ch.zhaw.integration.beacons.rest.admin;

import ch.zhaw.integration.beacons.entities.admin.AdminDto;
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
public class AdminRestController implements ApiRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminRestController.class);
    private static final String PUBLIC_ADMINS_PATH = API_PUBLIC_BASE_PATH + "/admins";

    private final AdminService adminService;
    private final JwtTokenUtil jwtTokenUtil;

    public AdminRestController(AdminService adminService, JwtTokenUtil jwtTokenUtil) {
        this.adminService = adminService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    /**
     *  API for authorizing admin-user
     *
     *  @url:           "<host>"/beacons/api/public/admins/login
     *  @method:        POST
     *  @body-param:    adminDto: JSON-Object
     *  @returns:       HttpStatus = 200, JSON-Object of authorized adminUser
     *  @returnsHeader:  bearerToken used for private-API authorization
     *
     * */
    @RequestMapping(value = PUBLIC_ADMINS_PATH + "/login", method = RequestMethod.POST, consumes="application/json")
    public ResponseEntity<AdminDto> loginAdmin(@RequestBody AdminDto adminDto) {
        AdminDto responseDto = adminService.authenticateLogin(adminDto);
        LOGGER.info("loginAdmin-API requested: " + adminDto.getEmail() + " Response: " + HttpStatus.OK);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, jwtTokenUtil.generateToken(adminDto.getEmail()))
                .body(responseDto);
    }

    /**
     *  API for creating new admin-user
     *
     *  @url:           "<host>"/beacons/api/public/admins/signup
     *  @method:        POST
     *  @body-param:    adminDto: JSON-Object
     *  @returns:       HttpStatus = 200, JSON-Object of created adminUser
     *
     * */
    @RequestMapping(value = PUBLIC_ADMINS_PATH + "/signup", method = RequestMethod.POST, consumes="application/json")
    public ResponseEntity signUpAdmin(@RequestBody AdminDto adminDto) throws EmailInUseException {
        adminService.createNewAdminAccount(adminDto);
        LOGGER.info("signUpAdmin-API requested: User: " + adminDto.getEmail() + " Response: " + HttpStatus.OK);
        return new ResponseEntity(HttpStatus.OK);
    }

}
