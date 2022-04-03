package com.example.trackingapp;

import android.app.Application;
import android.util.Log;



public class TrackingApplication extends Application {
    private static final String TAG = "########## TrackingApplication ##########";

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate-TrackingApplication");
    }

}
