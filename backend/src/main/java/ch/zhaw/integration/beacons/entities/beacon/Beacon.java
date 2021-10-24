package ch.zhaw.integration.beacons.entities.beacon;

import ch.zhaw.integration.beacons.entities.bed.Bed;
import ch.zhaw.integration.beacons.entities.trackingdata.TrackingData;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.util.List;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Beacon implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private Bed bed;

    @OneToMany(mappedBy = "beacon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TrackingData> trackingDataList;

    @Column(nullable = false)
    private String uid;

    @Column(nullable = false)
    private String minor;

    @Column(nullable = false)
    private String major;

    public Long getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public Bed getBed() {
        return bed;
    }

    public void setBed(Bed bed) {
        this.bed = bed;
    }

    public List<TrackingData> getTrackingDataList() {
        return trackingDataList;
    }

    public void setTrackingDataList(List<TrackingData> trackingDataList) {
        this.trackingDataList = trackingDataList;
    }

    public void setUid(String uid) {
        this.uid = uid;
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