package ch.zhaw.integration.beacons.rest.doctor;

import ch.zhaw.integration.beacons.entities.doctor.Doctor;
import ch.zhaw.integration.beacons.entities.doctor.DoctorDto;
import ch.zhaw.integration.beacons.entities.doctor.DoctorToDoctorDtoMapper;
import ch.zhaw.integration.beacons.entities.person.Person;
import ch.zhaw.integration.beacons.entities.person.PersonRepository;
import ch.zhaw.integration.beacons.error.exception.EmailInUseException;
import ch.zhaw.integration.beacons.security.SecurityHelper;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DoctorService {

    private final PersonRepository personRepository;
    private final SecurityHelper securityHelper;
    private final AuthenticationManager authenticationManager;
    private final DoctorToDoctorDtoMapper doctorMapper;

    public DoctorService(
            PersonRepository personRepository,
            SecurityHelper securityHelper,
            AuthenticationManager authenticationManager) {
        this.personRepository = personRepository;
        this.securityHelper = securityHelper;
        this.authenticationManager = authenticationManager;
        doctorMapper = Mappers.getMapper(DoctorToDoctorDtoMapper.class);
    }

    DoctorDto authenticateLogin(DoctorDto doctorDto) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        doctorDto.getEmail(),
                        doctorDto.getPassword()
                ));

        Doctor doctor = (Doctor) authenticate.getPrincipal();
        return doctorMapper.mapDoctorToDoctorDto(doctor);
    }

    void createNewUserAccount(DoctorDto doctorDto) throws EmailInUseException {
        Optional<Person> optional = personRepository.findByEmail(doctorDto.getEmail());
        if(optional.isEmpty()) {
            Doctor doctor = doctorMapper.mapDoctorDtoToDoctor(doctorDto);
            String encryptedPassword = securityHelper.getEncryptedPassword(doctorDto.getPassword());
            doctor.setPassword(encryptedPassword);
            personRepository.save(doctor);
        } else {
            throw new EmailInUseException();
        }
    }

}
