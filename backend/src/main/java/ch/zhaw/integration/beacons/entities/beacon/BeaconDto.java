package ch.zhaw.integration.beacons.entities.beacon;

import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;

public class BeaconDto extends RepresentationModel<BeaconDto> implements Serializable {

    private Long id;
    private String uuid;
    private String major;
    private String minor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }
}
