package org.forestguardian.DataAccess;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

import org.forestguardian.DataAccess.NASA.MODIS;
import org.forestguardian.Helpers.GeoHelper;
import org.forestguardian.View.MapActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by luisalonsomurillorojas on 12/3/17.
 */

public class WebMapInterface {

    private static final String TAG = "WebMapInterface";
    private Context mContext;

    public WebMapInterface(Context context) {
        this.mContext = context;
    }

    @JavascriptInterface
    public void getMODISData(String data) {
        //Parse the JSON data
        JSONObject jsonMODIS = null;
        MODIS modis = null;
        try {
            jsonMODIS = new JSONObject(data);
            modis = new MODIS(jsonMODIS);
            modis.setPlaceName(GeoHelper.getAddressNameFromPoint(this.mContext, modis.getCoordinate()));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ((MapActivity)mContext).processWildfireData(modis);
    }

    @JavascriptInterface
    public void notifyCurrentLocation() {
        ((MapActivity)mContext).setIsCurrentLocation(true);
    }

    @JavascriptInterface
    public void showWildfireDetails() {
        ((MapActivity)mContext).showWildfireDetails();
    }

    @JavascriptInterface
    public void reportLocation( Double latitude, Double longitude ){
        Log.d("ReportLocation",String.valueOf(latitude) + " - " + String.valueOf(longitude));
        ((MapActivity)mContext).openReportCreation();
    }
}
