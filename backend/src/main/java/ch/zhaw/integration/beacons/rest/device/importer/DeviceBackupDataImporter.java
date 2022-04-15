package ch.zhaw.integration.beacons.rest.device.importer;

import ch.zhaw.integration.beacons.entities.device.Device;
import ch.zhaw.integration.beacons.entities.device.DeviceRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DeviceBackupDataImporter {

    private static final String ID = "id";
    private static final String FINGER_PRINT = "\"finger_print\"";
    private static final String MANUFACTURER = "\"manufacturer\"";
    private static final String BRAND = "\"brand\"";
    private static final String MODEL = "\"model\"";
    private static final String SDK = "\"sdk\"";
    private static final String[] HEADERS = {
            ID, FINGER_PRINT, MANUFACTURER, BRAND, MODEL, SDK
    };

    private final DeviceRepository deviceRepository;

    public DeviceBackupDataImporter(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public List<Device> importBackupFromCsv(String resourceFilePath) {
        List<Device> result;
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceFilePath);
        if(inputStream != null) {
            try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                 CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.builder().setHeader(HEADERS).setSkipHeaderRecord(true).setDelimiter(",").setTrim(true).setIgnoreHeaderCase(true).build())) {
                List<Device> devices = new ArrayList<>();
                Iterable<CSVRecord> csvRecords = csvParser.getRecords();
                for (CSVRecord csvRecord : csvRecords) {
                    devices.add(parseCsvRecordToDevice(csvRecord));
                }
                result = deviceRepository.saveAll(devices);
            } catch (IOException | ParseException e) {
                throw new RuntimeException("fail to parse CSV file: " + e.getLocalizedMessage());
            }
        } else {
            throw new RuntimeException("resource file not found: " + resourceFilePath);
        }
        return result;
    }

    private Device parseCsvRecordToDevice(CSVRecord csvRecord) throws ParseException {
        Device device = new Device();
        device.setId(Long.valueOf(csvRecord.get(ID)));
        device.setFingerPrint(csvRecord.get(FINGER_PRINT));
        device.setManufacturer(csvRecord.get(MANUFACTURER));
        device.setBrand(csvRecord.get(BRAND));
        device.setModel(csvRecord.get(MODEL));
        device.setSdk(Integer.valueOf(csvRecord.get(SDK)));
        return device;
    }
}
