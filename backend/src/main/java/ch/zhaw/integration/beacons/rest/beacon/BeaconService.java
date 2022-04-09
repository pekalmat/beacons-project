package ch.zhaw.integration.beacons.rest.beacon;

import ch.zhaw.integration.beacons.entities.beacon.Beacon;
import ch.zhaw.integration.beacons.entities.beacon.BeaconDto;
import ch.zhaw.integration.beacons.entities.beacon.BeaconRepository;
import ch.zhaw.integration.beacons.entities.beacon.BeaconToBeaconDtoMapper;
import ch.zhaw.integration.beacons.entities.signal.Signal;
import ch.zhaw.integration.beacons.entities.signal.SignalRepository;
import ch.zhaw.integration.beacons.rest.beacon.exporter.BeaconsDataCsvExporter;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BeaconService {

    private final BeaconRepository beaconRepository;
    private final SignalRepository signalRepository;
    private final BeaconsDataCsvExporter beaconsDataCsvExporter;
    private final BeaconToBeaconDtoMapper beaconMapper;

    public BeaconService(BeaconRepository beaconRepository,
                         SignalRepository signalRepository,
                         BeaconsDataCsvExporter beaconsDataCsvExporter) {
        this.beaconRepository = beaconRepository;
        this.signalRepository = signalRepository;
        this.beaconsDataCsvExporter = beaconsDataCsvExporter;
        this.beaconMapper = Mappers.getMapper(BeaconToBeaconDtoMapper.class);
    }

    List<BeaconDto> getAllBeacons() {
        List<Beacon> beacons = beaconRepository.findAll();
        return beaconMapper.mapBeaconListToBeaconDtoList(beacons);
    }

    public List<BeaconDto> getAllBeaconsWithSignalsAndWriteToCsv(String filename) {
        List<Beacon> beacons = beaconRepository.findAll();
        List<Beacon> beaconsEg = new ArrayList<>();
        List<Beacon> beaconsUg1 = new ArrayList<>();
        List<Beacon> beaconsUg2 = new ArrayList<>();
        List<Beacon> beaconsUg3 = new ArrayList<>();
        for(Beacon beacon : beacons) {
            List<Signal> signals = signalRepository.findAllByMajorAndMinor(beacon.getMajor(), beacon.getMinor());
            if(signals != null && !signals.isEmpty()) {
                if(beacon.getFloor().contains("0")) {
                    beaconsEg.add(beacon);
                } else if(beacon.getFloor().contains("1")) {
                    beaconsUg1.add(beacon);
                } else if(beacon.getFloor().contains("2")) {
                    beaconsUg2.add(beacon);
                } if(beacon.getFloor().contains("3")) {
                    beaconsUg3.add(beacon);
                }
            }
        }
        if(filename != null) {
            beaconsDataCsvExporter.createBeaconsCsvFileOnePerFloorInOutFolderForOpenDataSoft(filename, beaconsEg, "EG");
            beaconsDataCsvExporter.createBeaconsCsvFileOnePerFloorInOutFolderForOpenDataSoft(filename, beaconsUg1, "UG1");
            beaconsDataCsvExporter.createBeaconsCsvFileOnePerFloorInOutFolderForOpenDataSoft(filename, beaconsUg2, "UG2");
            beaconsDataCsvExporter.createBeaconsCsvFileOnePerFloorInOutFolderForOpenDataSoft(filename, beaconsUg3, "UG3");
        }
        List<Beacon> beaconsWithSignals = new ArrayList<>();
        beaconsWithSignals.addAll(beaconsEg);
        beaconsWithSignals.addAll(beaconsUg1);
        beaconsWithSignals.addAll(beaconsUg2);
        beaconsWithSignals.addAll(beaconsUg3);
        return beaconMapper.mapBeaconListToBeaconDtoList(beaconsWithSignals);
    }
}
