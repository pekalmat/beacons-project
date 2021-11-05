package ch.zhaw.integration.beacons.entities.person;

import ch.zhaw.integration.beacons.entities.person.doctor.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Doctor findByEmail(String email);

}
