package org.forestguardian.View.Fragments;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.forestguardian.DataAccess.NASA.MODIS;
import org.forestguardian.DataAccess.OSM.FireStation;
import org.forestguardian.DataAccess.OSM.WaterResource;
import org.forestguardian.Helpers.GeoHelper;
import org.forestguardian.R;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class RouteMapInteractionFragment extends Fragment {

    public interface OnRouteInteractionListener {
        void drawRoute(Location startPlace, Location endPlace);
    }

    private static String TAG = "RouteMapInteractionFragment";
    private static String FIRESTATION_KEY = "FireStationKey";
    private static String WATER_KEY = "WaterKey";
    private static String MODIS_KEY = "MODISKey";
    private static String LOCATION_KEY = "LocationKey";

    private OnRouteInteractionListener mListener;
    private MODIS mMODIS;
    private FireStation mFirestation;
    private WaterResource mWaterResource;
    private Location mCurrentLocation;
    private Location mEndPlace;

    @BindView(R.id.route_current_location_button) Button mCurrentLocationButton;
    @BindView(R.id.route_firestation_location_button) Button mFirestationLocationButton;
    @BindView(R.id.route_water_location_button) Button mWaterLocationButton;
    @BindView(R.id.wildfire_location_name_label) TextView mWildfireLocationNameLabel;
    @BindView(R.id.wildfire_coordinates_label) TextView mWildfireCoordinatesLabel;
    @BindView(R.id.fab_route) FloatingActionButton mFabRoute;

    public RouteMapInteractionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.map_bottom_route_information, container, false);
        ButterKnife.bind(this, view);
        //Get data models
        MODIS modis = (MODIS) getArguments().getSerializable(MODIS_KEY);
        this.mMODIS = modis;
        FireStation fireStation = (FireStation) getArguments().getSerializable(FIRESTATION_KEY);
        this.mFirestation = fireStation;
        WaterResource waterResource = (WaterResource) getArguments().getSerializable(WATER_KEY);
        this.mWaterResource = waterResource;
        Location currentLocation = (Location) getArguments().getSerializable(LOCATION_KEY);
        this.mCurrentLocation = currentLocation;
        this.mEndPlace = null;
        //Set UI data
        if (currentLocation != null) {
            this.mCurrentLocationButton.setText(GeoHelper.formatCoordinates(currentLocation));
        }
        if (fireStation != null) {
            this.mFirestationLocationButton.setText(fireStation.getName() + ", " + fireStation.getCity());
        }
        if (waterResource != null) {
            this.mWaterLocationButton.setText(waterResource.getName());
        }
        if (modis != null) {
            this.mWildfireLocationNameLabel.setText(modis.getPlaceName());
            this.mWildfireCoordinatesLabel.setText(GeoHelper.formatCoordinates(modis.getCoordinate()));
        }
        return view;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
    }

    public static Fragment setFireData(MODIS modis, FireStation fireStation, WaterResource waterResource, Location currentLocation) {
        RouteMapInteractionFragment routeMapInteractionFragment = new RouteMapInteractionFragment();

        Bundle fragmentBundle = new Bundle();
        fragmentBundle.putSerializable(MODIS_KEY, (Serializable) modis);
        fragmentBundle.putSerializable(FIRESTATION_KEY, (Serializable) fireStation);
        fragmentBundle.putSerializable(WATER_KEY, (Serializable) waterResource);
        fragmentBundle.putSerializable(LOCATION_KEY, (Serializable) currentLocation);

        routeMapInteractionFragment.setArguments(fragmentBundle);
        return routeMapInteractionFragment;
    }

    public OnRouteInteractionListener getListener() {
        return mListener;
    }

    public void setListener(OnRouteInteractionListener mListener) {
        this.mListener = mListener;
    }

    @OnClick(R.id.route_current_location_button)
    public void onCurrentLocationBtnClick() {
        this.mEndPlace = this.mCurrentLocation;
    }

    @OnClick(R.id.route_firestation_location_button)
    public void onFirestationLocationBtnClick() {
        if (this.mFirestation != null) {
            this.mEndPlace = this.mFirestation.getCoordinate();
        }
    }

    @OnClick(R.id.route_water_location_button)
    public void onWaterLocationBtn() {
        if (this.mWaterResource != null) {
            this.mEndPlace = this.mWaterResource.getCoordinate();
        }
    }

    @OnClick(R.id.fab_route)
    public void onFabRouteBtnClick() {
        if (this.mListener != null) {
            this.mListener.drawRoute(mMODIS.getCoordinate(), this.mEndPlace);
        }
    }

}
