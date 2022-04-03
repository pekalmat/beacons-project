package com.example.trackingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements MonitorNotifier, RangeNotifier {
    protected static final String TAG =  "########## MainActivity ##########";
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_BACKGROUND_LOCATION = 2;
    private final BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);

    private static final String HOST = "http://192.168.1.188:8081";
    private static final String POST_NEW_SIGNALS_REQUEST_URL = HOST + "/beacons/api/internal/signals";
    private static final String MOCK_LOGIN_REQUEST_URL = HOST + "/beacons/api/public/doctors/login";

    private boolean currentlyRanging = false;
    private String sessionBearerToken;
    private RequestQueue requestQueue;

    //
    // APPLICATION_START_UP
    //
    // onCreate Method triggered on Application Start-Up
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        verifyBluetooth();
        requestPermissions();
        requestQueue = Volley.newRequestQueue(this);
        // Mock a Login because of authorization check for data fetching/manipulation apis
        mockLoginToGetBearerToken();
        BeaconManager.getInstanceForApplication(this).addMonitorNotifier(this);
        if (TrackingApplication.insideRegion) {
            updateText("Beacons are visible.");
        }
        else {
            updateText("No beacons are visible.");
        }
    }
    // Do Mock Login (get authorization header needed for Internal/Get/Post APIs
    private void mockLoginToGetBearerToken() {
        try {
            JSONObject jsonBody = new JSONObject("{" + "\"email\":\"doctor1@example.com\"," + "\"password\":\"doctor1\"" + "}");
            CustomJsonObjectRequest mockLoginRequest = new CustomJsonObjectRequest(
                    Request.Method.POST,
                    MOCK_LOGIN_REQUEST_URL,
                    null,
                    jsonBody,
                    response -> {
                        Log.i(TAG, "MockLoginRequestResponse is: " + response.toString());
                        try {
                            JSONObject headers = (JSONObject) response.get("headers");
                            String bearerToken = (String) headers.get("Authorization");
                            sessionBearerToken = "Bearer " + bearerToken;
                        } catch (JSONException e) {
                            Log.e(TAG, "Could not extract Authorization header from MockLoginRequest: error: " + e.getMessage());
                        }
                    },
                    error -> Log.e(TAG, "MockLoginRequestError is: " + error.getMessage())
            );
            Log.i(TAG, "Trying to MockLogin");
            requestQueue.add(mockLoginRequest);
        } catch (JSONException e) {
            Log.e(TAG,"Could not perform Mock Login on startup.", e);
        }
    }
    //
    //
    //  MAIN-LOGIC
    //
    // Range Notifier -> Main Beacon-Tracking logic
    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        try {
            JSONArray signalsJsonArray = collectAllBeaconSignalsAndConvertToJsonArray(beacons);
            createAndSendPostRequest(signalsJsonArray);
        } catch (JSONException e) {
            Log.e(TAG,"Error Creating RequestBody");
            e.printStackTrace();
        }
    }
    //
    // MAIN CODE HELPER-METHODS
    private JSONArray collectAllBeaconSignalsAndConvertToJsonArray(Collection<Beacon> beacons) throws JSONException {
        Log.e(TAG, "Did Range Beacons in Region: Count: " + beacons.size());
        Date timestamp = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") String timestampString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss'Z'").format(timestamp);
        JSONArray result = new JSONArray();
        for (Beacon beacon: beacons) {
            // Collect all Beacon Signals and create JsonString for Each
            JSONObject beaconSignalJson = new JSONObject()
                    .put("signalTimestamp", timestampString)
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
                    .put("distance", beacon.getDistance());
            result.put(beaconSignalJson);
        }
        return result;
    }

    private void createAndSendPostRequest(JSONArray postRequestBody) {
        //(jsonString.toString());
        // Create and POST Json-PostRequest
        CustomJsonArrayRequest postNewTreatmentListRequest = new CustomJsonArrayRequest(
                Request.Method.POST,
                POST_NEW_SIGNALS_REQUEST_URL,
                sessionBearerToken,
                postRequestBody,
                response -> {
                    // TODO: get new token and update session token
                    Log.i(TAG, "PostRequestResponse is: " + response.toString());
                },
                error -> Log.e(TAG, "PostRequestError: " + error.getMessage())
        );
        Log.i(TAG, "Send Tracking Data to server: Number of collected Beacon-Signals:" + postRequestBody.length());
        requestQueue.add(postNewTreatmentListRequest);
    }
    //
    //
    //  OLD-TRACKING-Helper MAIN-LOGIC-HELPER-METHODS
    //
    // Sends Post Request for Tracking Data Transmission
    /*private void sendNewTreatmentPostRequest() {
        // Create request JsonBody
        JSONObject jsonBody = createPostRequestBody();
        // Create Json-PostRequest
        CustomJsonObjectRequest postNewTreatmentRequest = new CustomJsonObjectRequest(
                Request.Method.POST,
                POST_NEW_TREATMENT_REQUEST_URL,
                sessionBearerToken,
                jsonBody,
                response -> {
                    // TODO: get new token and update session token
                    Log.i(TAG, "PostRequestResponse is: " + response.toString());
                },
                error -> Log.e(TAG, "PostRequestError: " + error.getMessage())
        );
        Log.i(TAG, "Send Tracking Data to server: " + currentlyTrackedBeacon.getUuid() + " Major: " + currentlyTrackedBeacon.getMajor() + " Minor: " + currentlyTrackedBeacon.getMinor());
        requestQueue.add(postNewTreatmentRequest);
    }*/
    //
    // Create Request body for Tracking Data Transmission
    /*private JSONObject createPostRequestBody() {
        try {
            @SuppressLint("SimpleDateFormat") String startTimeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss'Z'").format(currentTrackingStartTime);
            Date endTime = java.util.Calendar.getInstance().getTime();
            @SuppressLint("SimpleDateFormat") String endTimeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss'Z'").format(endTime);
            Gson gson = new Gson();
            String beaconJson = gson.toJson(currentlyTrackedBeacon);
            JSONObject doctorJson = new JSONObject();
            doctorJson.put("id", 2);
            return new JSONObject(
                    "{" +
                            "\"startTime\":\"" + startTimeString + "\"," +
                            "\"endTime\":\"" + endTimeString + "\"," +
                            "\"doctor\":" + doctorJson.toString() + "," +
                            "\"beacon\":" + beaconJson +
                            "}");
        } catch (JSONException e) {
            Log.e(TAG,"Error Creating RequestBody");
            e.printStackTrace();
        }
        return new JSONObject();
    }*/
    // Compare beacons -> used in Tracking logic
    /*private boolean beaconMatchesBeaconDto(Beacon beacon, BeaconDto beaconDto) {
        return beaconDto.getUuid().equals(beacon.getId1().toString())
                && beaconDto.getMajor().equals(beacon.getId2().toString())
                && beaconDto.getMinor().equals(beacon.getId3().toString());
    }*/
    // Check if beacon is registered -> used in Tracking logic
    /*private boolean beaconIsRegisteredInSystem(Beacon beacon) {
        for (BeaconDto beaconDto : knownBeaconsToTrack) {
            if (beaconMatchesBeaconDto(beacon, beaconDto)) {
                return true;
            }
        }
        return false;
    }*/
    //
    //
    //
    // SCANNING / MONITORING START METHODS
    //
    // Triggered when Ranging Button is clicked
    public void onRanging(View view){
        if(!currentlyRanging) {
            RangeNotifier rangeNotifier = (beacons, region) -> {
                if (beacons.size() > 0) {
                    Log.i(TAG, "didRangeBeaconsInRegion called with beacon count:  " + beacons.size());
                    Beacon firstBeacon = beacons.iterator().next();

                    logToDisplay("The first beacon " + firstBeacon.toString() + " is about " + firstBeacon.getDistance() + " meters away.");
                }
            };
            beaconManager.addRangeNotifier(rangeNotifier);
            beaconManager.startRangingBeacons(TrackingApplication.beaconRegion);
            currentlyRanging = true;
        } else{
            beaconManager.stopRangingBeacons(TrackingApplication.beaconRegion);
            beaconManager.removeAllRangeNotifiers();
            currentlyRanging = false;
        }
    }
    //
    // Triggered when Monitoring Button is clicked
    @SuppressLint("SetTextI18n")
    public void onScan(View view) {
        // This is a toggle.  Each time we tap it, we start or stop
        Button button = (Button) findViewById(R.id.scanButton);

        if (BeaconManager.getInstanceForApplication(this).getMonitoredRegions().size() > 0) {
            BeaconManager.getInstanceForApplication(this).stopMonitoring(TrackingApplication.beaconRegion);
            button.setText("Enable Monitoring");
            updateText("Monitoring deactivated");
        }
        else {
            updateText("Beacon not visible");
            BeaconManager.getInstanceForApplication(this).startMonitoring(TrackingApplication.beaconRegion);
            button.setText("Disable Monitoring");
        }

    }
    // Called when at least one beacon in a Region is visible from Monitor Notifier
    @Override
    public void didEnterRegion(Region region) {
        updateText("Beacon visible");
        // start ranging for beacons.  This will provide an update once per second with the estimated
        // distance to the beacon in the didRAngeBeaconsInRegion method.
        beaconManager.startRangingBeacons(new Region("myRangingUniqueId", null, null, null));
        beaconManager.addRangeNotifier(this);
    }
    //
    //
    //
    // NOT NEEDED CODE Overrides
    //
    // Called when no beacons in a Region are visible from MonitorNotifier
    @Override
    public void didExitRegion(Region region) {
        updateText("Beacon not visible");
    }
    // Called with a state value when at least one or no beacons in a Region are visible from Monitor Notifier
    @Override
    public void didDetermineStateForRegion(int state, Region region) {
    }
    //
    //
    //
    //Bluetooth / Permissions / App-Display-Log-methods
    //
    // Override Method in FragmentActivity
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
                    builder.setOnDismissListener(dialog -> {
                    });
                    builder.show();
                }
            }
        }
    }
    // Request the location-permissions if not already granted
    private void requestPermissions() {
        if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (this.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (!this.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("This app needs background location access");
                        builder.setMessage("Please grant location access so this app can detect beacons in the background.");
                        builder.setPositiveButton(android.R.string.ok, null);
                        builder.setOnDismissListener(dialog -> requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                                PERMISSION_REQUEST_BACKGROUND_LOCATION));
                        builder.show();
                    }
                    else {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Functionality limited");
                        builder.setMessage("Since background location access has not been granted, this app will not be able to discover beacons in the background.  Please go to Settings -> Applications -> Permissions and grant background location access to this app.");
                        builder.setPositiveButton(android.R.string.ok, null);
                        builder.setOnDismissListener(dialog -> {
                        });
                        builder.show();
                    }
                }
            }
        } else {
            if (!this.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        PERMISSION_REQUEST_FINE_LOCATION);
            }
            else {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Functionality limited");
                builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons.  Please go to Settings -> Applications -> Permissions and grant location access to this app.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(dialog -> {
                });
                builder.show();
            }

        }
    }
    // Check Bluetooth functionalities
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
        }
        catch (RuntimeException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not available");
            builder.setMessage("Sorry, this device does not support Bluetooth LE.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(dialog -> finishAffinity());
            builder.show();

        }

    }
    // Log the Beacon Details to the Screen when Ranging
    private void logToDisplay(String line) {
        runOnUiThread(() -> {
            TextView editText = (TextView) MainActivity.this.findViewById(R.id.rangingText);
            editText.setText(line);
        });
    }
    // Update the visibility text
    private void updateText(String line) {
        runOnUiThread(() -> {
            TextView editText = (TextView) MainActivity.this.findViewById(R.id.monitoringText);
            editText.setText(line);
        });
    }

}