package org.forestguardian.DataAccess;

import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import org.forestguardian.R;

import java.util.Locale;

import az.openweatherapi.OWService;
import az.openweatherapi.listener.OWRequestListener;
import az.openweatherapi.model.OWResponse;
import az.openweatherapi.model.gson.common.Coord;
import az.openweatherapi.model.gson.common.Wind;
import az.openweatherapi.model.gson.current_day.CurrentWeather;
import az.openweatherapi.utils.OWSupportedUnits;

/**
 * Created by luisalonsomurillorojas on 12/3/17.
 */

public class OpenWeatherWrapper {

    private static String TAG = "OpenWeatherWrapper";
    private OWService mOWService;
    private Context mContext;
    private CurrentWeather mCurrentWeather;

    public OpenWeatherWrapper(Context context) {
        //Set the app's context
        this.mContext = context;
        //Set the open weather api key
        this.mOWService = new OWService(this.mContext.getResources().getString(R.string.open_weather_api_key));
        //Setting the language and metrics
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = context.getResources().getConfiguration().locale;
        }
        this.mOWService.setLanguage(locale);
        this.mOWService.setMetricUnits(OWSupportedUnits.METRIC);
    }

    public void requestCurrentForecastWeather(Location pLocation, final IWeather pListener) {
        if (this.mOWService != null) {
            //Set the coordinates
            Coord coordinate = new Coord();
            coordinate.setLat(pLocation.getLatitude());
            coordinate.setLon(pLocation.getLongitude());
            //Request the weather forecast
            this.mOWService.getCurrentDayForecast(coordinate, new OWRequestListener<CurrentWeather>() {

                @Override
                public void onResponse(OWResponse<CurrentWeather> owResponse) {
                    OpenWeatherWrapper.this.mCurrentWeather = owResponse.body();
                    if (pListener != null) {
                        pListener.onForecastRequest(OpenWeatherWrapper.this);
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Log.e(TAG, "Current Day Forecast request failed: " + throwable.getMessage());
                }
            });
        }
    }

    public Double getTemperature() {
        if (this.mCurrentWeather != null) {
            return this.mCurrentWeather.getMain().getTemp();
        } else {
            return null;
        }
    }

    public Integer getHumidity() {
        if (this.mCurrentWeather != null) {
            return this.mCurrentWeather.getMain().getHumidity();
        } else {
            return null;
        }
    }

    public Double getPressure() {
        if (this.mCurrentWeather != null) {
            return this.mCurrentWeather.getMain().getPressure();
        } else {
            return null;
        }
    }

    public Wind getWind() {
        if (this.mCurrentWeather != null) {
            return this.mCurrentWeather.getWind();
        } else {
            return null;
        }
    }
}
