package org.forestguardian.DataAccess.NASA;

import android.location.Location;

import org.forestguardian.Helpers.IContants;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by luisalonsomurillorojas on 12/4/17.
 */

public class MODIS implements IContants, Serializable {

    /* Attribute */
    private Location mCoordinate;
    private double mBrightness;
    private double mScan;
    private double mTrack;
    private String mPlaceName;
    private String mDate;
    private String mTime;
    private String mSatellite;
    private int mConfidence;
    private String mVersion;
    private double mBright_t31;
    private double mFRP;
    private String mDayNight;

    /* constructor */
    public MODIS(JSONObject jsonMODIS) throws JSONException {
        this.setCoordinate(jsonMODIS.getDouble(MODIS_LATITUDE), jsonMODIS.getDouble(MODIS_LONGITUDE));
        this.setBrightness(jsonMODIS.getDouble(MODIS_BRIGHTNESS));
        this.setScan(jsonMODIS.getDouble(MODIS_SCAN));
        this.setTrack(jsonMODIS.getDouble(MODIS_TRACK));
        this.setSatellite(jsonMODIS.getString(MODIS_SATELLITE));
        this.setConfidence(jsonMODIS.getInt(MODIS_CONFIDENCE));
        this.setBright_t31(jsonMODIS.getDouble(MODIS_BRIGHT_T31));
        this.setFRP(jsonMODIS.getDouble(MODIS_FRP));
        this.setVersion(jsonMODIS.getString(MODIS_VERSION));
        this.setDayNight(jsonMODIS.getString(MODIS_DAYNIGHT));
    }

    /* getter and setters */

    public Location getCoordinate() {
        return mCoordinate;
    }

    public void setCoordinate(double latitude, double longitude) {
        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        this.mCoordinate = location;
    }

    public double getBrightness() {
        return mBrightness;
    }

    public void setBrightness(double brightness) {
        this.mBrightness = brightness;
    }

    public double getScan() {
        return mScan;
    }

    public void setScan(double scan) {
        this.mScan = scan;
    }

    public double getTrack() {
        return mTrack;
    }

    public void setTrack(double track) {
        this.mTrack = track;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        this.mTime = time;
    }

    public String getSatellite() {
        return mSatellite;
    }

    public void setSatellite(String satellite) {
        this.mSatellite = satellite;
    }

    public int getConfidence() {
        return mConfidence;
    }

    public void setConfidence(int confidence) {
        this.mConfidence = confidence;
    }

    public String getVersion() {
        return mVersion;
    }

    public void setVersion(String version) {
        this.mVersion = version;
    }

    public double getBright_t31() {
        return mBright_t31;
    }

    public void setBright_t31(double bright_t31) {
        this.mBright_t31 = bright_t31;
    }

    public double getFRP() {
        return mFRP;
    }

    public void setFRP(double FRP) {
        this.mFRP = FRP;
    }

    public String getDayNight() {
        return mDayNight;
    }

    public void setDayNight(String dayNight) {
        this.mDayNight = dayNight;
    }

    public String getPlaceName() {
        return mPlaceName;
    }

    public void setPlaceName(String placeName) {
        this.mPlaceName = placeName;
    }
}
