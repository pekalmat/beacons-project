package ch.zhaw.integration.beacons.rest.treatment;

import java.io.Serializable;
import java.util.Date;

public class BeaconTrackingData implements Serializable {

    private String beaconUid;
    private String major;
    private String minor;
    private Date treatmentStart;
    private Date treatmentEnd;

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

    public Date getTreatmentStart() {
        return treatmentStart;
    }

    public void setTreatmentStart(Date treatmentStart) {
        this.treatmentStart = treatmentStart;
    }

    public Date getTreatmentEnd() {
        return treatmentEnd;
    }

    public void setTreatmentEnd(Date treatmentEnd) {
        this.treatmentEnd = treatmentEnd;
    }
}
