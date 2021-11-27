package ch.zhaw.integration.beacons.entities.beacon;

import java.io.Serializable;

public class BeaconDto implements Serializable {

    private String beaconUid;
    private String major;
    private String minor;

    public String getBeaconUid() {
        return beaconUid;
    }

    public void setBeaconUid(String beaconUid) {
        this.beaconUid = beaconUid;
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
