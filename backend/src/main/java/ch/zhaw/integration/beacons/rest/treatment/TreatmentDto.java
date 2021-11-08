package ch.zhaw.integration.beacons.rest.treatment;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

public class TreatmentDto implements Serializable {

    private Long doctorId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date treatmentStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date treatmentEnd;
    private BeaconDto beaconDto;

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
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

    public BeaconDto getBeaconDto() {
        return beaconDto;
    }

    public void setBeaconDto(BeaconDto beaconDto) {
        this.beaconDto = beaconDto;
    }
}
