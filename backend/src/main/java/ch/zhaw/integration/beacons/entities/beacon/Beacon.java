package ch.zhaw.integration.beacons.entities.beacon;

import ch.zhaw.integration.beacons.entities.bed.Bed;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import java.io.Serializable;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Beacon implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String ID_SEQ = "beacon_id_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ)
    @SequenceGenerator(name=ID_SEQ,  allocationSize = 100)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private Bed bed;

    @Column(nullable = false)
    private String uuid;

    @Column(nullable = false)
    private String minor;

    @Column(nullable = false)
    private String major;

    public Long getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public Bed getBed() {
        return bed;
    }

    public void setBed(Bed bed) {
        this.bed = bed;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }
}