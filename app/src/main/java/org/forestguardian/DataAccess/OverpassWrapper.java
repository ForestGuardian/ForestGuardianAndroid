package org.forestguardian.DataAccess;

import android.location.Location;

import java.util.ArrayList;

import hu.supercluster.overpasser.library.query.OverpassQuery;

/**
 * Created by luisalonsomurillorojas on 13/3/17.
 */

public class OverpassWrapper {

    /* Attributes */
    private Location OSMPoint;  // Open Stree Map (OSM) coordinate

    /* Private methods */
    private ArrayList<Location> getOSMArea(int range) {
        return null;
    }

    /* Get/Set methods */
    public Location getOSMPoint() {
        return OSMPoint;
    }

    public void setOSMPoint(Location OSMPoint) {
        this.OSMPoint = OSMPoint;
    }
}
