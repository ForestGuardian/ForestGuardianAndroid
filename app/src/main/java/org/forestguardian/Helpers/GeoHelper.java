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
        if (context == null || point == null) {
            return null;
        }

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

    public static Location getPointFromAddressName(Context context, String placeName) throws IOException {
        if (context == null || placeName == null) {
            return null;
        }

        AndroidGeocoder geocoder = new AndroidGeocoder(context, Locale.getDefault());
        geocoder.setAccessToken(context.getResources().getString(R.string.mapbox_geocodign_token));
        List<Address> address = geocoder.getFromLocationName(placeName, 1);
        if (address.size() > 0) {
            Location addressPoint = new Location("");
            addressPoint.setLatitude(address.get(0).getLatitude());
            addressPoint.setLongitude(address.get(0).getLongitude());
            return addressPoint;
        } else {
            return null;
        }
    }

    public static List<Address> getPointsFromAddressName(Context context, String placeName) throws IOException {
        if (context == null || placeName == null) {
            return null;
        }

        AndroidGeocoder geocoder = new AndroidGeocoder(context, Locale.getDefault());
        geocoder.setAccessToken(context.getResources().getString(R.string.mapbox_geocodign_token));
        List<Address> address = geocoder.getFromLocationName(placeName, 10);
        return address;
    }

    public static String formatCoordinates(Location location) {
        if (location == null) {
            return null;
        }

        String coordinatesLabel = String.valueOf(location.getLatitude());
        if (location.getLatitude() >= 0) {
            coordinatesLabel += "° N, ";
        } else {
            coordinatesLabel += "° S, ";
        }

        coordinatesLabel += String.valueOf(location.getLongitude());
        if (location.getLongitude() >= 0) {
            coordinatesLabel += "° E";
        } else {
            coordinatesLabel += "° O";
        }
        return coordinatesLabel;
    }

    public static String convertLocationToString(Location point) {
        if (point == null) {
            return null;
        }

        String locationText = String.valueOf(point.getLatitude()) + "," + String.valueOf(point.getLongitude());
        return locationText;
    }

    public static Location convertStringToLocation(String locationText) {
        if (locationText == null) {
            return null;
        }

        //Split the string
        String[] locationList = locationText.split(",");
        //Creating the location
        Location point = new Location("");
        point.setLatitude(Double.valueOf(locationList[0]));
        point.setLongitude(Double.valueOf(locationList[1]));
        return point;
    }
}
