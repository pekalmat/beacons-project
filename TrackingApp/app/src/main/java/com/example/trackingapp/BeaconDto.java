package com.example.trackingapp;

import java.io.Serializable;

public class BeaconDto implements Serializable {

    private String beaconUid;
    private String major;
    private String minor;

    public BeaconDto(){
    }

    public BeaconDto(String beaconUid, String major, String minor) {
        this.beaconUid = beaconUid;
        this.major = major;
        this.minor = minor;
    }

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
