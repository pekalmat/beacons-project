package ch.zhaw.integration.beacons.service.signal.importer;

import ch.zhaw.integration.beacons.entities.beacon.Beacon;
import ch.zhaw.integration.beacons.entities.beacon.BeaconRepository;
import ch.zhaw.integration.beacons.entities.device.Device;
import ch.zhaw.integration.beacons.entities.device.DeviceRepository;
import ch.zhaw.integration.beacons.entities.signal.Signal;
import ch.zhaw.integration.beacons.entities.signal.SignalRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

@Component
public class SignalBackupDataImporter {

    private static final String ID = "id";
    private static final String SIGNAL_TIMESTAMP = "signal_timestamp";
    private static final String UUID = "uuid";
    private static final String MAJOR = "major";
    private static final String MINOR = "minor";
    private static final String SERVICE_UUID = "service_uuid";
    private static final String BLUETOOTH_ADDRESS = "bluetooth_address";
    private static final String BLUETOOTH_NAME = "bluetooth_name";
    private static final String BEACON_TYPE_CODE = "beacon_type_code";
    private static final String PARSER_IDENTIFIER = "parser_identifier";
    private static final String TX_POWER = "tx_power";
    private static final String RSSI = "rssi";
    private static final String RUNNING_AVERAGE_RSSI = "running_average_rssi";
    private static final String DISTANCE = "distance";
    private static final String CALCULATED_DISTANCE = "calculated_distance";
    private static final String BEACON_ID = "beacon_id";
    private static final String DEVICE_ID = "device_id";
    private static final String[] HEADERS = {
            ID,SIGNAL_TIMESTAMP,UUID,MAJOR,MINOR,SERVICE_UUID,BLUETOOTH_ADDRESS,BLUETOOTH_NAME,BEACON_TYPE_CODE
            ,PARSER_IDENTIFIER, TX_POWER,RSSI,RUNNING_AVERAGE_RSSI,DISTANCE,CALCULATED_DISTANCE,BEACON_ID,DEVICE_ID
    };

    private final SignalRepository signalRepository;
    private final BeaconRepository beaconRepository;
    private final DeviceRepository deviceRepository;

    public SignalBackupDataImporter(
            SignalRepository signalRepository,
            BeaconRepository beaconRepository,
            DeviceRepository deviceRepository) {
        this.signalRepository = signalRepository;
        this.beaconRepository = beaconRepository;
        this.deviceRepository = deviceRepository;
    }


    public List<Signal> importBackupFromCsv(String resourceFilePath) {
        List<Signal> result;
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceFilePath);
        if(inputStream != null) {
            try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                 CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.builder().setHeader(HEADERS).setSkipHeaderRecord(true).setDelimiter(",").setTrim(true).setIgnoreHeaderCase(true).build())) {
                List<Signal> signals = new ArrayList<>();
                Iterable<CSVRecord> csvRecords = csvParser.getRecords();
                for (CSVRecord csvRecord : csvRecords) {
                    signals.add(parseCsvRecordToSignal(csvRecord));
                }
                result = signalRepository.saveAll(signals);
            } catch (IOException | ParseException e) {
                throw new RuntimeException("fail to parse CSV file: " + e.getLocalizedMessage());
            }
        } else {
            throw new RuntimeException("resource file not found: " + resourceFilePath);
        }
        return result;
    }

    private Signal parseCsvRecordToSignal(CSVRecord csvRecord) throws ParseException {
        Signal signal = new Signal();
        signal.setSignalTimestamp(parseSignalTimestamp(csvRecord));
        signal.setUuid(csvRecord.get(UUID));
        signal.setMajor(csvRecord.get(MAJOR));
        signal.setMinor(csvRecord.get(MINOR));
        signal.setServiceUuid(Integer.valueOf(csvRecord.get(SERVICE_UUID)));
        signal.setBluetoothAddress(csvRecord.get(BLUETOOTH_ADDRESS));
        signal.setBluetoothName(csvRecord.get(BLUETOOTH_NAME));
        signal.setBeaconTypeCode(Integer.valueOf(csvRecord.get(BEACON_TYPE_CODE)));
        signal.setParserIdentifier(csvRecord.get(PARSER_IDENTIFIER));
        signal.setTxPower(Integer.valueOf(csvRecord.get(TX_POWER)));
        signal.setRssi(Integer.valueOf(csvRecord.get(RSSI)));
        signal.setRunningAverageRssi(Double.valueOf(csvRecord.get(RUNNING_AVERAGE_RSSI)));
        signal.setDistance(new BigDecimal(csvRecord.get(DISTANCE)));
        signal.setCalculatedDistance(csvRecord.get(CALCULATED_DISTANCE) != null && !csvRecord.get(CALCULATED_DISTANCE).equals("NULL") ? new BigDecimal(csvRecord.get(CALCULATED_DISTANCE)) : null);
        signal.setBeacon(parseBeacon(csvRecord));
        signal.setDevice(parseDevice(csvRecord));
        return signal;
    }

    private Date parseSignalTimestamp(CSVRecord csvRecord) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getDefault());
        Date date;
        try {
            date = sdf.parse(csvRecord.get(SIGNAL_TIMESTAMP));
        } catch (ParseException e) {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getDefault());
            date = sdf.parse(csvRecord.get(SIGNAL_TIMESTAMP));
        }
        return date;
    }

    private Beacon parseBeacon(CSVRecord csvRecord) {
        Beacon result = null;
        if(csvRecord.get(BEACON_ID) != null && !csvRecord.get(BEACON_ID).equals("NULL")) {
            Optional<Beacon> beacon = beaconRepository.findById(Long.valueOf(csvRecord.get(BEACON_ID)));
            result = beacon.orElse(null);
        }
        return result;
    }

    private Device parseDevice(CSVRecord csvRecord) {
        Device result = null;
        if(csvRecord.get(DEVICE_ID) != null && !csvRecord.get(DEVICE_ID).equals("NULL")) {
            Optional<Device> device = deviceRepository.findById(Long.valueOf(csvRecord.get(DEVICE_ID)));
            result = device.orElse(null);
        }
        return result;
    }
}
