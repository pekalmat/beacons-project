package ch.zhaw.integration.beacons.entities.signal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface SignalToSignalDtoMapper {

    @Mapping(target = "deviceFingerPrint", ignore = true)
    SignalDto mapSignalToSignalDto(Signal signal);

    @Mapping(target = "deviceFingerPrint", ignore = true)
    List<SignalDto> mapSignalListToSignalDtoList(List<Signal> signalList);

    @Mapping(target = "beacon", ignore = true)
    @Mapping(target = "device", ignore = true)
    Signal mapSignalDtoToSignal(SignalDto signalDto);

    @Mapping(target = "beacon", ignore = true)
    @Mapping(target = "device", ignore = true)
    List<Signal> mapSignalDtoListToSignalList(List<SignalDto> signals);
    
}
