package ch.zhaw.integration.beacons;

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
    private static final String SQL_INIT_MODE_ALWAYS = "always";
    private final String sqlInitMode;
    private final SbbBeaconsDataLoader sbbBeaconsDataLoader;

    public Application(
            @Value( "${spring.sql.init.mode}" ) String sqlInitMode,
            SbbBeaconsDataLoader sbbBeaconsDataLoader) {
        this.sqlInitMode = sqlInitMode;
        this.sbbBeaconsDataLoader = sbbBeaconsDataLoader;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadSbbBeaconsAfterStartup() {
        if(sqlInitMode.equals(SQL_INIT_MODE_ALWAYS)) {
            LOGGER.info("Loading beacons-sbb-bahnhofe.csv into db");
            sbbBeaconsDataLoader.loadSbbBeaconData();
        } else {
            LOGGER.info("Not Loading beacons-sbb-bahnhofe.csv into db because property spring.sql.init.mode is set to '" + sqlInitMode + "'");
        }
    }
}
