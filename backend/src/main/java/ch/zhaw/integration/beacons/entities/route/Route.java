package ch.zhaw.integration.beacons.entities.route;

import ch.zhaw.integration.beacons.entities.device.Device;
import ch.zhaw.integration.beacons.entities.position.Position;
import ch.zhaw.integration.beacons.utils.CalculationMethod;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Route implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String ID_SEQ = "route_id_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ)
    @SequenceGenerator(name = ID_SEQ, sequenceName = ID_SEQ, allocationSize = 1)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Device device;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Position> positions;

    private Date routeStart;

    private Date routeEnd;

    private Date calculationTriggerTime;

    @Enumerated(EnumType.STRING)
    private CalculationMethod calculationMethod;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }

    public Date getRouteStart() {
        return routeStart;
    }

    public void setRouteStart(Date routeStart) {
        this.routeStart = routeStart;
    }

    public Date getRouteEnd() {
        return routeEnd;
    }

    public void setRouteEnd(Date routeEnd) {
        this.routeEnd = routeEnd;
    }

    public Date getCalculationTriggerTime() {
        return calculationTriggerTime;
    }

    public void setCalculationTriggerTime(Date calculationTriggerTime) {
        this.calculationTriggerTime = calculationTriggerTime;
    }

    public CalculationMethod getCalculationMethod() {
        return calculationMethod;
    }

    public void setCalculationMethod(CalculationMethod calculationMethod) {
        this.calculationMethod = calculationMethod;
    }
}
