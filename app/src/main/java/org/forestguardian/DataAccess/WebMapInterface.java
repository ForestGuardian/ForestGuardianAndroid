package org.forestguardian.DataAccess;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import org.forestguardian.DataAccess.NASA.MODIS;
import org.forestguardian.Helpers.GeoHelper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by luisalonsomurillorojas on 12/3/17.
 */

public class WebMapInterface {

    public interface WebMapInterfaceListener {

        void processWildfireData(MODIS modisData);
        void setIsCurrentLocation(boolean mIsCurrentLocation);
        void showWildfireDetails();
        void openReportCreation(final Double pLatitude, final Double pLongitude);
    }

    private static final String TAG = "WebMapInterface";
    private Context mContext;
    private WebMapInterfaceListener mListener;

    public WebMapInterface(Context context, WebMapInterfaceListener pListener) {
        this.mContext = context;
        this.mListener = pListener;
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

        mListener.processWildfireData(modis);
    }

    @JavascriptInterface
    public void notifyCurrentLocation() {
        mListener.setIsCurrentLocation(true);
    }

    @JavascriptInterface
    public void showWildfireDetails() {
        mListener.showWildfireDetails();
    }

    @JavascriptInterface
    public void reportLocation( String pLatitude, String pLongitude ){
        Log.d("ReportLocation",pLatitude+ " - " + pLongitude);
        mListener.openReportCreation(Double.valueOf(pLatitude),Double.valueOf(pLongitude));
    }

    @JavascriptInterface
    public void notifyRouteError() {
        Toast.makeText(mContext, "Error al trazar la ruta", Toast.LENGTH_LONG).show();
    }
}
