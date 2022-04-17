package com.example.trackingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.trackingapp.requests.CustomJsonArrayRequest;
import com.example.trackingapp.requests.CustomJsonObjectRequest;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SentryLevel;

import static com.example.trackingapp.constants.Constants.*;

public class MainActivity extends AppCompatActivity  implements  RangeNotifier {

    // Logger-TAG
    private static final String TAG =  "########## MainActivity ##########";

    // API-Request
    private String sessionBearerToken;
    private RequestQueue requestQueue;
    private String deviceFingerPrint;
    // Beacon-Manager / Scanner
    private BeaconManager beaconManager;
    private static Long detectedSignalsCount;
    private long beaconManagerScanPeriod;
    // RANGING-STATUS ON/OFF
    private boolean currentlyRanging;
    //
    private SimpleDateFormat dateFormat;

    //
    //
    // App-Init
    //
    //
    @SuppressLint("SimpleDateFormat")
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.i(TAG, LOG_ON_CREATE);
        setContentView(R.layout.activity_main);
        createDefaultUncaughtExceptionHandler();

        detectedSignalsCount = 0L;
        beaconManagerScanPeriod = 100;
        currentlyRanging = false;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS'Z'");
        requestQueue = Volley.newRequestQueue(this);

        verifyBluetooth();
        requestPermissions();

        // Mock a Login because of authorization check for data fetching/manipulation apis
        updateApplicationStatusText(LOGIN_IN_PROGRESS_STATUS);
        requestMockLoginToGetBearerTokenAndRegisterDevice();

