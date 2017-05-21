package org.forestguardian.Helpers;

import android.content.Context;
import android.location.Address;
import com.mapbox.geocoder.android.AndroidGeocoder;
import android.location.Location;
import android.util.Log;

import org.forestguardian.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by luisalonsomurillorojas on 1/4/17.
 */

public class GeoHelper implements IContants{

    private static final String TAG = "GeoHelper";

    public static Location calculateCoordinateDistanceFromAPoint(Location centerPoint, int distance, int directionInDegrees) {
        double tmpDistance = distance * Math.sqrt(2);
        //Convert the direction form degrees to radians
        double radiansDirection = Math.toRadians(directionInDegrees);
        //Calculate the dx and dy
        double dx = tmpDistance * Math.cos(radiansDirection);
        double dy = tmpDistance * Math.sin(radiansDirection);
        //Convert the latitude to radians
        double radLat = Math.toRadians(centerPoint.getLatitude());
        //Calculate the delta_x and delta_y
        double delta_long = dx / (GEO_CONST * Math.cos(radLat));
        double delta_lat = dy / GEO_CONST_2;
        //Generate the resulting coordinate
        Location newLocation = new Location("");
        newLocation.setLatitude(centerPoint.getLatitude() + delta_lat);
        newLocation.setLongitude(centerPoint.getLongitude() + delta_long);

        return newLocation;
    }

    public static double calculateDistanceBetweenTwoPoints(Location pointA, Location pointB) {
        double dLat = Math.toRadians(pointB.getLatitude() - pointA.getLatitude());
        double dLon = Math.toRadians(pointB.getLongitude() - pointA.getLongitude());
        double a =  Math.sin(dLat/2) * Math.sin(dLat/2) +
                    Math.cos(Math.toRadians(pointA.getLatitude())) * Math.cos(Math.toRadians(pointB.getLatitude())) * Math.sin(dLon/2) * Math.sin(dLon/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = EARTH_RADIUS * c; // Distance in km
        return distance;
    }

    public static String getAddressNameFromPoint(Context context, Location point) throws IOException {
        AndroidGeocoder geocoder = new AndroidGeocoder(context, Locale.getDefault());
        geocoder.setAccessToken(context.getResources().getString(R.string.mapbox_geocodign_token));
        List<Address> address = geocoder.getFromLocation(point.getLatitude(), point.getLongitude(), 1);
        if (address.size() > 0) {
            if (address.get(0).getAddressLine(0) != null) {
                return address.get(0).getAddressLine(0);
            } else if (address.get(0).getFeatureName() != null){
                return address.get(0).getFeatureName();
            } else {
                return context.getResources().getString(R.string.unknown_place);
            }
        } else {
            return context.getResources().getString(R.string.unknown_place);
        }
    }
}
