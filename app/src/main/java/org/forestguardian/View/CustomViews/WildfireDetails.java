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
import org.forestguardian.DataAccess.Weather.OpenWeatherWrapper;
import org.forestguardian.R;
import org.forestguardian.View.MapActivity;
import org.w3c.dom.Text;

/**
 * Created by luisalonsomurillorojas on 13/4/17.
 */

public class WildfireDetails extends LinearLayout {

    private TextView mIntensity;
    private TextView mTemperature;
    private TextView mPressure;
    private TextView mHumidity;
    private TextView mWind;
    private Button mExitButton;
    private Context mContext;

    public WildfireDetails(Context context) {
        super(context);

        init(context);
    }

    public WildfireDetails(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public WildfireDetails(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        inflate(context, R.layout.wildfire_details, this);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.setLayoutParams(params);

        //Init views
        this.mExitButton = (Button) findViewById(R.id.wildfire_details_background_button);
        this.mExitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MapActivity)(WildfireDetails.this.mContext)).removeRouteDetails(WildfireDetails.this);
            }
        });

        this.mHumidity = (TextView) findViewById(R.id.wildfire_humidity_value);
        this.mIntensity = (TextView) findViewById(R.id.wildfire_intesity_value);
        this.mPressure = (TextView) findViewById(R.id.wildfire_pressure_value);
        this.mWind = (TextView) findViewById(R.id.wildfire_wind_velocity_value);
        this.mTemperature = (TextView) findViewById(R.id.wildfire_temperature_value);
    }

    public void setWeatherData(OpenWeatherWrapper weatherData) {
        if (weatherData != null) {
            this.mTemperature.setText("" + weatherData.getTemperature());
            this.mWind.setText("" + weatherData.getWind().getSpeed());
            this.mPressure.setText("" + weatherData.getPressure());
            this.mHumidity.setText("" + weatherData.getHumidity());
        }
    }

    public void setMODISData(MODIS modisData) {
        if (modisData != null) {
            this.mIntensity.setText("" + modisData.getBrightness());
        }
    }
}
