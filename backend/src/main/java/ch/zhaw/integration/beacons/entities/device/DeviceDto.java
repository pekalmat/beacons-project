package ch.zhaw.integration.beacons.entities.device;

import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;

public class DeviceDto extends RepresentationModel<DeviceDto> implements Serializable {

    private Long id;
    private String fingerPrint;
    private String manufacturer;
    private String brand;
    private String model;
    private Integer sdk;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFingerPrint() {
        return fingerPrint;
    }

    public void setFingerPrint(String fingerPrint) {
        this.fingerPrint = fingerPrint;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getSdk() {
        return sdk;
    }

    public void setSdk(Integer sdk) {
        this.sdk = sdk;
    }
}
