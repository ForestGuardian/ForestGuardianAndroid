package org.forestguardian.DataAccess.OSM;

import android.location.Location;

import java.io.Serializable;

/**
 * Created by luisalonsomurillorojas on 12/4/17.
 */

public class WaterResource implements Serializable {

    /* Attributes */
    private long mID;
    private String mType;
    private String mName;
    private double mLatitude;
    private double mLongitude;

    /* Constructor */
    public WaterResource() {
        //initiating the attributes
        this.mType = null;
        this.mName = null;
    }

    /*Getters and setters*/
    public long getID() {
        return mID;
    }

    public void setID(long mID) {
        this.mID = mID;
    }

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
        Location coordinatesLocation = new Location("");
        coordinatesLocation.setLatitude(this.mLatitude);
        coordinatesLocation.setLongitude(this.mLongitude);
        return coordinatesLocation;
    }

    public void setCoordinate(double latitude, double longitude) {
        this.mLatitude = latitude;
        this.mLongitude = longitude;
    }
}
