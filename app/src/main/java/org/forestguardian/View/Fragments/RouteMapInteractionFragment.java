package org.forestguardian.View.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.forestguardian.DataAccess.NASA.MODIS;
import org.forestguardian.DataAccess.OSM.FireStation;
import org.forestguardian.DataAccess.OSM.WaterResource;
import org.forestguardian.Helpers.GeoHelper;
import org.forestguardian.R;

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
    private static String LATITUDE_KEY = "LatitudeKey";
    private static String LONGITUDE_KEY = "LongitudeKey";

    private OnRouteInteractionListener mListener;
    private MODIS mMODIS;
    private FireStation mFirestation;
    private WaterResource mWaterResource;
    private Location mCurrentLocation;
    private Location mEndPlace;

    @BindView(R.id.route_current_location_button) TextView mCurrentLocationButton;
    @BindView(R.id.route_firestation_location_button) TextView mFirestationLocationButton;
    @BindView(R.id.route_water_location_button) TextView mWaterLocationButton;
    @BindView(R.id.wildfire_location_name_label) TextView mWildfireLocationNameLabel;
    @BindView(R.id.wildfire_coordinates_label) TextView mWildfireCoordinatesLabel;

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
        Double currentLatitude = Math.round(getArguments().getDouble(LATITUDE_KEY) * 1000.0) / 1000.0;
        Double currentLongitude = Math.round(getArguments().getDouble(LONGITUDE_KEY) * 1000.0) / 1000.0;
        this.mCurrentLocation = new Location("");
        this.mCurrentLocation.setLatitude(currentLatitude);
        this.mCurrentLocation.setLongitude(currentLongitude);
        this.mEndPlace = null;
        //Set UI data
        if (this.mCurrentLocation != null) {
            this.mCurrentLocationButton.setText(GeoHelper.formatCoordinates(this.mCurrentLocation));
        }
        if (fireStation != null) {
            if (fireStation.getName() != null) {
                this.mFirestationLocationButton.setText(fireStation.getName());
            } else {
                this.mFirestationLocationButton.setText(fireStation.getAddress());
            }
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
        fragmentBundle.putSerializable(MODIS_KEY, modis);
        fragmentBundle.putSerializable(FIRESTATION_KEY, fireStation);
        fragmentBundle.putSerializable(WATER_KEY, waterResource);
        fragmentBundle.putDouble(LATITUDE_KEY, currentLocation.getLatitude());
        fragmentBundle.putDouble(LONGITUDE_KEY, currentLocation.getLongitude());

        routeMapInteractionFragment.setArguments(fragmentBundle);
        return routeMapInteractionFragment;
    }

    public OnRouteInteractionListener getListener() {
        return mListener;
    }

    public void setListener(OnRouteInteractionListener mListener) {
        this.mListener = mListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @OnClick(R.id.route_current_location_button)
    public void onCurrentLocationBtnClick() {
        this.mEndPlace = this.mCurrentLocation;

        //Set selection
        this.mCurrentLocationButton.setBackgroundColor(getActivity().getResources().getColor(R.color.colorDarkGray));
        this.mFirestationLocationButton.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
        this.mWaterLocationButton.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
    }

    @OnClick(R.id.route_firestation_location_button)
    public void onFirestationLocationBtnClick() {
        if (this.mFirestation != null) {
            this.mEndPlace = this.mFirestation.getCoordinate();
        }

        //Set selection
        this.mCurrentLocationButton.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary
        ));
        this.mFirestationLocationButton.setBackgroundColor(getActivity().getResources().getColor(R.color.colorDarkGray));
        this.mWaterLocationButton.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
    }

    @OnClick(R.id.route_water_location_button)
    public void onWaterLocationBtn() {
        if (this.mWaterResource != null) {
            this.mEndPlace = this.mWaterResource.getCoordinate();
        }

        //Set selection
        this.mCurrentLocationButton.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
        this.mFirestationLocationButton.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
        this.mWaterLocationButton.setBackgroundColor(getActivity().getResources().getColor(R.color.colorDarkGray));
    }

    @OnClick(R.id.fab_route)
    public void onFabRouteBtnClick() {
        if (this.mListener != null) {
            this.mListener.drawRoute(mMODIS.getCoordinate(), this.mEndPlace);
        }
    }

}
