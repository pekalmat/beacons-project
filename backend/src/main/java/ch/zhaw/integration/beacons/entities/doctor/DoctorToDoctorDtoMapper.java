package ch.zhaw.integration.beacons.entities.doctor;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface DoctorToDoctorDtoMapper {

    @Mapping(target = "password", ignore = true)
    DoctorDto mapDoctorToDoctorDto(Doctor doctor);

    @Mapping(target = "password", ignore = true)
    List<DoctorDto> mapDoctorListToDoctorDtoList(List<Doctor> doctorList);

    @Mapping(target = "treatments", ignore = true)
    @Mapping(target = "password", ignore = true)
    Doctor mapDoctorDtoToDoctor(DoctorDto doctorDto);

    @Mapping(target = "treatments", ignore = true)
    @Mapping(target = "password", ignore = true)
    List<Doctor> mapDoctorDtoListToDoctorList(List<DoctorDto> doctorsDtoList);

}
