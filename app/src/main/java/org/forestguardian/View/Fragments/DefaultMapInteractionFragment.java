package org.forestguardian.View.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.forestguardian.Helpers.GeoHelper;
import org.forestguardian.Helpers.IContants;
import org.forestguardian.R;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by emma on 30/04/17.
 */
public class DefaultMapInteractionFragment extends Fragment implements IContants{

    public interface OnDefaultInteractionListener {
        void addReport();
        void centerOnLocation();
        void changeBasemap(String basemapURL);
    }

    private OnDefaultInteractionListener mListener;

    @BindView(R.id.add_report_btn) FloatingActionButton mAddReportBtn;
    @BindView(R.id.center_location_btn) FloatingActionButton mCenterLocationBtn;
    @BindView(R.id.currentLocationTextView) TextView mCurrentLocationText;
    @BindView(R.id.fab_temperature) ImageButton mTemperatureMapButton;
    @BindView(R.id.fab_wind) ImageButton mWindMapButton;
    @BindView(R.id.fab_precipitation) ImageButton mForestMapButton;

    /* Map buttons flags */
    private boolean mTemperatureState;
    private boolean mWindState;
    private boolean mForestState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Init the map button flags
        mTemperatureState = false;
        mWindState = false;
        mForestState = false;

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.map_bottom_default, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
    }

    @OnClick(R.id.add_report_btn)
    public void onAddReportBtnClick(){
        if ( mListener != null ){
            mListener.addReport();
        }
    }

    @OnClick(R.id.center_location_btn)
    public void onCenterLocationClick(){
        if ( mListener != null ){
            mListener.centerOnLocation();
        }
    }

    @OnClick(R.id.fab_temperature)
    public void onTemperatureMapClick() {
        //check the layer state
        if (mTemperatureState) {
            mTemperatureState = false;
            mTemperatureMapButton.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_agua_layer_on));
        } else {
            mTemperatureState = true;
            mWindState = false;
            mForestState = false;
            mTemperatureMapButton.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_agua_layer_off));
            mWindMapButton.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_viento_layer_on));
            mForestMapButton.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_bosque_layer_on));
        }
        //notify the change
        if ( mListener != null && mTemperatureState ) {
            mListener.changeBasemap(TEMPERATURE_BASEMAP);
        } else if ( mListener != null && !mTemperatureState) {
            mListener.changeBasemap(FIRE_BASEMAP);
        }
    }

    @OnClick(R.id.fab_wind)
    public void onWindMapClick() {
        //check the layer state
        if (mWindState) {
            mWindState = false;
            mWindMapButton.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_viento_layer_on));
        } else {
            mWindState = true;
            mTemperatureState = false;
            mForestState = false;
            mWindMapButton.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_viento_layer_off));
            mTemperatureMapButton.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_agua_layer_on));
            mForestMapButton.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_bosque_layer_on));
        }
        //notify the change
        if ( mListener != null && mWindState ) {
            mListener.changeBasemap(WIND_BASEMAP);
        } else if ( mListener != null && !mWindState ) {
            mListener.changeBasemap(FIRE_BASEMAP);
        }
    }

    @OnClick(R.id.fab_precipitation)
    public void onForestMapClick() {
        //check the layer state
        if (mForestState) {
            mForestState = false;
            mForestMapButton.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_bosque_layer_on));
        } else {
            mForestState = true;
            mTemperatureState = false;
            mWindState = false;
            mForestMapButton.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_bosque_layer_off));
            mWindMapButton.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_viento_layer_on));
            mTemperatureMapButton.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_agua_layer_on));
        }
        //notify the change
        if ( mListener != null && mForestState ) {
            mListener.changeBasemap(FOREST_BASEMAP);
        } else if ( mListener != null && !mForestState ) {
            mListener.changeBasemap(FIRE_BASEMAP);
        }
    }

    public OnDefaultInteractionListener getListener() {
        return mListener;
    }

    public void setListener(final OnDefaultInteractionListener pListener) {
        mListener = pListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setCurrentLocation(String locationText) {
        final String tmpLocationText = locationText;
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                                setLocationLabelText(tmpLocationText);
                            }
            });
        }
    }

    public void setLocationLabelText(String message) {
        if (mCurrentLocationText != null) {
            mCurrentLocationText.setText(message);
        }
    }
}
