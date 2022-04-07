package ch.zhaw.integration.beacons.entities.admin;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface AdminToAdminDtoMapper {

    @Mapping(target = "password", ignore = true)
    AdminDto mapAdminToAdminDto(Admin admin);

    @Mapping(target = "password", ignore = true)
    List<AdminDto> mapAdminListToAdminDtoList(List<Admin> adminList);

    @Mapping(target = "authorities", ignore = true)
    Admin mapAdminDtoToAdmin(AdminDto admin);

    @Mapping(target = "authorities", ignore = true)
    List<Admin> mapAdminDtoListToAdminList(List<AdminDto> adminList);
    
}
