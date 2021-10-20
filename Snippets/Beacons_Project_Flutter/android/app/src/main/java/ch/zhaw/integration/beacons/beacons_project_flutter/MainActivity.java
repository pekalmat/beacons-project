package ch.zhaw.integration.beacons.beacons_project_flutter;

import io.flutter.embedding.android.FlutterActivity;
//import com.umair.beacons_plugin.BeaconsPlugin;

public class MainActivity extends FlutterActivity {

    @Override
    protected void onPause() {
        super.onPause();
        //BeaconsPlugin.startBackgroundService(this);
    }

    protected void onResume() {
        super.onResume();
        //BeaconPlugin.stopBackgroundService(this);
    }
}
