package ch.zhaw.integration.beacons.rest.trackingdata;

import java.io.Serializable;
import java.util.List;

public class TrackingPutRequestData implements Serializable {

    private Long doctorId;
    private List<BeaconTrackingData> beaconTrackingDataList;

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public List<BeaconTrackingData> getBeaconTrackingDataList() {
        return beaconTrackingDataList;
    }

    public void setBeaconTrackingDataList(List<BeaconTrackingData> beaconTrackingDataList) {
        this.beaconTrackingDataList = beaconTrackingDataList;
    }
}
