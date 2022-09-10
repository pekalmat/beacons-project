package ch.zhaw.integration.beacons.entities.device;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository  extends JpaRepository<Device, Long> {

    Device findByFingerPrint(String fingerPrint);

}
