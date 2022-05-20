package ch.zhaw.integration.beacons;

import ch.zhaw.integration.beacons.entities.admin.Admin;
import ch.zhaw.integration.beacons.entities.admin.AdminRepository;
import ch.zhaw.integration.beacons.entities.beacon.BeaconRepository;
import ch.zhaw.integration.beacons.utils.SbbBeaconsDataLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;


@SpringBootApplication
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    private final String adminUserEmail;
    private final String adminUserFirstName;
    private final String adminUserSurname;
    private final String adminUserPassword;
    private final SbbBeaconsDataLoader sbbBeaconsDataLoader;
    private final AdminRepository adminRepository;
    private final BeaconRepository beaconRepository;

    public Application(
            @Value( "${beacons.user.admin.email}" ) String adminUserEmail,
            @Value( "${beacons.user.admin.firstname}" ) String adminUserFirstName,
            @Value( "${beacons.user.admin.surname}" ) String adminUserSurname,
            @Value( "${beacons.user.admin.password}" ) String adminUserPassword,
            SbbBeaconsDataLoader sbbBeaconsDataLoader,
            AdminRepository adminRepository,
            BeaconRepository beaconRepository) {
        this.adminUserEmail = adminUserEmail;
        this.adminUserFirstName = adminUserFirstName;
        this.adminUserSurname = adminUserSurname;
        this.adminUserPassword = adminUserPassword;
        this.sbbBeaconsDataLoader = sbbBeaconsDataLoader;
        this.adminRepository = adminRepository;
        this.beaconRepository = beaconRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void insertTestUserIfNotPresent() {
        if(adminRepository.count() == 0) {
            LOGGER.info("creating new Admin user");
            Admin admin = new Admin();
            admin.setEmail(adminUserEmail);
            admin.setFirstName(adminUserFirstName);
            admin.setSurname(adminUserSurname);
            admin.setPassword(adminUserPassword);
            adminRepository.save(admin);
        } else {
            LOGGER.info("Not Inserting Admin-User since its already present in DB");
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadSbbBeaconsAfterStartup() {
        if(beaconRepository.count() == 0) {
            LOGGER.info("Loading beacons-sbb-bahnhofe.csv into db");
            sbbBeaconsDataLoader.loadSbbBeaconData();
        } else {
            LOGGER.info("Not Loading beacons-sbb-bahnhofe.csv into db because 840 beacons already exist in db");
        }
    }
}
