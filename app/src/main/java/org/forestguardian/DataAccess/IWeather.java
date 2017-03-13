package org.forestguardian.DataAccess;

/**
 * Created by luisalonsomurillorojas on 12/3/17.
 */

public interface IWeather {
    void onForecastRequest(OpenWeatherWrapper openWeatherWrapper);
}
