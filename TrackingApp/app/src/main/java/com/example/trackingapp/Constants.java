package com.example.trackingapp;

import org.altbeacon.beacon.Region;

public abstract class Constants {

    //
    // REST-API
    //
    //public static final String HOST = "https://beaconsserver.herokuapp.com"; // heroku deployment host
    public static final String HOST = "http://192.168.1.188:8081"; // lokal deployment host
    public static final String POST_NEW_SIGNALS_REQUEST_URL = HOST + "/beacons/api/internal/signals";
    public static final String PUT_REGISTER_DEVICE_REQUEST_URL = HOST + "/beacons/api/internal/devices";
    public static final String MOCK_LOGIN_REQUEST_URL = HOST + "/beacons/api/public/admins/login";

    //
    // BEACON_REGION
    //
    public static final Region BEACON_REGION = new Region("beaconRegion", null, null, null);

    //
    // Permissions
    //
    public static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    public static final int PERMISSION_REQUEST_BACKGROUND_LOCATION = 2;

    //
    // Application Status Constants
    //
    public static final String LOGIN_IN_PROGRESS_STATUS = "Login in progress...";
    public static final String INITIALIZING_BEACON_SCANNER = "Initializing Beacon Scanner";
    public static final String RANGING_PAUSED_APP_STATUS = "Ranging Paused";
    public static final String RANGING_RUNNING_APP_STATUS = "Ranging Running";
    public static final String LOG_ON_CREATE = "OnCreate";
    public static final String LOG_SCANNER_SETUP = "setting up background monitoring in app onCreate";
    public static final String RANGING_STARTED = "Ranging Started";
    public static final String LOGIN_SUCCESSFUL = "Login succesful -> Registering Device now...";
    public static final String ERROR_LOGIN_HEADER_EXTRACTION = "Login failed - Could not extract Auth-Header from request response";
    public static final String ERROR_LOGIN_FAILED = "Login Request Failed";
    public static final String ERROR_LOGIN_FAILED_REQUEST_BODY = "Login Failed creating request body";
    public static final String REGISTER_DEVICE_SUCCESSFUL = "RegisterDeviceRequest successful";
    public static final String ERROR_REGISTER_DEVICE_FAILED = "RegisterDeviceRequest Failed";
    public static final String ERROR_REGISTER_DEVICE_REQUEST_BODY = "RegisterDeviceRequest Failed creating request body";
    public static final String ERROR_SIGNAL_REQUEST_BODY = "Error creating request body for sending signal data";
    public static final String ERROR_SIGNAL_POST_REQUEST = "Failed to send Signal data to Server";
    public static final String NO_ERRORS = "---NoErrors...";
    public static final String RETRY_LOGIN= "Retrying to Login";
    public static final String COLLECTED_SIGNALS_COUNT = "Collected Signals Count: ";

    //
    // Logs
    //
    public static final String LOG_CLEAR_ERRORS_BUTTON_CLICKED = "clear ErrorLog button clicked";
    public static final String LOG_RETRY_LOGIN_BUTTON_CLICKED = "retry login button clicked";
    public static final String LOG_RANGING_BUTTON_CLICKED = "Ranging Button is clicked";
    public static final String LOG_POST_SIGNAL_DATA_SUCCESSFUL = "Send Tracking Data to server: Number of collected Beacon-Signals:";
    public static final String LOG_POST_REQUEST_ERROR = "PostRequestError: ";
    public static final String LOG_POST_REQUEST_RESPONSE = "PostRequestResponse is: ";
    public static final String LOG_WARN_RANGING_BUT_NOT_COLLECTING = " !!! NOT collecting Data -> Waiting for LOGIN and DEVICE REGISTRATION response! But Did Range Beacons in Region: Count: ";
    public static final String LOG_JSON_EXCEPTION = "Error Creating RequestBody: ";
    public static final String LOG_RANGED_BEACONS_COUNT = "Did Range Beacons in Region: Count: ";
    public static final String LOG_REGISTER_DEVICE_ERROR = "Could not perform Register Device on startup.";
    public static final String LOG_TRYING_REGISTER_DEVICE = "Trying to RegisterDevice";
    public static final String LOG_REGISTER_DEVICE_REQUEST_ERROR = "RegisterDeviceRequestError is: ";
    public static final String LOG_REGISTER_DEVICE_SUCCESSFUL = "RegisterDeviceRequest successful!";
    public static final String LOG_TRYING_LOGIN = "Trying to MockLogin";
    public static final String LOG_LOGIN_ERROR = "MockLoginRequestError is: ";
    public static final String LOG_LOGIN_AUTH_HEADER_ERROR = "Could not extract Authorization header from MockLoginRequest: error: ";
    public static final String LOG_LOGIN_SUCCESSFUL = "MockLoginRequestResponse successful!";


}