        // Setup beaconManager
        initializeBeaconManager();
    }

    private void createDefaultUncaughtExceptionHandler() {
        // https://stackoverflow.com/questions/27829955/android-handle-application-crash-and-start-a-particular-activity
        Thread.setDefaultUncaughtExceptionHandler(
                (thread, e) -> Sentry.captureMessage("UncaughtExceptionFound:" + Arrays.toString(e.getStackTrace()), SentryLevel.ERROR)
        );
    }

    private void initializeBeaconManager() {
        updateApplicationStatusText(INITIALIZING_BEACON_SCANNER);
        beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);
        // By default the AndroidBeaconLibrary will only find AltBeacons.  f you wish to make it
        // find a different type of beacon, you must specify the byte layout for that beacon's
        // advertisement with a line like below.  The example shows how to find a beacon with the
        // same byte layout as AltBeacon but with a beaconTypeCode of 0xaabb.  To find the proper
        // layout expression for other beacon types, do a web search for "setBeaconLayout"
        // including the quotes.
        //cut out (,d:25-25)
        beaconManager.getBeaconParsers().clear();

        // add BeaconParser for     //      IBEACON             "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24")));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")));
        // add BeaconParser for     //      ALTBEACON           "m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"
        beaconManager.getBeaconParsers().add( new BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));
        // add BeaconParser for     //      EDDYSTONE  UID      "s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"
        beaconManager.getBeaconParsers().add( new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        // add BeaconParser for     //      EDDYSTONE  TLM      "x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15"
        beaconManager.getBeaconParsers().add( new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        // add BeaconParser for     //      EDDYSTONE  URL      "s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v"
        beaconManager.getBeaconParsers().add( new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        BeaconManager.setDebug(false);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Scanning for Beacons");
        Intent intent = new Intent(this, MainActivity.class);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationChannel channel = new NotificationChannel("My Notification Channel ID", "My Notification Name", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("My Notification Channel Description");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
        builder.setChannelId(channel.getId());

        beaconManager.enableForegroundServiceScanning(builder.build(), 456);
        beaconManager.setEnableScheduledScanJobs(false);
        beaconManager.setBackgroundBetweenScanPeriod(0);
        beaconManager.setBackgroundScanPeriod(beaconManagerScanPeriod);
        beaconManager.setForegroundBetweenScanPeriod(0);
        beaconManager.setForegroundScanPeriod(beaconManagerScanPeriod);

        Log.i(TAG, LOG_SCANNER_SETUP);

        // If we were monitoring *different* regions on the last run of this app, they will be
        // remembered.  In this case we need to disable them here
        for (Region region: beaconManager.getMonitoredRegions()) {
            beaconManager.stopMonitoring(region);
        }
    }

    private void requestMockLoginToGetBearerTokenAndRegisterDevice() {
        try {
            JSONObject jsonBody = new JSONObject()
                .put("email", "admin@example.com")
                .put("password", "admin");
            CustomJsonObjectRequest mockLoginRequest = new CustomJsonObjectRequest(
                    Request.Method.POST,
                    MOCK_LOGIN_REQUEST_URL,
                    null,
                    jsonBody,
                    response -> {
                        Log.i(TAG, LOG_LOGIN_SUCCESSFUL);
                        try {
                            JSONObject headers = (JSONObject) response.get("headers");
                            String bearerToken = (String) headers.get("Authorization");
                            sessionBearerToken = "Bearer " + bearerToken;
                            updateApplicationStatusText(LOGIN_SUCCESSFUL);
                            requestRegisterDevice();
                        } catch (JSONException e) {
                            updateApplicationStatusText(ERROR_LOGIN_HEADER_EXTRACTION);
                            logErrorToDisplay(e.toString());
                            Log.e(TAG, LOG_LOGIN_AUTH_HEADER_ERROR + e);
                            Sentry.captureMessage(LOG_LOGIN_AUTH_HEADER_ERROR + Arrays.toString(e.getStackTrace()), SentryLevel.ERROR);
                        }
                    },
                    error -> {
                        updateApplicationStatusText(ERROR_LOGIN_FAILED);
                        logErrorToDisplay(error.toString());
                        Log.e(TAG, LOG_LOGIN_ERROR + error);
                        Sentry.captureMessage(LOG_LOGIN_ERROR + Arrays.toString(error.getStackTrace()), SentryLevel.ERROR);
                    }
            );
            Log.i(TAG, LOG_TRYING_LOGIN);
            requestQueue.add(mockLoginRequest);
        } catch (JSONException e) {
            updateApplicationStatusText(ERROR_LOGIN_FAILED_REQUEST_BODY);
            logErrorToDisplay(e.toString());
            Log.e(TAG,ERROR_LOGIN_FAILED_REQUEST_BODY, e);
            Sentry.captureMessage(ERROR_LOGIN_FAILED_REQUEST_BODY + Arrays.toString(e.getStackTrace()), SentryLevel.ERROR);
        }
    }

    private void requestRegisterDevice() {
        try {
            String fingerPrint = Build.FINGERPRINT;
            JSONObject jsonBody = new JSONObject()
                    .put("fingerPrint", fingerPrint)
                    .put("manufacturer", Build.MANUFACTURER)
                    .put("brand", Build.BRAND)
                    .put("model", Build.MODEL)
                    .put("sdk", Build.VERSION.SDK_INT);
            CustomJsonObjectRequest registerDeviceRequest = new CustomJsonObjectRequest(
                    Request.Method.POST,
                    PUT_REGISTER_DEVICE_REQUEST_URL,
                    sessionBearerToken,
                    jsonBody,
                    response -> {
                        updateApplicationStatusText(REGISTER_DEVICE_SUCCESSFUL);
                        Log.i(TAG, LOG_REGISTER_DEVICE_SUCCESSFUL);
                        deviceFingerPrint = fingerPrint;
                    },
                    error -> {
                        updateApplicationStatusText(ERROR_REGISTER_DEVICE_FAILED);
                        logErrorToDisplay(error.toString());
                        //Log.e(TAG, LOG_REGISTER_DEVICE_REQUEST_ERROR + error);
                        Sentry.captureMessage(LOG_REGISTER_DEVICE_REQUEST_ERROR + Arrays.toString(error.getStackTrace()), SentryLevel.ERROR);
                    }
            );
            Log.i(TAG, LOG_TRYING_REGISTER_DEVICE);
            requestQueue.add(registerDeviceRequest);
        } catch (JSONException e) {
            updateApplicationStatusText(ERROR_REGISTER_DEVICE_REQUEST_BODY);
            logErrorToDisplay(e.toString());
            //Log.e(TAG, LOG_REGISTER_DEVICE_ERROR, e);
            Sentry.captureMessage(LOG_REGISTER_DEVICE_ERROR + Arrays.toString(e.getStackTrace()), SentryLevel.ERROR);

        }
    }

    //
    //
    // Signal Data Collecting and Sending
    //
    //

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        ITransaction sentryTransaction = Sentry.startTransaction("rangNotifierTransaction", "collectingSignals");
        Date detectionTimestamp = Calendar.getInstance().getTime();
        processBeaconSignalsAsynchronously(beacons, detectionTimestamp);
        logScannedSignalCountToDisplay();
        sentryTransaction.finish();
    }

    private void processBeaconSignalsAsynchronously(Collection<Beacon> beacons, Date detectionTimestamp) {
        String signalTimestampString = dateFormat.format(detectionTimestamp);
        new Thread(() -> {
            if (sessionBearerToken != null & deviceFingerPrint != null) {
                //Log.d(TAG,  LOG_RANGED_BEACONS_COUNT + beacons.size());
                if (beacons.size() != 0) {
                    detectedSignalsCount += beacons.size();
                    collectAndSendSignals(beacons, signalTimestampString);
                    logScannedSignalCountToDisplay();
                }
            } else {
                Log.w(TAG, LOG_WARN_RANGING_BUT_NOT_COLLECTING + beacons.size());
            }
        }).start();
    }

    private void collectAndSendSignals(Collection<Beacon> beacons, String signalTimestampString) {
        JSONArray jsonArray = collectAllBeaconSignalsAndConvertToJsonArray(beacons, signalTimestampString);
        createAndSendSignalsPostRequest(jsonArray);
    }

    private JSONArray collectAllBeaconSignalsAndConvertToJsonArray(Collection<Beacon> beacons, String signalTimestampString) {
        JSONArray result = new JSONArray();
        for (Beacon beacon: beacons) {
            // Collect all Beacon Signals and create JsonString for Each
            try {
                JSONObject beaconSignalJson = new JSONObject()
                        .put("signalTimestamp", signalTimestampString)
                        .put("serviceUuid", beacon.getServiceUuid())
                        .put("uuid", beacon.getId1().toString())
                        .put("major", beacon.getId2().toString())
                        .put("minor", beacon.getId3().toString())
                        .put("bluetoothAddress", beacon.getBluetoothAddress())
                        .put("bluetoothName", beacon.getBluetoothName())
                        .put("beaconTypeCode", beacon.getBeaconTypeCode())
                        .put("parserIdentifier", beacon.getParserIdentifier())
                        .put("txPower", beacon.getTxPower())
                        .put("rssi", beacon.getRssi())
                        .put("runningAverageRssi", beacon.getRunningAverageRssi())
                        .put("distance", beacon.getDistance())
                        .put("deviceFingerPrint", deviceFingerPrint);
                result.put(beaconSignalJson);
            } catch (JSONException e) {
                logErrorToDisplay(ERROR_SIGNAL_REQUEST_BODY);
                //Log.e(TAG, LOG_JSON_EXCEPTION + e);
                Sentry.captureMessage(LOG_JSON_EXCEPTION + Arrays.toString(e.getStackTrace()), SentryLevel.ERROR);
            }
        }
        return result;
    }

    private void createAndSendSignalsPostRequest(JSONArray jsonArray) {
        CustomJsonArrayRequest postNewSignalsListRequest = new CustomJsonArrayRequest(
                Request.Method.POST,
                POST_NEW_SIGNALS_REQUEST_URL,
                sessionBearerToken,
                jsonArray,
                response -> {// TODO: get new token and update session token//Log.d(TAG, LOG_POST_REQUEST_RESPONSE + response.toString());
                },
                error -> {
                    logErrorToDisplay(ERROR_SIGNAL_POST_REQUEST);
                    //Log.e(TAG, LOG_POST_REQUEST_ERROR + error.toString());
                    Sentry.captureMessage(LOG_POST_REQUEST_ERROR + Arrays.toString(error.getStackTrace()), SentryLevel.ERROR);
                }
        );
        //Log.d(TAG, LOG_POST_SIGNAL_DATA_SUCCESSFUL + postRequestBody.length());
        requestQueue.add(postNewSignalsListRequest);
    }

    //
    //
    // Bluetooth & Permissions
    //
    //

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_FINE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "fine location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(dialog -> {
                    });
                    builder.show();
                }
                return;
            }
            case PERMISSION_REQUEST_BACKGROUND_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "background location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since background location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(dialog -> { });
                    builder.show();
                }
            }
        }
    }

    private void requestPermissions() {
        if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (!this.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("This app needs background location access");
                    builder.setMessage("Please grant location access so this app can detect beacons in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(dialog -> requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, PERMISSION_REQUEST_BACKGROUND_LOCATION));
                    builder.show();
                }
                else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since background location access has not been granted, this app will not be able to discover beacons in the background.  Please go to Settings -> Applications -> Permissions and grant background location access to this app.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(dialog -> { });
                    builder.show();
                }
            }
        } else {
            if (!this.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, PERMISSION_REQUEST_FINE_LOCATION);
            }
            else {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Functionality limited");
                builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons.  Please go to Settings -> Applications -> Permissions and grant location access to this app.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(dialog -> { });
                builder.show();
            }
        }
    }

    private void verifyBluetooth() {
        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Bluetooth not enabled");
                builder.setMessage("Please enable bluetooth in settings and restart this application.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(dialog -> finishAffinity());
                builder.show();
            }
        } catch (RuntimeException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not available");
            builder.setMessage("Sorry, this device does not support Bluetooth LE.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(dialog -> finishAffinity());
            builder.show();

        }

    }

    //
    //
    // BUTTON-Listener Methods
    //
    //

    public void increaseScanRateButtonListener(View view) {
        Log.i(TAG, LOG_INCREASE_SCAN_RATE_BUTTON_CLICKED);
        if (beaconManagerScanPeriod < 1000) {
            beaconManagerScanPeriod = beaconManagerScanPeriod + 100;
        } else {
            beaconManagerScanPeriod = 100;
        }
        updateCurrentScanningRateTest(String.valueOf(beaconManagerScanPeriod));
        initializeBeaconManager();
        updateApplicationStatusText(SCAN_RATE_CHANGED_TO + beaconManagerScanPeriod);
    }

    public void startStopRangingButtonListener(View view){
        Log.i(TAG, LOG_RANGING_BUTTON_CLICKED);
        if(!currentlyRanging) {
            beaconManager.addRangeNotifier(this);
            beaconManager.startRangingBeacons(BEACON_REGION);
            currentlyRanging = true;
            updateApplicationStatusText(RANGING_RUNNING_APP_STATUS);
            Log.i(TAG, RANGING_STARTED);

        } else{
            beaconManager.stopRangingBeacons(BEACON_REGION);
            beaconManager.removeAllRangeNotifiers();
            currentlyRanging = false;
            updateApplicationStatusText(RANGING_PAUSED_APP_STATUS);
        }
    }

    public void retryLoginButtonListener(View view) {
        Log.i(TAG, LOG_RETRY_LOGIN_BUTTON_CLICKED);
        updateApplicationStatusText(RETRY_LOGIN);
        requestMockLoginToGetBearerTokenAndRegisterDevice();
    }

    public void clearErrorLogButtonListener(View view) {
        Log.i(TAG, LOG_CLEAR_ERRORS_BUTTON_CLICKED);
        runOnUiThread(() -> {
            TextView errorLog = MainActivity.this.findViewById(R.id.errorLog);
            errorLog.setText(NO_ERRORS);
        });
    }

    //
    //
    // UI-Text-Update methods
    //
    //

    private void updateCurrentScanningRateTest(String currentRate) {
        runOnUiThread(() -> {
            TextView editText = MainActivity.this.findViewById(R.id.currentScanningRate);
            editText.setText(currentRate);
        });
    }

    private void updateApplicationStatusText(String line) {
        runOnUiThread(() -> {
            TextView editText = MainActivity.this.findViewById(R.id.applicationStatus);
            editText.setText(line);
        });
    }

    private void logScannedSignalCountToDisplay() {
        runOnUiThread(() -> {
            TextView editText = MainActivity.this.findViewById(R.id.collectedSignalCount);
            String countLogText = COLLECTED_SIGNALS_COUNT + detectedSignalsCount;
            editText.setText(countLogText);
        });
    }

    private void logErrorToDisplay(String errorMessage) {
        runOnUiThread(() -> {
            TextView errorLog = MainActivity.this.findViewById(R.id.errorLog);
            errorLog.setText(errorMessage);
        });
    }

}