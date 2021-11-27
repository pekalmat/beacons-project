package com.example.trackingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
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
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);

    private static final String HOST = "http://192.168.1.188:8081";
    private static final String POST_NEW_TREATMENT_REQUEST_URL = HOST + "/tracking/treatments";
    private static final String GET_ALL_BEACONS_URL = HOST + "/tracking/beacons";
    private RequestQueue requestQueue;

    private List<BeaconDto> knownBeaconsToTrack = new ArrayList<>();
    private BeaconDto currentlyTrackedBeacon;
    private Date currentTrackingStartTime;



    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        verifyBluetooth();
        requestPermissions();

        requestQueue = Volley.newRequestQueue(this);
        fetchBeaconsToTrack();


        BeaconManager.getInstanceForApplication(this).addMonitorNotifier(this);

        if (TrackingApplication.insideRegion) {
            updateText("Beacons are visible.");
        }
        else {
            updateText("No beacons are visible.");
        }
    }

    private void fetchBeaconsToTrack() {
        // Create Json-PostRequest
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                Request.Method.GET,
                GET_ALL_BEACONS_URL,
                null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Gson gson = new Gson();
                        Type listOfMyClassObject = new TypeToken<ArrayList<BeaconDto>>() {}.getType();
                        knownBeaconsToTrack = gson.fromJson(response.toString(), listOfMyClassObject);
                        Log.i(TAG, "Successfully fetched " + knownBeaconsToTrack.size() + " registered Beacons.");
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG,"Could Not Fetch Beacons");
                        Log.e(TAG, error.toString());
                    }
                });

        // Add the Request to the RequestQueue.
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        for (Beacon beacon: beacons) {
            if(beaconIsRegisteredInSystem(beacon)) {
                if (beacon.getDistance() < 1.0) {
                    if (currentlyTrackedBeacon == null) {
                        currentTrackingStartTime = Calendar.getInstance().getTime();
                        currentlyTrackedBeacon = new BeaconDto(beacon.getId1().toString(), beacon.getId2().toString(), beacon.getId3().toString());
                        Log.i(TAG, "Start Tracking Beacon UUID: " + currentlyTrackedBeacon.getBeaconUid() + " Major: " + currentlyTrackedBeacon.getMajor() + " Minor: " + currentlyTrackedBeacon.getMinor());
                    } else if(!beaconMatchesBeaconDto(beacon, currentlyTrackedBeacon)) {
                        sendNewTreatmentPostRequest();
                        Log.i(TAG, "End Tracking Beacon UUID: " + currentlyTrackedBeacon.getBeaconUid() + " Major: " + currentlyTrackedBeacon.getMajor() + " Minor: " + currentlyTrackedBeacon.getMinor());
                        currentlyTrackedBeacon = new BeaconDto(beacon.getId1().toString(), beacon.getId2().toString(), beacon.getId3().toString());
                        currentTrackingStartTime = Calendar.getInstance().getTime();
                        Log.i(TAG, "Start Tracking Beacon UUID: " + currentlyTrackedBeacon.getBeaconUid() + " Major: " + currentlyTrackedBeacon.getMajor() + " Minor: " + currentlyTrackedBeacon.getMinor());
                    }
                } else if(beacon.getDistance() > 10.0) {
                    if(currentlyTrackedBeacon != null) {
                        if(beaconMatchesBeaconDto(beacon, currentlyTrackedBeacon)) {
                            sendNewTreatmentPostRequest();
                            Log.i(TAG, "End Tracking Beacon UUID: " + currentlyTrackedBeacon.getBeaconUid() + " Major: " + currentlyTrackedBeacon.getMajor() + " Minor: " + currentlyTrackedBeacon.getMinor());
                            currentlyTrackedBeacon = null;
                            currentTrackingStartTime = null;
                            Log.i(TAG, "No Beacon is tracked currently");
                        }
                    }
                }
            }
        }
    }

    private boolean beaconMatchesBeaconDto(Beacon beacon, BeaconDto beaconDto) {
        if (beaconDto.getBeaconUid().equals(beacon.getId1().toString())
                && beaconDto.getMajor().equals(beacon.getId2().toString())
                && beaconDto.getMinor().equals(beacon.getId3().toString())) {
            return true;
        }
        return false;
    }

    private boolean beaconIsRegisteredInSystem(Beacon beacon) {
        for (BeaconDto beaconDto : knownBeaconsToTrack) {
            if (beaconMatchesBeaconDto(beacon, beaconDto)) {
                return true;
            }
        }
        return false;
    }


    private void sendNewTreatmentPostRequest() {
        // Create request JsonBody
        JSONObject jsonBody = createPostRequestBody();
        // Create Json-PostRequest
        JsonObjectRequest postNewTreatmentRequest = new JsonObjectRequest(
                Request.Method.POST,
                POST_NEW_TREATMENT_REQUEST_URL,
                jsonBody,
                response -> Log.d(TAG, "PostRequestResponse is: " + response.toString()),
                error -> Log.d(TAG, "PostRequestError: " + error.getMessage())
        );
        // Add the Request to the RequestQueue.
        Log.i(TAG, "Send Tracking Data to server: " + currentlyTrackedBeacon.getBeaconUid() + " Major: " + currentlyTrackedBeacon.getMajor() + " Minor: " + currentlyTrackedBeacon.getMinor());
        requestQueue.add(postNewTreatmentRequest);

    }

    private JSONObject createPostRequestBody() {
        try {
            String startTimeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentTrackingStartTime);
            Date endTime = java.util.Calendar.getInstance().getTime();
            String endTimeString =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(endTime);
            Gson gson = new Gson();
            String beaconJson = gson.toJson(currentlyTrackedBeacon);

            final JSONObject jsonBody = new JSONObject(
                    "{" +
                            "\"doctorId\":\"2\"," +
                            "\"treatmentStart\":\"" + startTimeString + "\"," +
                            "\"treatmentEnd\":\"" + endTimeString + "\"," +
                            "\"beaconDto\":" + beaconJson +
                            "}");
            return jsonBody;
        } catch (JSONException e) {
            Log.d(TAG,"Error Creating RequestBody");
            e.printStackTrace();
        }
        return new JSONObject();
    }

    @Override
    public void didEnterRegion(Region region) {
        //Log.d(TAG,"didEnterRegion called");
        updateText("Beacon visible");
        //becvaon id uslese + aktuelle zeit
        // speichern map/list
        try {
            // start ranging for beacons.  This will provide an update once per second with the estimated
            // distance to the beacon in the didRAngeBeaconsInRegion method.
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            beaconManager.addRangeNotifier(this);
        } catch (RemoteException e) {   }
    }

    @Override
    public void didExitRegion(Region region) {
        // beacon-start zeit + beacon id aus list/map auslesen
        // + aktuelle zeit (endTimme)
        // post request an server
        //Log.d(TAG,"didExitRegion called");
        updateText("Beacon not visible");

    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {
        //Log.d(TAG,"didDetermineStateForRegion called with state: " + (state == 1 ? "INSIDE ("+state+")" : "OUTSIDE ("+state+")"));
    }



    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
                            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                                @TargetApi(23)
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                                            PERMISSION_REQUEST_BACKGROUND_LOCATION);
                                }

                            });
                            builder.show();
                        }
                        else {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle("Functionality limited");
                            builder.setMessage("Since background location access has not been granted, this app will not be able to discover beacons in the background.  Please go to Settings -> Applications -> Permissions and grant background location access to this app.");
                            builder.setPositiveButton(android.R.string.ok, null);
                            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                }

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
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_FINE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "fine location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
            case PERMISSION_REQUEST_BACKGROUND_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "background location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since background location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
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
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finishAffinity();
                    }
                });
                builder.show();
            }
        }
        catch (RuntimeException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not available");
            builder.setMessage("Sorry, this device does not support Bluetooth LE.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    finishAffinity();
                }

            });
            builder.show();

        }

    }

    private String cumulativeLog = "";
    private void logToDisplay(String line) {
        cumulativeLog += line+"\n";
        runOnUiThread(new Runnable() {
            public void run() {
                TextView editText = (TextView) MainActivity.this.findViewById(R.id.rangingText);
                editText.setText(line);
            }
        });
    }

    private void updateText(String line) {
        runOnUiThread(new Runnable() {
            public void run() {
                TextView editText = (TextView) MainActivity.this.findViewById(R.id.monitoringText);
                editText.setText(line);
            }
        });
    }


    private boolean currentlyRanging = false;
    public void onRanging(View view){
        if(currentlyRanging == false) {
            RangeNotifier rangeNotifier = new RangeNotifier() {
                @Override
                public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                    if (beacons.size() > 0) {
                        Log.d(TAG, "didRangeBeaconsInRegion called with beacon count:  " + beacons.size());
                        Beacon firstBeacon = beacons.iterator().next();

                        logToDisplay("The first beacon " + firstBeacon.toString() + " is about " + firstBeacon.getDistance() + " meters away.");
                    }
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


}