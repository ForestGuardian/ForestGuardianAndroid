package org.forestguardian.Helpers;

/**
 * Created by luisalonsomurillorojas on 1/4/17.
 */

public interface IContants {
    public static final int EARTH_RADIUS = 6371;//In kilometers
    public static final int GEO_CONST = 111320;
    public static final int GEO_CONST_2 = 110540;
    public static final String OVERPASS_REQUEST_FORMAT_AND_TIMEOUT = "[out:json][timeout:30]";
    public static final String OSM_FIRESTATION_AMENITY = "fire_station";
    public static final String OSM_RIVERS_WATERWAY = "river";
    //MODIS keys
    public static final String MODIS_LATITUDE = "LATITUDE";
    public static final String MODIS_LONGITUDE = "LONGITUDE";
    public static final String MODIS_BRIGHTNESS = "BRIGHTNESS";
    public static final String MODIS_SCAN = "SCAN";
    public static final String MODIS_TRACK = "TRACK";
    public static final String MODIS_ACQ_DATE = "ACQ_DATE";
    public static final String MODIS_ACQ_TIME = "ACQ_TIME";
    public static final String MODIS_SATELLITE = "SATELLITE";
    public static final String MODIS_CONFIDENCE = "CONFIDENCE";
    public static final String MODIS_VERSION = "VERSION";
    public static final String MODIS_BRIGHT_T31 = "BRIGHT_T31";
    public static final String MODIS_FRP = "FRP";
    public static final String MODIS_DAYNIGHT = "DAYNIGHT";
    //Basemaps
    public static final String FIRE_BASEMAP = "/maps/fires";
    public static final String TEMPERATURE_BASEMAP = "/maps/weather_perspective";
    public static final String WIND_BASEMAP = "/maps/windy";
    public static final String PRECIPITATION_BASEMAP = "/maps/protected_area";

}
