package org.forestguardian.DataAccess.Location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.Pair;
import android.util.Log;

import org.forestguardian.Helpers.GeoHelper;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by emma on 04/08/17.
 */

public class LocationController implements LocationListener{

    private static final String TAG = "LocationController";

    public interface SimpleLocationListener{
        void onGPSChanged(Location pLocation, String pLocationName);
        void onUnavailable();
    }

    private final Context   mContext;
    private Location        mCurrentLocation;
    private String          mCurrentLocationName;
    private LocationManager mLocationManager;
    private ArrayList<SimpleLocationListener> mListeners;

    public LocationController(Context pContext) {
        mListeners = new ArrayList<>();
        mContext = pContext;
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Location permission was granted");
            if (this.mLocationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
                this.mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
            }
            if (this.mLocationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
                this.mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
            }
        } else {
            Log.e(TAG, "Location permission was not granted");
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        mCurrentLocation = location;
        Log.i(TAG, "Changed to: " + String.valueOf(location.getLatitude()) + ", " + String.valueOf(location.getLongitude()));
        updateLocalization(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

                /* Check the status of the location signal */
        String toastMessage = "";
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                Log.w(TAG, "OUT_OF_SERVICE");
                mListeners.forEach(SimpleLocationListener::onUnavailable);
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.w(TAG, "TEMPORARILY_UNAVAILABLE");
                break;
            case LocationProvider.AVAILABLE:
                Log.w(TAG, "AVAILABLE");
                break;
        }
                /* Show the toast message */
        Log.d("Location", toastMessage);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.w(TAG, "onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.w(TAG, "onProviderDisabled");
        mListeners.forEach(SimpleLocationListener::onUnavailable);
    }

    public Location getCurrentLocation() {
        return mCurrentLocation;
    }

    public String getCurrentLocationName() {
        return mCurrentLocationName;
    }

    public void notifyGPS(){
        if ( mCurrentLocation != null ){
            updateLocalization( mCurrentLocation );
        }
    }

    public void updateLocalization(Location pLocation) {
        new Thread(() -> {
            try {
                String locationText = GeoHelper.getAddressNameFromPoint(mContext, mCurrentLocation);
                mListeners.forEach( listener -> listener.onGPSChanged(pLocation, locationText) );
            } catch (IOException e) {
                e.printStackTrace();
                mListeners.forEach( listener -> listener.onGPSChanged(pLocation, null) );

            }
        }).start();
    }

    public void addListener( SimpleLocationListener pSimpleLocationListener){
        mListeners.add(pSimpleLocationListener);
        notifyGPS();
    }

    public void removeListener( SimpleLocationListener pSimpleLocationListener){
        mListeners.remove(pSimpleLocationListener);
    }
}
