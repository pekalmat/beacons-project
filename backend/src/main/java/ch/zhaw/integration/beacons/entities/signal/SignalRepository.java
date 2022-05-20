package ch.zhaw.integration.beacons.entities.signal;

import ch.zhaw.integration.beacons.entities.beacon.Beacon;
import ch.zhaw.integration.beacons.entities.device.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface SignalRepository extends JpaRepository<Signal, Long> {

    List<Signal> findAllByDeviceAndSignalTimestampBetween(Device device, Date signalTimestampStart, Date signalTimestampEnd);

    List<Signal> findAllByMajorAndMinor(String major, String minor);

    // select count(*) from public.signal where signal_timestamp >= '2022-04-17 18:27:00' and signal_timestamp <= '2022-04-17 18:30:00' and major like ''and minor like ''
    long countByBeaconAndSignalTimestampBetween(Beacon beacon, Date signalTimestampStart, Date signalTimestampEnd);

}
