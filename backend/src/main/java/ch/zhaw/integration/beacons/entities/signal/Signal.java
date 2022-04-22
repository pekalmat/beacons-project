package ch.zhaw.integration.beacons.entities.signal;

import ch.zhaw.integration.beacons.entities.beacon.Beacon;
import ch.zhaw.integration.beacons.entities.device.Device;
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
public class Signal implements Serializable, Comparable<Signal> {

    private static final long serialVersionUID = 1L;
    private static final String ID_SEQ = "signal_id_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ)
    @SequenceGenerator(name = ID_SEQ, sequenceName = ID_SEQ, allocationSize = 1)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Beacon beacon;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Device device;

    private Date signalTimestamp;

    private String uuid;

    private String major;

    private String minor;

    private Integer serviceUuid;

    private String bluetoothAddress;

    private String bluetoothName;

    private Integer beaconTypeCode;

    private String parserIdentifier;

    private Integer txPower;

    private Integer rssi;

    private Double runningAverageRssi;

    private Double distance;

    private Double calculatedDistance;

    private Double calculatedDistanceSlidingWindow;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getSignalTimestamp() {
        return signalTimestamp;
    }

    public void setSignalTimestamp(Date signalTimestamp) {
        this.signalTimestamp = signalTimestamp;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public Integer getServiceUuid() {
        return serviceUuid;
    }

    public void setServiceUuid(Integer serviceUuid) {
        this.serviceUuid = serviceUuid;
    }

    public String getBluetoothAddress() {
        return bluetoothAddress;
    }

    public void setBluetoothAddress(String bluetoothAddress) {
        this.bluetoothAddress = bluetoothAddress;
    }

    public String getBluetoothName() {
        return bluetoothName;
    }

    public void setBluetoothName(String bluetoothName) {
        this.bluetoothName = bluetoothName;
    }

    public Integer getBeaconTypeCode() {
        return beaconTypeCode;
    }

    public void setBeaconTypeCode(Integer beaconTypeCode) {
        this.beaconTypeCode = beaconTypeCode;
    }

    public String getParserIdentifier() {
        return parserIdentifier;
    }

    public void setParserIdentifier(String parserIdentifier) {
        this.parserIdentifier = parserIdentifier;
    }

    public Integer getTxPower() {
        return txPower;
    }

    public void setTxPower(Integer txPower) {
        this.txPower = txPower;
    }

    public Integer getRssi() {
        return rssi;
    }

    public void setRssi(Integer rssi) {
        this.rssi = rssi;
    }

    public Double getRunningAverageRssi() {
        return runningAverageRssi;
    }

    public void setRunningAverageRssi(Double runningAverageRssi) {
        this.runningAverageRssi = runningAverageRssi;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Beacon getBeacon() {
        return beacon;
    }

    public void setBeacon(Beacon beacon) {
        this.beacon = beacon;
    }

    public Double getCalculatedDistance() {
        return calculatedDistance;
    }

    public void setCalculatedDistance(Double calculatedDistance) {
        this.calculatedDistance = calculatedDistance;
    }

    public Double getCalculatedDistanceSlidingWindow() {
        return calculatedDistanceSlidingWindow;
    }

    public void setCalculatedDistanceSlidingWindow(Double calculatedDistanceSlidingWindow) {
        this.calculatedDistanceSlidingWindow = calculatedDistanceSlidingWindow;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    @Override
    public int compareTo(Signal o) {
        return getSignalTimestamp().compareTo(o.getSignalTimestamp());
    }
}
