package ch.zhaw.integration.beacons.utils;

import ch.zhaw.integration.beacons.entities.beacon.Beacon;
import ch.zhaw.integration.beacons.entities.beacon.BeaconRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class SbbBeaconsDataLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(SbbBeaconsDataLoader.class);

    private static final String MAJOR = "MAJOR";
    private static final String MINOR = "MINOR";
    private static final String NAME = "Name";
    private static final String PROXIMITYUUID = "PROXIMITYUUID";
    private static final String MAC = "MAC";
    private static final String GEOPOS = "geopos";
    private static final String REGISTER_SOURCE = "register_source";
    private static final String UID = "UID";
    private static final String STANDORT = "Standort";
    private static final String GEOPOSITION = "Geoposition";
    private static final String FLOOR = "floor";
    private static final String[] HEADERS = {
            MAJOR, MINOR, NAME, PROXIMITYUUID, MAC, GEOPOS, REGISTER_SOURCE, UID, STANDORT, GEOPOSITION, FLOOR
    };

    private final BeaconRepository beaconRepository;

    public SbbBeaconsDataLoader(BeaconRepository beaconRepository) {
        this.beaconRepository = beaconRepository;
    }

    public void loadSbbBeaconData() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("beacons-sbb-bahnhofe.csv");
        if(inputStream != null) {
            try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                 CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.builder().setHeader(HEADERS).setSkipHeaderRecord(true).setDelimiter(";").setTrim(true).setIgnoreHeaderCase(true).build())) {

                List<Beacon> beacons = new ArrayList<>();
                Iterable<CSVRecord> csvRecords = csvParser.getRecords();
                for (CSVRecord csvRecord : csvRecords) {
                    beacons.add(parseCsvRecordToBeacon(csvRecord));
                }
                beaconRepository.saveAll(beacons);
                LOGGER.info("Finished loading beacons-sbb-bahnhofe.csv - Records Imported: " + beacons.size());

            } catch (IOException e) {
                throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
            }
        } else {
            throw new RuntimeException("resource file not found: beacons-sbb-bahnhofe.csv");
        }
    }

    private Beacon parseCsvRecordToBeacon(CSVRecord csvRecord) {
        Beacon beacon = new Beacon();
        beacon.setMajor(csvRecord.get(MAJOR));
        beacon.setMinor(csvRecord.get(MINOR));
        beacon.setName(csvRecord.get(NAME));
        beacon.setGeopos(csvRecord.get(GEOPOS));
        beacon.setRegisterSource(csvRecord.get(REGISTER_SOURCE));
        beacon.setUuid(csvRecord.get(UID));
        beacon.setStandort(csvRecord.get(STANDORT));
        beacon.setGeoposition(csvRecord.get(GEOPOSITION));
        beacon.setFloor(csvRecord.get(FLOOR));
        String[] coordinates = beacon.getGeoposition().split(",");
        beacon.setxCoordinate(Double.parseDouble(coordinates[0]));
        beacon.setyCoordinate(Double.parseDouble(coordinates[1]));
        return beacon;
    }
}
