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
import java.util.ArrayList;
import java.util.List;

@Component
public class SbbBeaconsDataLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(SbbBeaconsDataLoader.class);
    private static String[] HEADERS = {"MAJOR", "MINOR", "Name", "PROXIMITYUUID", "MAC", "geopos", "register_source", "UID", "Standort", "Geoposition", "floor"};

    private final BeaconRepository beaconRepository;

    public SbbBeaconsDataLoader(BeaconRepository beaconRepository) {
        this.beaconRepository = beaconRepository;
    }

    public void loadSbbBeaconData() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("beacons-sbb-bahnhofe.csv");
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.builder().setHeader(HEADERS).setSkipHeaderRecord(true).setDelimiter(";").setTrim(true).setIgnoreHeaderCase(true).build());) {
            List<Beacon> beacons = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                Beacon beacon = new Beacon();
                beacon.setMajor(csvRecord.get("MAJOR"));
                beacon.setMinor(csvRecord.get("MINOR"));
                beacon.setName(csvRecord.get("Name"));
                beacon.setGeopos(csvRecord.get("geopos"));
                beacon.setRegisterSource(csvRecord.get("register_source"));
                beacon.setUuid(csvRecord.get("UID"));
                beacon.setStandort(csvRecord.get("Standort"));
                beacon.setGeoposition(csvRecord.get("Geoposition"));
                beacon.setFloor(csvRecord.get("floor"));
                String[] coordinates = beacon.getGeoposition().split(",");
                beacon.setxCoordinate(Double.parseDouble(coordinates[0]));
                beacon.setyCoordinate(Double.parseDouble(coordinates[1]));
                beacons.add(beacon);
            }
            beaconRepository.saveAll(beacons);
            LOGGER.info("Finished loading beacons-sbb-bahnhofe.csv - Records Imported: " + beacons.size());
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

}
