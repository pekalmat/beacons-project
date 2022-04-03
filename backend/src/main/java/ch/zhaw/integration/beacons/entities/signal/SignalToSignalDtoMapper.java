package ch.zhaw.integration.beacons.entities.signal;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface SignalToSignalDtoMapper {

    SignalDto mapSignalToSignalDto(Signal signal);

    List<SignalDto> mapSignalListToSignalDtoList(List<Signal> signalList);

    Signal mapSignalDtoToSignal(SignalDto signalDto);

    List<Signal> mapSignalDtoListToSignalList(List<SignalDto> signals);
    
}
