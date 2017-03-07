package org.forestguardian;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * Created by luisalonsomurillorojas on 6/3/17.
 */

public class WebMapInterface {

    private static final String TAG = "WebMapInterface";
    private Context mContext;

    public WebMapInterface(Context context) {
        this.mContext = context;
    }

    @JavascriptInterface
    public void getMODISData(String data) {
        Toast.makeText(this.mContext, data, Toast.LENGTH_LONG).show();
    }
}
