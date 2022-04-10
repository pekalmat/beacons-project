package ch.zhaw.integration.beacons.entities.position;

import ch.zhaw.integration.beacons.entities.route.Route;
import ch.zhaw.integration.beacons.entities.signal.Signal;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import java.io.Serializable;
import java.util.Date;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Position implements Serializable, Comparable<Position>{

    private static final long serialVersionUID = 1L;
    private static final String ID_SEQ = "position_id_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ)
    @SequenceGenerator(name = ID_SEQ, sequenceName = ID_SEQ, allocationSize = 1)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Route route;

    private String floors;

    private Integer estimatedFloor;

    private double xCoordinate;

    private double yCoordinate;

    private Date positionTimestamp;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Signal signal1;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Signal signal2;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Signal signal3;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
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

    public Date getPositionTimestamp() {
        return positionTimestamp;
    }

    public void setPositionTimestamp(Date positionTimestamp) {
        this.positionTimestamp = positionTimestamp;
    }

    public Signal getSignal1() {
        return signal1;
    }

    public void setSignal1(Signal signal1) {
        this.signal1 = signal1;
    }

    public Signal getSignal2() {
        return signal2;
    }

    public void setSignal2(Signal signal2) {
        this.signal2 = signal2;
    }

    public Signal getSignal3() {
        return signal3;
    }

    public void setSignal3(Signal signal3) {
        this.signal3 = signal3;
    }

    @Override
    public int compareTo(Position o) {
        return getPositionTimestamp().compareTo(o.getPositionTimestamp());
    }

}
