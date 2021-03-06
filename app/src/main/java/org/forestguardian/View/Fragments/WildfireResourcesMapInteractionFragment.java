package org.forestguardian.View.Fragments;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class WildfireResourcesMapInteractionFragment extends Fragment {

    public interface OnGeneralInteractionListener {
        void showRouteOptions();
    }

    private static String TAG = "WildfireResourcesMapInteractionFragment";
    private static String FIRESTATION_KEY = "FireStationKey";
    private static String WATER_KEY = "WaterKey";
    private static String MODIS_KEY = "MODISKey";

    private OnGeneralInteractionListener mListener;

    @BindView(R.id.wildfire_location_place_name) TextView mWildfireLocationName;
    @BindView(R.id.firestationLabel) TextView mFirestationLabel;
    @BindView(R.id.waterLabel) TextView mWaterLabel;
    @BindView(R.id.wildfireLocationLabel) TextView mWildfireLocationLabel;
    @BindView(R.id.fab_route) Button mFabRoute;

    public WildfireResourcesMapInteractionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.map_bottom_general_information, container, false);
        ButterKnife.bind(this, view);
        //Get data models
        MODIS modis = (MODIS) getArguments().getSerializable(MODIS_KEY);
        FireStation fireStation = (FireStation) getArguments().getSerializable(FIRESTATION_KEY);
        WaterResource waterResource = (WaterResource) getArguments().getSerializable(WATER_KEY);
        // Set TextViews
        if (fireStation != null) {
            if (fireStation.getName() != null) {
                this.mFirestationLabel.setText(fireStation.getName());
            } else {
                this.mFirestationLabel.setText(fireStation.getAddress());
            }
        }
        if (waterResource != null) {
            this.mWaterLabel.setText(waterResource.getName());
        }
        if (modis != null) {
            this.mWildfireLocationName.setText(modis.getPlaceName());
            this.mWildfireLocationLabel.setText(GeoHelper.formatCoordinates(modis.getCoordinate()));
        }
        return view;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
    }

    public static Fragment setFireData(MODIS modis, FireStation fireStation, WaterResource waterResource) {
        WildfireResourcesMapInteractionFragment wildfireResourcesMapInteractionFragment = new WildfireResourcesMapInteractionFragment();

        Bundle fragmentBundle = new Bundle();
        fragmentBundle.putSerializable(MODIS_KEY, modis);
        fragmentBundle.putSerializable(FIRESTATION_KEY, fireStation);
        fragmentBundle.putSerializable(WATER_KEY, waterResource);

        wildfireResourcesMapInteractionFragment.setArguments(fragmentBundle);
        return wildfireResourcesMapInteractionFragment;
    }

    @OnClick(R.id.fab_route)
    public void onFabRouteBtnClick() {
        if (this.mListener != null) {
            this.mListener.showRouteOptions();
        }
    }

    public OnGeneralInteractionListener getListener() {
        return mListener;
    }

    public void setListener(OnGeneralInteractionListener mListener) {
        this.mListener = mListener;
    }

}
