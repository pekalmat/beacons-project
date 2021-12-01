package ch.zhaw.integration.beacons.entities.patient;

import ch.zhaw.integration.beacons.entities.bed.Bed;
import ch.zhaw.integration.beacons.entities.treatment.Treatment;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Patient implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String ID_SEQ = "patient_id_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ)
    @SequenceGenerator(name=ID_SEQ,  allocationSize = 100)
    private Long id;

    @NotNull
    private String firstName;

    @NotNull
    private String surname;

    @NotNull
    private String ahvNr;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Treatment> treatments;

    @OneToOne(cascade = CascadeType.ALL)
    private Bed bed;

    public Long getId() {
        return id;
    }

    public List<Treatment> getTreatments() {
        return treatments;
    }

    public void setTreatments(List<Treatment> treatments) {
        this.treatments = treatments;
    }

    public Bed getBed() {
        return bed;
    }

    public void setBed(Bed bed) {
        this.bed = bed;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAhvNr() {
        return ahvNr;
    }

    public void setAhvNr(String ahvNr) {
        this.ahvNr = ahvNr;
    }
}
