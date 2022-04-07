package ch.zhaw.integration.beacons.entities.position;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.io.Serializable;
import java.util.Date;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Position implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String ID_SEQ = "position_id_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ)
    @SequenceGenerator(name = ID_SEQ, sequenceName = ID_SEQ, allocationSize = 1)
    private Long id;

    private String floors;
    private Integer estimatedFloor;
    private double xCoordinate;
    private double yCoordinate;

    private double xCoordinateBasedOnLibraryDistance;
    private double yCoordinateBasedOnLibraryDistance;

    private Date calculationTriggerTime;
    private String calculationMethod;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFloors() {
        return floors;
    }

    public void setFloors(String floors) {
        this.floors = floors;
    }

    public Integer getEstimatedFloor() {
        return estimatedFloor;
    }

    public void setEstimatedFloor(Integer estimatedFloor) {
        this.estimatedFloor = estimatedFloor;
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

    public double getxCoordinateBasedOnLibraryDistance() {
        return xCoordinateBasedOnLibraryDistance;
    }

    public void setxCoordinateBasedOnLibraryDistance(double xCoordinateBasedOnLibraryDistance) {
        this.xCoordinateBasedOnLibraryDistance = xCoordinateBasedOnLibraryDistance;
    }

    public double getyCoordinateBasedOnLibraryDistance() {
        return yCoordinateBasedOnLibraryDistance;
    }

    public void setyCoordinateBasedOnLibraryDistance(double yCoordinateBasedOnLibraryDistance) {
        this.yCoordinateBasedOnLibraryDistance = yCoordinateBasedOnLibraryDistance;
    }

    public Date getCalculationTriggerTime() {
        return calculationTriggerTime;
    }

    public void setCalculationTriggerTime(Date calculationTriggerTime) {
        this.calculationTriggerTime = calculationTriggerTime;
    }

    public String getCalculationMethod() {
        return calculationMethod;
    }

    public void setCalculationMethod(String calculationMethod) {
        this.calculationMethod = calculationMethod;
    }

}
