package org.forestguardian.View.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.forestguardian.Helpers.IContants;
import org.forestguardian.R;

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

    @BindView(R.id.add_report_btn) Button mAddReportBtn;
    @BindView(R.id.center_location_btn) Button mCenterLocationBtn;
    @BindView(R.id.currentLocationTextView) TextView mCurrentLocationText;
    @BindView(R.id.fab_temperature) ImageButton mTemperatureMapButton;
    @BindView(R.id.fab_wind) ImageButton mWindMapButton;
    @BindView(R.id.fab_precipitation) ImageButton mForestMapButton;

    /* Map buttons flags */
    private boolean mTemperatureState = false;
    private boolean mWindState = true;
    private boolean mForestState = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.map_bottom_default, container, false);
        ButterKnife.bind(this, view);
        //Update the basemap layer UI
        updateLayersUI(getActivity());
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
        mTemperatureState = !mTemperatureState;
        updateLayersUI(getActivity());
        //notify the change
        if ( mListener != null && mTemperatureState ) {
            mListener.changeBasemap(ADD_TEMPERATURE_BASEMAP);
        } else if ( mListener != null && !mTemperatureState) {
            mListener.changeBasemap(REMOVE_TEMPERATURE_BASEMAP);
        }
    }

    @OnClick(R.id.fab_wind)
    public void onWindMapClick() {
        //check the layer state
        mWindState = !mWindState;
        updateLayersUI(getActivity());
        //notify the change
        if ( mListener != null && mWindState ) {
            mListener.changeBasemap(ADD_WIND_BASEMAPP);
        } else if ( mListener != null && !mWindState ) {
            mListener.changeBasemap(REMOVE_WIND_BASEMAP);
        }
    }

    @OnClick(R.id.fab_precipitation)
    public void onForestMapClick() {
        //check the layer state
        mForestState = !mForestState;
        updateLayersUI(getActivity());
        //notify the change
        if ( mListener != null && mForestState ) {
            mListener.changeBasemap(ADD_FOREST_BASEMAP);
        } else if ( mListener != null && !mForestState ) {
            mListener.changeBasemap(REMOVE_FOREST_BASEMAP);
        }
    }

    public void updateLayersUI(Context context) {
        //Update the temperature layer
        if (mTemperatureState) {
            mTemperatureMapButton.setBackground(context.getResources().getDrawable(R.drawable.ic_lluvia_layer_rain_on));
        } else {
            mTemperatureMapButton.setBackground(context.getResources().getDrawable(R.drawable.ic_lluvia_layer_rain_off));
        }

        //Update the wind layer
        if (mWindState) {
            mWindMapButton.setBackground(context.getResources().getDrawable(R.drawable.ic_viento_layer_on));
        } else {
            mWindMapButton.setBackground(context.getResources().getDrawable(R.drawable.ic_viento_layer_off));
        }

        //Update the forest layer
        if (mForestState) {
            mForestMapButton.setBackground(context.getResources().getDrawable(R.drawable.ic_bosque_layer_on));
        } else {
            mForestMapButton.setBackground(context.getResources().getDrawable(R.drawable.ic_bosque_layer_off));
        }
    }

    public OnDefaultInteractionListener getListener() {
        return mListener;
    }

    public void setListener(final OnDefaultInteractionListener pListener) {
        mListener = pListener;
    }

    public void setCurrentLocation(String locationText) {
        final String tmpLocationText = locationText;
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> setLocationLabelText(tmpLocationText));
        }
    }

    public void setLocationLabelText(String message) {
        if (mCurrentLocationText != null) {
            mCurrentLocationText.setText(message);
        }
    }
}
