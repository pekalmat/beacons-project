package ch.zhaw.integration.beacons.entities.treatment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface TreatmentRepository extends JpaRepository<Treatment, Long> {

    List<Treatment> findAllByDoctorAndStartTimeAfter(Date date);
}
