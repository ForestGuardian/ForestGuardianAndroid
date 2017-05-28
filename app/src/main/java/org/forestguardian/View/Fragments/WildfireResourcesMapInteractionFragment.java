package org.forestguardian.View.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class WildfireResourcesMapInteractionFragment extends Fragment {

    public interface OnGeneralInteractionListener {
        void showRouteOptions();
    }

    private static String TAG = "WildfireResourcesMapInteractionFragment";
    private static String FIRESTATION_KEY = "FireStationKey";
    private static String WATER_KEY = "WaterKey";
    private static String MODIS_KEY = "MODISKey";

    private OnGeneralInteractionListener mListener;
    private String mFirestationText;
    private String mWaterText;
    private String mWildfireLocationText;

    @BindView(R.id.firestationLabel) TextView mFirestationLabel;
    @BindView(R.id.waterLabel) TextView mWaterLabel;
    @BindView(R.id.wildfireLocationLabel) TextView mWildfireLocationLabel;
    @BindView(R.id.fab_route) FloatingActionButton mFabRoute;


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
        this.mFirestationLabel.setText(fireStation.getName() + ", " + fireStation.getCity());
        this.mWaterLabel.setText(waterResource.getName());
        this.mWildfireLocationLabel.setText(GeoHelper.formatCoordinates(modis.getCoordinate()));
        return view;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
    }

    public static WildfireResourcesMapInteractionFragment setFireData(MODIS modis, FireStation fireStation, WaterResource waterResource) {
        WildfireResourcesMapInteractionFragment wildfireResourcesMapInteractionFragment = new WildfireResourcesMapInteractionFragment();

        Bundle fragmentBundle = new Bundle();
        fragmentBundle.putSerializable(MODIS_KEY, (Serializable) modis);
        fragmentBundle.putSerializable(FIRESTATION_KEY, (Serializable) fireStation);
        fragmentBundle.putSerializable(WATER_KEY, (Serializable) waterResource);

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
