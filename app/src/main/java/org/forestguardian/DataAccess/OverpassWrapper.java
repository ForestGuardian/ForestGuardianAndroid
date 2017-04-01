package org.forestguardian.DataAccess;

import android.location.Location;
import android.os.HandlerThread;
import android.util.Log;

import org.forestguardian.Helpers.GeoHelper;
import org.forestguardian.Helpers.IContants;

import java.io.IOException;
import java.util.ArrayList;

import hu.supercluster.overpasser.adapter.OverpassQueryResult;
import hu.supercluster.overpasser.adapter.OverpassServiceProvider;
import hu.supercluster.overpasser.library.output.OutputModificator;
import hu.supercluster.overpasser.library.output.OutputOrder;
import hu.supercluster.overpasser.library.output.OutputVerbosity;
import hu.supercluster.overpasser.library.query.OverpassQuery;

import static hu.supercluster.overpasser.library.output.OutputFormat.JSON;

/**
 * Created by luisalonsomurillorojas on 13/3/17.
 */

public class OverpassWrapper implements IContants {

    /* Attributes */
    private static final String TAG = "OverpassWrapper";
    private Location OSMPoint;  // Open Stree Map (OSM) coordinate

    /* Private methods */

    /**
     *
     * @param range distance with the center point
     * @return an Array with for coordinates: [S_Lat, W_Lon, N_Lat, E_Lon]
     */
    private ArrayList<Double> getOSMArea(int range) {
        //Initiate the coordinates array
        ArrayList<Double> OSMCoordinates = new ArrayList<Double>();
        Location tmpCoordiante = null;
        //Calculate the S_Lat
        tmpCoordiante = GeoHelper.calculateCoordinateDistanceFromAPoint(this.OSMPoint, range, 45);
        OSMCoordinates.add(tmpCoordiante.getLatitude());
        //Calculate the W_Lon
        tmpCoordiante = GeoHelper.calculateCoordinateDistanceFromAPoint(this.OSMPoint, range, 135);
        OSMCoordinates.add(tmpCoordiante.getLongitude());
        //Calculate the N_Lat
        tmpCoordiante = GeoHelper.calculateCoordinateDistanceFromAPoint(this.OSMPoint, range, 225);
        OSMCoordinates.add(tmpCoordiante.getLatitude());
        //Calculate the E_Lon
        tmpCoordiante = GeoHelper.calculateCoordinateDistanceFromAPoint(this.OSMPoint, range, 315);
        OSMCoordinates.add(tmpCoordiante.getLongitude());

        return OSMCoordinates;
    }

    /**
     *
     * @param OSMArea Array with the coordinates of the Map where the query will request data
     * @param amenity The type of information wants to be got
     * @return String that represent the query needed to request information to the Overpass API
     */
    private String getOverpassAPIQueryForNodes(ArrayList<Double> OSMArea, String amenity) {
        String query = new OverpassQuery()
                .filterQuery()
                .node()
                .amenity(amenity)
                .boundingBox(
                        OSMArea.get(2), OSMArea.get(1),
                        OSMArea.get(0), OSMArea.get(3))
                .end()
                .output(100)
                .build();
        return query;
    }

    /* Public methods */

    /**
     *
     * @param distance of the diameter of the map area
     * @param iOverpassAPI interface that will be called the got some result form OSM
     */
    public void getOSMDataForFireStations(int distance, IOverpassAPI iOverpassAPI) {
        ArrayList<Double> area = this.getOSMArea(distance);
        String query = OVERPASS_REQUEST_FORMAT_AND_TIMEOUT + this.getOverpassAPIQueryForNodes(area, OSM_FIRESTATION_AMENITY);
        Log.i(TAG, "Query: " + query);
        new Thread() {
            public void run() {
                try {
                    OverpassQueryResult result = OverpassServiceProvider.get().interpreter(query).execute().body();
                    if (iOverpassAPI != null) {
                        iOverpassAPI.overpassCallback(result);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /* Get/Set methods */
    public Location getOSMPoint() {
        return OSMPoint;
    }

    public void setOSMPoint(Location OSMPoint) {
        this.OSMPoint = OSMPoint;
    }
}
