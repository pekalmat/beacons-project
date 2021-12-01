package ch.zhaw.integration.beacons.entities.beacon;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BeaconRepository extends JpaRepository<Beacon, Long> {

    Beacon findBeaconByUuidAndMajorAndMinor(String uuid, String major, String minor);
}
