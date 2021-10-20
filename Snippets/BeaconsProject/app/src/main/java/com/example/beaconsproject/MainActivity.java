package com.example.beaconsproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public ListView beaconListView;
    public TextView beaconCountTextView;
    public Button monitoringButton;
    public Button rangingButton;
    //public BeaconReferenceApplication beaconReferenceApplication;
    @Nullable
    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}