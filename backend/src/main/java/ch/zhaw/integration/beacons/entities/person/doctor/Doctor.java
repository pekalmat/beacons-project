package ch.zhaw.integration.beacons.entities.person.doctor;

import ch.zhaw.integration.beacons.entities.person.Person;
import ch.zhaw.integration.beacons.entities.treatment.Treatment;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Doctor extends Person {

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Treatment> treatments;

    public List<Treatment> getTreatments() {
        return treatments;
    }

    public void setTreatments(List<Treatment> treatments) {
        this.treatments = treatments;
    }

}
