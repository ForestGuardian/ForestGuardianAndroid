package org.forestguardian.DataAccess.OSM;

import android.location.Location;

import java.io.Serializable;

/**
 * Created by luisalonsomurillorojas on 12/4/17.
 */

public class FireStation implements Serializable {

    /* Attributes */
    private String mName;
    private String mCity;
    private String mStreet;
    private String mAddress;
    private String mOperator;
    private Location mCoordinate;

    /* Constructor */
    public FireStation() {
        //initiate the attributes
        this.mName = null;
        this.mCity = null;
        this.mStreet = null;
        this.mOperator = null;
        this.mCoordinate = null;
    }

    /* Getters and setters */

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        this.mCity = city;
    }

    public String getStreet() {
        return mStreet;
    }

    public void setStreet(String street) {
        this.mStreet = street;
    }

    public String getOperator() {
        return mOperator;
    }

    public void setOperator(String operator) {
        this.mOperator = operator;
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

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String mAddress) {
        this.mAddress = mAddress;
    }
}
