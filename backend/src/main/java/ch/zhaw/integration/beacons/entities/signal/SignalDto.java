package ch.zhaw.integration.beacons.entities.signal;

import ch.zhaw.integration.beacons.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class SignalDto extends RepresentationModel<SignalDto> implements Serializable {

    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.YYYY_MM_DD_HH_MM_SS_SSS)
    private Date signalTimestamp;
    private String uuid;
    private String major;
    private String minor;
    private int serviceUuid;
    private String bluetoothAddress;
    private String bluetoothName;
    private int beaconTypeCode;
    private String parserIdentifier;
    private int txPower;
    private int rssi;
    private Double runningAverageRssi;
    private BigDecimal distance;
    private BigDecimal calculatedDistance;
    private BigDecimal calculatedDistanceSlidingWindow;
    private String deviceFingerPrint;

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

    public int getServiceUuid() {
        return serviceUuid;
    }

    public void setServiceUuid(int serviceUuid) {
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

    public int getBeaconTypeCode() {
        return beaconTypeCode;
    }

    public void setBeaconTypeCode(int beaconTypeCode) {
        this.beaconTypeCode = beaconTypeCode;
    }

    public String getParserIdentifier() {
        return parserIdentifier;
    }

    public void setParserIdentifier(String parserIdentifier) {
        this.parserIdentifier = parserIdentifier;
    }

    public int getTxPower() {
        return txPower;
    }

    public void setTxPower(int txPower) {
        this.txPower = txPower;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public Double getRunningAverageRssi() {
        return runningAverageRssi;
    }

    public void setRunningAverageRssi(Double runningAverageRssi) {
        this.runningAverageRssi = runningAverageRssi;
    }

    public BigDecimal getDistance() {
        return distance;
    }

    public void setDistance(BigDecimal distance) {
        this.distance = distance;
    }

    public BigDecimal getCalculatedDistance() {
        return calculatedDistance;
    }

    public void setCalculatedDistance(BigDecimal calculatedDistance) {
        this.calculatedDistance = calculatedDistance;
    }

    public BigDecimal getCalculatedDistanceSlidingWindow() {
        return calculatedDistanceSlidingWindow;
    }

    public void setCalculatedDistanceSlidingWindow(BigDecimal calculatedDistanceSlidingWindow) {
        this.calculatedDistanceSlidingWindow = calculatedDistanceSlidingWindow;
    }

    public String getDeviceFingerPrint() {
        return deviceFingerPrint;
    }

    public void setDeviceFingerPrint(String deviceFingerPrint) {
        this.deviceFingerPrint = deviceFingerPrint;
    }
}
