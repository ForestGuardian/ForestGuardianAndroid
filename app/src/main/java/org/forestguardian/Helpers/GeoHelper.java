package org.forestguardian.Helpers;

import android.location.Location;

/**
 * Created by luisalonsomurillorojas on 1/4/17.
 */

public class GeoHelper implements IContants{

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
        double distance = Math.sqrt(Math.pow(pointB.getLatitude() - pointA.getLatitude(), 2) + Math.pow(pointB.getLongitude() - pointA.getLongitude(), 2));
        return distance;
    }
}
