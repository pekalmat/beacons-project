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
    @SequenceGenerator(name = ID_SEQ, sequenceName = ID_SEQ, allocationSize = 1)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private Bed bed;

    @Column(nullable = false)
    private String uuid;

    @Column(nullable = false)
    private String minor;

    @Column(nullable = false)
    private String major;

    private String name;
    private String geopos;
    private String registerSource;
    private String standort;
    private String geoposition;
    private String floor;
    private double xCoordinate;
    private double yCoordinate;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegisterSource() {
        return registerSource;
    }

    public void setRegisterSource(String registerSource) {
        this.registerSource = registerSource;
    }

    public String getGeopos() {
        return geopos;
    }

    public void setGeopos(String geopos) {
        this.geopos = geopos;
    }

    public String getStandort() {
        return standort;
    }

    public void setStandort(String standort) {
        this.standort = standort;
    }

    public String getGeoposition() {
        return geoposition;
    }

    public void setGeoposition(String geoposition) {
        this.geoposition = geoposition;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public double getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(double xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public double getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(double yCoordinate) {
        this.yCoordinate = yCoordinate;
    }
}