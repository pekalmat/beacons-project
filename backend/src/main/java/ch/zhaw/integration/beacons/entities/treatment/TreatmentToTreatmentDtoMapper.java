package ch.zhaw.integration.beacons.entities.treatment;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface TreatmentToTreatmentDtoMapper {

    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "beacon", ignore = true)
    TreatmentDto mapTreatmentToTreatmentDto(Treatment treatment);

    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "beacon", ignore = true)
    List<TreatmentDto> mapTreatmentListToTreatmentDtoList(List<Treatment> treatmentList);

    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "patient", ignore = true)
    Treatment mapTreatmentDtoToTreatment(TreatmentDto treatmentDto);

    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "patient", ignore = true)
    List<Treatment> mapTreatmentDtoListToTreatmentList(List<TreatmentDto> treatments);

}
