package ch.zhaw.integration.beacons.entities.treatment;

import ch.zhaw.integration.beacons.entities.beacon.BeaconDto;
import ch.zhaw.integration.beacons.entities.doctor.DoctorDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.util.Date;

public class TreatmentDto extends RepresentationModel<TreatmentDto> implements Serializable {

    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    private String comment;

    // Input Only
    private DoctorDto doctor;
    private BeaconDto beacon;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public DoctorDto getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorDto doctor) {
        this.doctor = doctor;
    }

    public BeaconDto getBeacon() {
        return beacon;
    }

    public void setBeacon(BeaconDto beacon) {
        this.beacon = beacon;
    }
}
