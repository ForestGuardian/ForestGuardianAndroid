package org.forestguardian.View.CustomViews;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.forestguardian.DataAccess.NASA.MODIS;
import org.forestguardian.DataAccess.OSM.FireStation;
import org.forestguardian.DataAccess.OSM.WaterResource;
import org.forestguardian.R;
import org.forestguardian.View.MapActivity;

/**
 * Created by luisalonsomurillorojas on 13/4/17.
 */

public class RouteDetails extends LinearLayout {

    private Button mCalculateRouteButton;
    private Button mRouteBackgroundButton;
    private TextView mWildfirePlace;
    private TextView mLatitude;
    private TextView mLongitude;
    private TextView mFireStation;
    private TextView mWaterResource;
    private Context mContext;

    public RouteDetails(Context context) {
        super(context);

        init(context);
    }

    public RouteDetails(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public RouteDetails(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        inflate(context, R.layout.wildfire_resources, this);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.setLayoutParams(params);

        //Init the views
        this.mCalculateRouteButton = (Button) findViewById(R.id.calc_route_button);
        this.mCalculateRouteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MapActivity)(RouteDetails.this.mContext)).showRouteDetails(RouteDetails.this);
            }
        });

        this.mRouteBackgroundButton = (Button) findViewById(R.id.res_background_button);
        this.mRouteBackgroundButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MapActivity)(RouteDetails.this.mContext)).removeWildfireDetails(RouteDetails.this);
            }
        });

        this.mWildfirePlace = (TextView) findViewById(R.id.res_wildfire_place_name);
        this.mLatitude = (TextView) findViewById(R.id.res_latitude_value);
        this.mLongitude = (TextView) findViewById(R.id.res_longitude_value);
        this.mFireStation = (TextView) findViewById(R.id.res_fire_station_name);
        this.mWaterResource = (TextView) findViewById(R.id.res_water_name);
    }

    public void setMODISData(MODIS modisData) {
        if (modisData != null) {
            this.mWildfirePlace.setText(modisData.getPlaceName());
            this.mLatitude.setText("" + modisData.getCoordinate().getLatitude());
            this.mLongitude.setText("" + modisData.getCoordinate().getLongitude());
        }
    }

    public void setOSMData(FireStation fireStation, WaterResource waterResource) {
        if (fireStation != null) {
            this.mFireStation.setText(fireStation.getCity());
        }
        if (waterResource != null) {
            this.mWaterResource.setText(waterResource.getName());
        }
    }
}
