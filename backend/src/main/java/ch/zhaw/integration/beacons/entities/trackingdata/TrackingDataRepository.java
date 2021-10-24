package ch.zhaw.integration.beacons.entities.trackingdata;

import ch.zhaw.integration.beacons.entities.doctor.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackingDataRepository extends JpaRepository<TrackingData, Long> {

    List<TrackingData> findAllByDoctorAndProcessedFalse(Doctor doctor);

}
