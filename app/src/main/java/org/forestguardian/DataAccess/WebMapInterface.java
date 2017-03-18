package org.forestguardian.DataAccess;

import android.content.Context;
import android.webkit.JavascriptInterface;

import org.forestguardian.View.MapActivity;
import org.json.JSONException;
import org.json.JSONObject;

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
        try {
            jsonMODIS = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ((MapActivity)mContext).processWildfireData(jsonMODIS);
    }

    @JavascriptInterface
    public void notifyCurrentLocation() {
        ((MapActivity)mContext).setIsCurrentLocation(true);
    }
}
