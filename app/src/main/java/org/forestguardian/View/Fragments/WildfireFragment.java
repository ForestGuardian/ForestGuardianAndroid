package org.forestguardian.View.Fragments;


import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.forestguardian.DataAccess.OSM.FireStation;
import org.forestguardian.DataAccess.OSM.OverpassWrapper;
import org.forestguardian.DataAccess.OSM.WaterResource;
import org.forestguardian.Helpers.GeoHelper;
import org.forestguardian.R;

import java.io.IOException;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class WildfireFragment extends Fragment {

    private static String TAG = WildfireFragment.class.getSimpleName();
    private static String LONGITUDE_KEY = "wildfireLongitude";
    private static String LATITUDE_KEY = "wildfireLatitude";

    private double mLatitude;
    private double mLongitude;

    @BindView(R.id.wildfire_image) ImageView mReportImage;
    @BindView(R.id.wildfire_title) TextView mTitle;
    @BindView(R.id.wildfire_description) TextView mDescription;
    @BindView(R.id.wildfire_report_location) TextView mReportPlace;
    @BindView(R.id.wildfire_firefigthers) TextView mFirefighters;
    @BindView(R.id.wildfire_water) TextView mWaterResource;
    @BindView(R.id.wildfire_position) TextView mPosition;

    public WildfireFragment() {
        // Required empty public constructor
    }

    public static WildfireFragment setFireLocation(double latitude, double longitud) {
        WildfireFragment wildfireFragment = new WildfireFragment();

        Bundle fragmentBundle = new Bundle();
        fragmentBundle.putDouble(LONGITUDE_KEY, longitud);
        fragmentBundle.putDouble(LATITUDE_KEY, latitude);

        wildfireFragment.setArguments(fragmentBundle);
        return wildfireFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.wildfire_details,container,false);

        // Set the attributes
        mLatitude = getArguments().getDouble(LATITUDE_KEY, -1);
        mLongitude = getArguments().getDouble(LONGITUDE_KEY, -1);

        setWildfireData();

        return view;
    }

    private void setWildfireData() {
        if (mLatitude != -1 && mLongitude != -1) {
            // Create the wildfire location
            Location wildfireLocation = new Location("");
            wildfireLocation.setLatitude(mLatitude);
            wildfireLocation.setLongitude(mLongitude);

            // Search for the nearest fire station
            OverpassWrapper overpassWrapper = new OverpassWrapper();
            overpassWrapper.setOSMPoint(wildfireLocation);
            overpassWrapper.getOSMDataForFireStations(100000, result -> {
                if (result != null) {
                    FireStation nearestFireStation = null;
                    double tmpDistance = 0;

                    for (int index = 0; index < result.elements.size(); index++) {
                        //Initiate the fire station model
                        FireStation fireStation = new FireStation();
                        fireStation.setName(result.elements.get(index).tags.name);
                        fireStation.setCity(result.elements.get(index).tags.addressCity);
                        fireStation.setStreet(result.elements.get(index).tags.addressStreet);
                        fireStation.setOperator(result.elements.get(index).tags.operator);
                        fireStation.setCoordinate(result.elements.get(index).lat, result.elements.get(index).lon);

                        //Set the address of the firestation
                        new Thread(() -> {
                            try {
                                String fireStationAddress = GeoHelper.getAddressNameFromPoint(getActivity(), fireStation.getCoordinate());
                                fireStation.setAddress(fireStationAddress);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }).start();

                        //Set initial conditions
                        if (nearestFireStation == null) {
                            nearestFireStation = fireStation;
                            tmpDistance = GeoHelper.calculateDistanceBetweenTwoPoints(wildfireLocation, fireStation.getCoordinate());
                        }

                        //Verify the distance between the wildfire and the fire stations
                        double currentStationDistance = GeoHelper.calculateDistanceBetweenTwoPoints(wildfireLocation, fireStation.getCoordinate());
                        if (currentStationDistance < tmpDistance) {
                            nearestFireStation = fireStation;
                            tmpDistance = currentStationDistance;
                        }
                    }
                    //Print the nearest fire station information
                    if (nearestFireStation != null) {
                        final FireStation tmpNearestFireStation = nearestFireStation;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mFirefighters.setText(tmpNearestFireStation.getAddress());
                            }
                        });
                    }
                } else {
                    Log.e(TAG, "Error getting the wildfires data!!");
                }
            });

            // Search for water resources
            overpassWrapper.getOSMDataForRivers(1000, result -> {
                if (result != null) {
                    if (result.elements.size() > 0) {
                        final String waterName = result.elements.get(0).tags.name;

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mWaterResource.setText(waterName);
                            }
                        });
                    }
                } else {
                    Log.e(TAG, "Error getting the rivers data!!");
                }
            });

            // Set the wildfire coordinates
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mPosition.setText(GeoHelper.formatCoordinates(wildfireLocation));
                }
            });
        }
    }

}
