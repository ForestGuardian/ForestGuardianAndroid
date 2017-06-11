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
    @BindView(R.id.fab_precipitation) ImageButton mPrecipitationMapButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        if ( mListener != null ) {
            mListener.changeBasemap(TEMPERATURE_BASEMAP);
        }
    }

    @OnClick(R.id.fab_wind)
    public void onWindMapClick() {
        if ( mListener != null ) {
            mListener.changeBasemap(WIND_BASEMAP);
        }
    }

    @OnClick(R.id.fab_precipitation)
    public void onPrecipitationMapClick() {
        if ( mListener != null ) {
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
    public void setCurrentLocation(Location point) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String locationText = GeoHelper.getAddressNameFromPoint(getActivity(), point);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mCurrentLocationText.setText(locationText);
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
