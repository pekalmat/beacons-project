package ch.zhaw.integration.beacons.rest.beacon.exporter;

import ch.zhaw.integration.beacons.entities.beacon.Beacon;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Component
public class BeaconsDataCsvExporter {

    private static final String OUT_FOLDER = "out//beacons//";
    private static final String FLOOR_SUFFIX = "_floor_";
    private static final String CSV_FILE = ".csv";

    private static final String SEMICOLON_COLUMN_DELIMITER = ";";

    private static final String BEACON_ID = "beacon_id";
    private static final String MAJOR = "MAJOR";
    private static final String MINOR = "MINOR";
    private static final String NAME = "Name";
    private static final String GEOPOS = "geopos";
    private static final String REGISTER_SOURCE = "register_source";
    private static final String UID = "UID";
    private static final String GEOPOSITION = "Geoposition";
    private static final String FLOOR = "floor";
    private static final String[] HEADERS = {
            BEACON_ID, MAJOR, MINOR, NAME, GEOPOS, REGISTER_SOURCE, UID, GEOPOSITION, FLOOR
    };

    public void createBeaconsCsvFileOnePerFloorInOutFolderForOpenDataSoft(String filename, List<Beacon> beacons, String floor) {
        String outputFileName = OUT_FOLDER + filename + FLOOR_SUFFIX + floor + CSV_FILE;
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFileName));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder().setDelimiter(SEMICOLON_COLUMN_DELIMITER).setHeader(HEADERS).build())) {

            for(Beacon beacon : beacons) {
                csvPrinter.printRecord(
                        beacon.getId(),
                        beacon.getMajor(),
                        beacon.getMinor(),
                        beacon.getName(),
                        beacon.getGeopos(),
                        beacon.getRegisterSource(),
                        beacon.getUuid(),
                        beacon.getGeoposition(),
                        beacon.getFloor());

            }
            csvPrinter.flush();

        } catch (IOException e) {
            throw new RuntimeException("fail to write CSV file: " + e.getLocalizedMessage());
        }
    }
}
