package ch.zhaw.integration.beacons.entities.signal;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.util.Date;

public class SignalDto extends RepresentationModel<SignalDto> implements Serializable {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
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
    private double runningAverageRssi;
    private double distance;

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

    public double getRunningAverageRssi() {
        return runningAverageRssi;
    }

    public void setRunningAverageRssi(double runningAverageRssi) {
        this.runningAverageRssi = runningAverageRssi;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
