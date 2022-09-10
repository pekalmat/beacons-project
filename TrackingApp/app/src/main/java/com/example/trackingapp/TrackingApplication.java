package com.example.trackingapp;

import android.app.Application;
import android.util.Log;

import io.sentry.Sentry;


public class TrackingApplication extends Application {
    private static final String TAG = "########## TrackingApplication ##########";

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate-TrackingApplication");
        createDefaultUncaughtExceptionHandler();
    }

    private void createDefaultUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException (Thread thread, Throwable e) {
                        Sentry.captureMessage("UncaughtExceptionFound:" + e);
                    }
                });
    }

}
