package ch.zhaw.integration.beacons.entities.route;

import ch.zhaw.integration.beacons.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.util.Date;

public class RouteDto extends RepresentationModel<RouteDto> implements Serializable {

    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.YYYY_MM_DD_HH_MM_SS_SSS)
    private Date routeStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.YYYY_MM_DD_HH_MM_SS_SSS)
    private Date routeEnd;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.YYYY_MM_DD_HH_MM_SS_SSS)
    private Date calculationTriggerTime;
    private String calculationMethod;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCalculationMethod() {
        return calculationMethod;
    }

    public void setCalculationMethod(String calculationMethod) {
        this.calculationMethod = calculationMethod;
    }
}
