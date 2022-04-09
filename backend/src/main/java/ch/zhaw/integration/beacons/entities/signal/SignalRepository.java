package ch.zhaw.integration.beacons.entities.signal;

import ch.zhaw.integration.beacons.entities.device.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface SignalRepository extends JpaRepository<Signal, Long> {

    List<Signal> findAllByDeviceAndSignalTimestampBetween(Device device, Date signalTimestampStart, Date signalTimestampEnd);

    List<Signal> findAllByMajorAndMinor(String major, String minor);

}
