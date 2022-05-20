package ch.zhaw.integration.beacons.service.admin;

import ch.zhaw.integration.beacons.entities.admin.Admin;
import ch.zhaw.integration.beacons.entities.admin.AdminDto;
import ch.zhaw.integration.beacons.entities.admin.AdminRepository;
import ch.zhaw.integration.beacons.entities.admin.AdminToAdminDtoMapper;
import ch.zhaw.integration.beacons.entities.person.Person;
import ch.zhaw.integration.beacons.entities.person.PersonRepository;
import ch.zhaw.integration.beacons.error.exception.EmailInUseException;
import ch.zhaw.integration.beacons.security.SecurityHelper;
import org.mapstruct.factory.Mappers;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AdminService {

    private final AuthenticationManager authenticationManager;
    private final PersonRepository personRepository;
    private final AdminRepository adminRepository;
    private final SecurityHelper securityHelper;
    private final AdminToAdminDtoMapper adminMapper;

    public AdminService(
            AuthenticationManager authenticationManager,
            PersonRepository personRepository,
            AdminRepository adminRepository,
            SecurityHelper securityHelper) {
        this.authenticationManager = authenticationManager;
        this.personRepository = personRepository;
        this.adminRepository = adminRepository;
        this.securityHelper = securityHelper;
        this.adminMapper = Mappers.getMapper(AdminToAdminDtoMapper.class);
    }

    public AdminDto authenticateLogin(AdminDto adminDto)  {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        adminDto.getEmail(),
                        adminDto.getPassword()
                ));

        Admin Admin = (Admin) authenticate.getPrincipal();
        return adminMapper.mapAdminToAdminDto(Admin);
    }

    public void createNewAdminAccount(AdminDto adminDto) throws EmailInUseException {
        Optional<Person> person = personRepository.findByEmail(adminDto.getEmail());
        if(person.isEmpty()) {
            Admin admin = adminMapper.mapAdminDtoToAdmin(adminDto);
            String encryptedPassword = securityHelper.getEncryptedPassword(adminDto.getPassword());
            admin.setPassword(encryptedPassword);
            adminRepository.save(admin);
        } else if (person.get() instanceof Admin) {
            throw new EmailInUseException();
        }
    }
}
