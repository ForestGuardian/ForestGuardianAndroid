package org.forestguardian.DataAccess.OSM;

import android.location.Location;

/**
 * Created by luisalonsomurillorojas on 12/4/17.
 */

public class WaterResource {

    /* Attributes */
    private String mType;
    private String mName;
    private Location mCoordinate;

    /* Constructor */
    public WaterResource() {
        //initiating the attributes
        this.mType = null;
        this.mName = null;
        this.mCoordinate = null;
    }

    /*Getters and setters*/
    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public Location getCoordinate() {
        return mCoordinate;
    }

    public void setCoordinate(double latitude, double longitude) {
        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        this.mCoordinate = location;
    }
}
