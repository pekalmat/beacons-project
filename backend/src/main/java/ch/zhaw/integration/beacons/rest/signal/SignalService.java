package ch.zhaw.integration.beacons.rest.signal;

import ch.zhaw.integration.beacons.entities.signal.Signal;
import ch.zhaw.integration.beacons.entities.signal.SignalDto;
import ch.zhaw.integration.beacons.entities.signal.SignalRepository;
import ch.zhaw.integration.beacons.entities.signal.SignalToSignalDtoMapper;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SignalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignalService.class);

    private final SignalRepository signalRepository;
    private final SignalToSignalDtoMapper signalMapper;

    public SignalService(SignalRepository signalRepository) {
        this.signalRepository = signalRepository;
        this.signalMapper = Mappers.getMapper(SignalToSignalDtoMapper.class);
    }

    List<SignalDto> storeNewSignals(List<SignalDto> signalDtoList) {
        List<Signal> signals = signalMapper.mapSignalDtoListToSignalList(signalDtoList);
        List<Signal> persisted = signalRepository.saveAll(signals);
        return signalMapper.mapSignalListToSignalDtoList(persisted);
    }
}
