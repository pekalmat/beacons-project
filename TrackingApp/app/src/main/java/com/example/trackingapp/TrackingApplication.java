package com.example.trackingapp;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;


public class TrackingApplication extends Application implements MonitorNotifier {
    private static final String TAG = "########## TrackingApplication ##########";
    public static final Region beaconRegion = new Region("beaconRegion", null, null, null);
    public static boolean insideRegion = false;

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        BeaconManager beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);

        // By default the AndroidBeaconLibrary will only find AltBeacons.  f you wish to make it
        // find a different type of beacon, you must specify the byte layout for that beacon's
        // advertisement with a line like below.  The example shows how to find a beacon with the
        // same byte layout as AltBeacon but with a beaconTypeCode of 0xaabb.  To find the proper
        // layout expression for other beacon types, do a web search for "setBeaconLayout"
        // including the quotes.
        //cut out (,d:25-25)
        beaconManager.getBeaconParsers().clear();
        //iBeacon Layout = ("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24")));

        beaconManager.setDebug(false);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Scanning for Beacons");
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationChannel channel = new NotificationChannel("My Notification Channel ID",
                "My Notification Name", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("My Notification Channel Description");
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
        builder.setChannelId(channel.getId());


        beaconManager.enableForegroundServiceScanning(builder.build(), 456);

        beaconManager.setEnableScheduledScanJobs(false);
        beaconManager.setBackgroundBetweenScanPeriod(0);
        beaconManager.setBackgroundScanPeriod(100);

        Log.d(TAG, "setting up background monitoring in app onCreate");
        /*beaconManager.addMonitorNotifier(this);*/

        // If we were monitoring *different* regions on the last run of this app, they will be
        // remembered.  In this case we need to disable them here
        for (Region region: beaconManager.getMonitoredRegions()) {
            beaconManager.stopMonitoring(region);
        }

        Log.d(TAG, "Started Monitoring");
        beaconManager.startMonitoring(beaconRegion);

    }

    @Override // Called when at least one beacon in a Region is visible from Monitor Notifier
    public void didEnterRegion(Region region) {
        Log.d(TAG, "did enter region.");
        insideRegion = true;
        Log.d(TAG, "Sending notification.");
    }

    @Override // Called when no beacons in a Region are visible from MonitorNotifier
    public void didExitRegion(Region region) {
        insideRegion = false;
    }

    @Override // Called with a state value when at least one or no beacons in a Region are visible from Monitor Notifier
    public void didDetermineStateForRegion(int state, Region region) {
    }

}
