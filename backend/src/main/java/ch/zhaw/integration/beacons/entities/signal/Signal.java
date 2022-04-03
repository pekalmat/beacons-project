package ch.zhaw.integration.beacons.entities.signal;

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
public class Signal implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String ID_SEQ = "signal_id_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ)
    @SequenceGenerator(name=ID_SEQ,  allocationSize = 100)
    private Long id;
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
