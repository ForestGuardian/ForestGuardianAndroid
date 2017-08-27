package org.forestguardian.View.Fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.forestguardian.R;

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

        return view;
    }

}
