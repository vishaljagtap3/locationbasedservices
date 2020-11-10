package in.bitcode.locationbasedservices;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LocationManager mLocationManager;
    private LocationListener locationListener;

    private BroadcastReceiver brLocation = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Location location = intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);
            if(location != null) {
                mt("Location Using PI: " + location.getLatitude() + " , " + location.getLongitude());
            }
        }
    };

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getAllProviders();

        for (String provider : providers) {
            mt(provider + " ---> ");
            LocationProvider locationProvider = mLocationManager.getProvider(provider);
            mt("accuracy -> " + locationProvider.getAccuracy());
            mt("power req -> " + locationProvider.getPowerRequirement());
            mt("cost? -> " + locationProvider.hasMonetaryCost());
            mt("Cell? -> " + locationProvider.requiresCell());
            mt("Network? -> " + locationProvider.requiresNetwork());
            mt("sat? -> " + locationProvider.requiresSatellite());
            mt("alt? -> " + locationProvider.supportsAltitude());
            mt("bearing -> " + locationProvider.supportsBearing());
            mt("speed? -> " + locationProvider.supportsSpeed());

            @SuppressLint("MissingPermission")
            Location location = mLocationManager.getLastKnownLocation(provider);
            if (location != null) {
                mt("last location: " + location.getLatitude() + " , " + location.getLongitude() + " time: " + location.getTime());
            }
        }

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(true);
        criteria.setCostAllowed(false);

        //List<String> matchingProviders = mLocationManager.getProviders(criteria, true);
        String bestProvider = mLocationManager.getBestProvider(criteria, true);
        mt("Best Provider: " + bestProvider);

        locationListener = new MyLocationListener();

        //listen to location change events
       mLocationManager.requestLocationUpdates(
                bestProvider,
                3000,
                100,
                locationListener
        );
        //mLocationManager.removeUpdates(locationListener);

        registerReceiver(
                brLocation,
                new IntentFilter("in.bitcode.LOCATION")
        );

        mLocationManager.requestLocationUpdates(
                bestProvider,
                3000,
                100,
                PendingIntent.getBroadcast(
                        this,
                        0,
                        new Intent("in.bitcode.LOCATION"),
                        0
                )
        );

        

    }


    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            mt("new location " + location.getLatitude() + " , " + location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }


    private void mt(String text) {
        Log.e("tag", text);
    }
}