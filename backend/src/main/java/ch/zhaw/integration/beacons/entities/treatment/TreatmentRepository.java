package ch.zhaw.integration.beacons.entities.treatment;

import ch.zhaw.integration.beacons.entities.person.doctor.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface TreatmentRepository extends JpaRepository<Treatment, Long> {

    List<Treatment> findAllByDoctorAndStartTimeAfter(Doctor doctor, Date date);

    List<Treatment> findAllByDoctor(Doctor doctor);
}
