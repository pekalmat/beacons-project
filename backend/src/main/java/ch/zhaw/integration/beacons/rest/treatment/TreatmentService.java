package ch.zhaw.integration.beacons.rest.treatment;

import ch.zhaw.integration.beacons.entities.beacon.Beacon;
import ch.zhaw.integration.beacons.entities.beacon.BeaconRepository;
import ch.zhaw.integration.beacons.entities.doctor.Doctor;
import ch.zhaw.integration.beacons.entities.doctor.DoctorRepository;
import ch.zhaw.integration.beacons.entities.patient.Patient;
import ch.zhaw.integration.beacons.entities.treatment.Treatment;
import ch.zhaw.integration.beacons.entities.treatment.TreatmentDto;
import ch.zhaw.integration.beacons.entities.treatment.TreatmentRepository;
import ch.zhaw.integration.beacons.entities.treatment.TreatmentToTreatmentDtoMapper;
import ch.zhaw.integration.beacons.error.exception.BadRequestException;
import ch.zhaw.integration.beacons.utils.DateUtils;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class TreatmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TreatmentService.class);

    private final TreatmentRepository treatmentRepository;
    private final DoctorRepository doctorRepository;
    private final BeaconRepository beaconRepository;
    private final TreatmentToTreatmentDtoMapper treatmentMapper;

    public TreatmentService(
            TreatmentRepository treatmentRepository,
            DoctorRepository doctorRepository,
            BeaconRepository beaconRepository) {
        this.treatmentRepository = treatmentRepository;
        this.doctorRepository = doctorRepository;
        this.beaconRepository = beaconRepository;
        this.treatmentMapper = Mappers.getMapper(TreatmentToTreatmentDtoMapper.class);

    }

    // TODO: add Hateoas Links

    List<TreatmentDto> getAllTreatmentsForDoctor(Long doctorId) throws BadRequestException {
        Optional<Doctor> doctor = doctorRepository.findById(doctorId);
        if(doctor.isPresent()) {
            List<Treatment> treatmentList = treatmentRepository.findAllByDoctor(doctor.get());
            return treatmentMapper.mapTreatmentListToTreatmentDtoList(treatmentList);
        } else {
            String message = "No Doctor found with ID:" + doctorId;
            LOGGER.warn(message);
            throw new BadRequestException(message);
        }
    }

    List<TreatmentDto> getTreatmentsForDoctorAsOfToday(Long doctorId) throws BadRequestException {
        Optional<Doctor> doctor = doctorRepository.findById(doctorId);
        if(doctor.isPresent()) {
            List<Treatment> treatmentList = treatmentRepository.findAllByDoctorAndStartTimeAfter(doctor.get(), DateUtils.atStartOfDay(new Date()));
            return treatmentMapper.mapTreatmentListToTreatmentDtoList(treatmentList);
        } else {
            String message = "No Doctor found with ID:" + doctorId;
            LOGGER.warn(message);
            throw new BadRequestException(message);
        }
    }

    List<TreatmentDto> getAllTreatments() {
        List<Treatment> treatmentList = treatmentRepository.findAll();
        return treatmentMapper.mapTreatmentListToTreatmentDtoList(treatmentList);
    }

    List<TreatmentDto> storeNewTreatments(List<TreatmentDto> treatmentDtoList) throws BadRequestException {
        List<TreatmentDto> newTreatments = new ArrayList<>();
        for(TreatmentDto treatmentDto : treatmentDtoList) {
            TreatmentDto newTreatment = storeNewTreatment(treatmentDto);
            newTreatments.add(newTreatment);
        }
        return newTreatments;
    }

    TreatmentDto storeNewTreatment(TreatmentDto treatmentDto) throws BadRequestException {
        Optional<Doctor> doctor = doctorRepository.findById(treatmentDto.getDoctor().getId());
        if(doctor.isPresent()){
            Beacon beacon = beaconRepository.findBeaconByUuidAndMajorAndMinor(treatmentDto.getBeacon().getUuid(), treatmentDto.getBeacon().getMajor(), treatmentDto.getBeacon().getMinor());
            if(beacon != null) {
                Treatment newTreatment = createAndPersistNewTreatment(treatmentDto, doctor.get(), beacon.getBed().getPatient());
                return treatmentMapper.mapTreatmentToTreatmentDto(newTreatment);
            } else {
                String message = "Beacon with uuid: " + treatmentDto.getBeacon().getUuid() + " and major: " + treatmentDto.getBeacon().getMajor() + " and minor:" + treatmentDto.getBeacon().getMinor() + " is not known by the system.";
                LOGGER.warn(message);
                throw new BadRequestException(message);
            }
        } else {
            String message = "Doctor with id: " + treatmentDto.getDoctor().getId() + " is not known by the system.";
            LOGGER.error(message);
            throw new BadRequestException(message);
        }
    }

    private Treatment createAndPersistNewTreatment(TreatmentDto treatmentDto, Doctor doctor, Patient patient) {
        Treatment treatment = treatmentMapper.mapTreatmentDtoToTreatment(treatmentDto);
        treatment.setDoctor(doctor);
        treatment.setPatient(patient);
        return treatmentRepository.save(treatment);
    }

}
