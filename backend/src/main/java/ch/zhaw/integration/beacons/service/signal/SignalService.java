package ch.zhaw.integration.beacons.service.signal;

import ch.zhaw.integration.beacons.entities.device.Device;
import ch.zhaw.integration.beacons.entities.device.DeviceRepository;
import ch.zhaw.integration.beacons.entities.signal.Signal;
import ch.zhaw.integration.beacons.entities.signal.SignalDto;
import ch.zhaw.integration.beacons.entities.signal.SignalRepository;
import ch.zhaw.integration.beacons.entities.signal.SignalToSignalDtoMapper;
import ch.zhaw.integration.beacons.service.route.trilateration.helper.TrilaterationSignalPreprocessor;
import ch.zhaw.integration.beacons.service.signal.importer.SignalBackupDataImporter;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SignalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignalService.class);

    private final SignalRepository signalRepository;
    private final DeviceRepository deviceRepository;
    private final SignalBackupDataImporter signalBackupDataImporter;
    private final SignalToSignalDtoMapper signalMapper;
    private final TrilaterationSignalPreprocessor trilaterationSignalPreprocessor;

    public SignalService(
            SignalRepository signalRepository,
            DeviceRepository deviceRepository,
            SignalBackupDataImporter signalBackupDataImporter,
            TrilaterationSignalPreprocessor trilaterationSignalPreprocessor) {
        this.signalRepository = signalRepository;
        this.deviceRepository = deviceRepository;
        this.signalBackupDataImporter = signalBackupDataImporter;
        this.trilaterationSignalPreprocessor = trilaterationSignalPreprocessor;
        this.signalMapper = Mappers.getMapper(SignalToSignalDtoMapper.class);
    }

    public List<SignalDto> storeNewSignals(List<SignalDto> signalDtoList) {
        List<Signal> newSignals = new ArrayList<>();
        for (SignalDto signalDto : signalDtoList) {
            Signal signal = signalMapper.mapSignalDtoToSignal(signalDto);
            Device device = deviceRepository.findByFingerPrint(signalDto.getDeviceFingerPrint());
            signal.setDevice(device);
            newSignals.add(signal);
        }
        List<Signal> persisted = signalRepository.saveAll(newSignals);
        return signalMapper.mapSignalListToSignalDtoList(persisted);
    }

    public List<SignalDto> importBackupFromCsv(String resourceFilePath) {
        List<Signal> signals = signalBackupDataImporter.importBackupFromCsv(resourceFilePath);
        return signalMapper.mapSignalListToSignalDtoList(signals);
    }

    public List<SignalDto> matchSignalsWithBeacons() {
        List<Signal> signals = signalRepository.findAll();
        List<Signal> connectedSignals = new ArrayList<>();
        for (Signal signal : signals) {
             trilaterationSignalPreprocessor.connectSignalsWithKnownSbbBeacons(signal);
             if (signal.getBeacon() != null) {
                connectedSignals.add(signal);
             }
        }
        List<Signal> result = signalRepository.saveAll(connectedSignals);
        LOGGER.info("Matched Signals : " + connectedSignals.size() + " of " + signals.size());
        return signalMapper.mapSignalListToSignalDtoList(result);
    }
}