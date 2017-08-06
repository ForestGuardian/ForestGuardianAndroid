package org.forestguardian.View.Fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.forestguardian.DataAccess.Location.LocationController;
import org.forestguardian.DataAccess.NASA.MODIS;
import org.forestguardian.DataAccess.OSM.FireStation;
import org.forestguardian.DataAccess.OSM.OverpassWrapper;
import org.forestguardian.DataAccess.OSM.WaterResource;
import org.forestguardian.DataAccess.Weather.OpenWeatherWrapper;
import org.forestguardian.DataAccess.WebMapInterface;
import org.forestguardian.DataAccess.WebServer.ForestGuardianAPI;
import org.forestguardian.Helpers.GeoHelper;
import org.forestguardian.Helpers.IContants;
import org.forestguardian.R;
import org.forestguardian.View.CreateReportActivity;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by emma on 02/08/17.
 */

public class MapFragment extends Fragment implements
        DefaultMapInteractionFragment.OnDefaultInteractionListener, ReportLocalizationFragment.OnReportLocalizationListener,
        WildfireResourcesMapInteractionFragment.OnGeneralInteractionListener, RouteMapInteractionFragment.OnRouteInteractionListener,
        WebMapInterface.WebMapInterfaceListener, LocationController.SimpleLocationListener{

    private static final int REPORT_CREATION_REQUEST = 23432;
    public static String TAG = "MapFragment";

    private Fragment                mMapInteractionFragment;
    private Fragment                mMapGeneralInteractionFragment;
    private Fragment                mMapRouteInteractionFragment;
    private Fragment                mReportLocalizationFragment;
    private Location                mCurrentLocation = null;
    private String                  mCurrentLocationText = null;

    private enum WebViewState {
        CREATED, LOADED, READY
    }
    private WebViewState mWebViewState;

    private boolean mInDefaultMap;
    private boolean mIsCurrentLocation;
    private WebMapInterface mMapInterface;
    private ArrayList<FireStation> mFireStations;
    private FireStation mNearestFireStation;
    private ArrayList<WaterResource> mWaterResources;
    private OpenWeatherWrapper mWeather;
    private MODIS mMODIS;

    @BindView(R.id.map_container) RelativeLayout mMapContainer;
    @BindView(R.id.map_web_view) WebView mMapWebView;
    @BindView(R.id.map_interaction_layout) FrameLayout mInteractionLayout;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mFireStations = new ArrayList<FireStation>();
        this.mNearestFireStation = null;
        this.mWaterResources = new ArrayList<WaterResource>();
        this.mWeather = null;
        this.mMODIS = null;
        this.mIsCurrentLocation = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.map_fragment_layout, container, false);
        ButterKnife.bind(this, view);

        //Init the map
        initWebMap();

        mMapInteractionFragment = new DefaultMapInteractionFragment();
        ((DefaultMapInteractionFragment)mMapInteractionFragment).setListener(this);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.map_interaction_layout, mMapInteractionFragment);
        transaction.commit();

        this.mWebViewState = WebViewState.CREATED;

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clear
    }

//    @Override
//    public void onSaveInstanceState(final Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putParcelable("currentLocation",mCurrentLocation);
//        outState.putBoolean("isCurrentLocation", mIsCurrentLocation);
//    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebMap() {
        //Load the default map
        // TODO: Improve path assignation.
        this.mMapWebView.loadUrl(ForestGuardianAPI.FOREST_GUARDIAN_WEB_SERVICE_ENDPOINT + IContants.WIND_BASEMAP);
        //Getting the webview settings
        WebSettings webSettings = this.mMapWebView.getSettings();
        //Enable javascript
        webSettings.setJavaScriptEnabled(true);
        // Make the zoom controls visible
        webSettings.setBuiltInZoomControls(true);
        // Allow for touching selecting/deselecting data series
        this.mMapWebView.requestFocusFromTouch();
        //Set map flag
        this.mInDefaultMap = true;
        //Setup the javascript interface
        this.mMapInterface = new WebMapInterface(getActivity(), this);
        this.mMapWebView.addJavascriptInterface(this.mMapInterface, "mobile");
        //Capture load errors
        this.mMapWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Log.e(TAG, "Error loading the URL");
                //TODO: set an error page
                //this.mMapWebView.loadUrl("file:///android_asset/myerrorpage.html");

            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mWebViewState = WebViewState.LOADED;
                if ( mCurrentLocation != null ){
                    onGPSChanged(mCurrentLocation,mCurrentLocationText);
                }
            }
        });

    }

    private void resetAttributes(){
        if (this.mFireStations != null) {
            this.mFireStations.clear();
        }
        if (this.mNearestFireStation != null) {
            this.mNearestFireStation = null;
        }
        if (this.mWaterResources != null) {
            this.mWaterResources.clear();
        }
        if (this.mWeather != null) {
            this.mWeather = null;
        }
        if (this.mMODIS != null) {
            this.mMODIS = null;
        }
    }

    private void changeGPSLabel(String message) {
        if (mMapInteractionFragment != null) {
            Log.i(TAG,"Changing the text to: " + message);
            ((DefaultMapInteractionFragment) mMapInteractionFragment).setLocationLabelText(message);
        }
    }

    /**
     *  The following static classes are required to properly use Butterknife.bind
     *  in certain nested news.
     *  Check https://guides.codepath.com/android/Reducing-View-Boilerplate-with-Butterknife
     * */


    private void clearInteractions(){
        Log.d(TAG,"Clearing Fragments");
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
    }


    private void loadNewInteraction(Fragment fragment){

        if ( currentFragment() == fragment ){
            Log.d(TAG,"Replacing fragment with itself");
            return;
        }

        Log.d("Replacing Fragment",fragment.getClass().getCanonicalName());
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.map_interaction_layout, fragment);
        transaction.addToBackStack(fragment.getClass().getCanonicalName());
        transaction.commit();
    }

    private void loadDefaultInteraction(){

        if (mMapInteractionFragment == null) {
            mMapInteractionFragment = new DefaultMapInteractionFragment();
            ((DefaultMapInteractionFragment) mMapInteractionFragment).setListener(this);
        }
        loadNewInteraction(mMapInteractionFragment);

        //TODO: Move this string to the string.xml file
        changeGPSLabel("Cargando ubicación...");
    }

    private void loadReportLocalizationInteraction(){
        if (mReportLocalizationFragment == null) {
            mReportLocalizationFragment = new ReportLocalizationFragment();
            ((ReportLocalizationFragment) mReportLocalizationFragment).setListener(this);
        }
        loadNewInteraction(mReportLocalizationFragment);
    }

    private void loadGeneralInfoInteraction() {
        if (mWaterResources.size() > 0) {
            mMapGeneralInteractionFragment = WildfireResourcesMapInteractionFragment.setFireData(this.mMODIS, this.mNearestFireStation, this.mWaterResources.get(0));
        } else {
            mMapGeneralInteractionFragment = WildfireResourcesMapInteractionFragment.setFireData(this.mMODIS, this.mNearestFireStation, null);
        }
        ((WildfireResourcesMapInteractionFragment) mMapGeneralInteractionFragment).setListener(this);
        loadNewInteraction(mMapGeneralInteractionFragment);
    }

    private void loadRouteInteraction() {
        if ( mCurrentLocation == null ){
            Log.w("loadRouteInteraction","No location information yet.");
            Toast.makeText(getActivity(), R.string.msg_waiting_gps, Toast.LENGTH_LONG).show();
            return;
        }

        if (mWaterResources.size() > 0) {
            mMapRouteInteractionFragment = RouteMapInteractionFragment.setFireData(this.mMODIS, this.mNearestFireStation, this.mWaterResources.get(0), this.mCurrentLocation);
        } else {
            mMapRouteInteractionFragment = RouteMapInteractionFragment.setFireData(this.mMODIS, this.mNearestFireStation, null, this.mCurrentLocation);
        }
        ((RouteMapInteractionFragment) mMapRouteInteractionFragment).setListener(this);
        loadNewInteraction(mMapRouteInteractionFragment);
    }

    private void setLocationText(String locationText) {
        if (mMapInteractionFragment != null && mCurrentLocation != null) {
            Log.i(TAG, "Resetting the location information");
            locationText = locationText == null ? "" :  locationText;
            ((DefaultMapInteractionFragment) mMapInteractionFragment).setCurrentLocation(locationText);
        }
    }

    public void processWildfireData(MODIS modisData) {
        //Reset attribute's values
        this.resetAttributes();
        //Reset route
        this.mMapWebView.post(() -> {
            mMapWebView.loadUrl("javascript:removeRoute()");
            mMapWebView.loadUrl("javascript:removeFireStationMark()");
            mMapWebView.loadUrl("javascript:removeWildfireMessage()");
        });

        //Create the wildfire coordinate
        this.mMODIS = modisData;
        Location wildfireCoordinates = modisData.getCoordinate();

        //Get the weather info
        OpenWeatherWrapper openWeatherWrapper = new OpenWeatherWrapper(getActivity());
        openWeatherWrapper.requestCurrentForecastWeather(wildfireCoordinates, openWeatherWrapper1 -> {
            mWeather = openWeatherWrapper1;
            mMapWebView.post(() -> {
                Log.d(TAG, "Creating popup");
                mMapWebView.loadUrl("javascript:addWildfireMessage(" + String.valueOf(mMODIS.getCoordinate().getLatitude()) + "," +
                        String.valueOf(mMODIS.getCoordinate().getLongitude()) + "," +
                        String.valueOf(mMODIS.getBrightness()) + "," +
                        String.valueOf(mWeather.getTemperature()) + "," +
                        String.valueOf(mWeather.getHumidity()) + ")");
            });
        });

        //Get the nearest fire stations
        OverpassWrapper overpassWrapper = new OverpassWrapper();
        overpassWrapper.setOSMPoint(wildfireCoordinates);
        overpassWrapper.getOSMDataForFireStations(100000, result -> {
            if (result != null) {
                FireStation nearestFireStation = null;
                double tmpDistance = 0;

                Log.i(TAG, "Result: " + result.elements.size());
                for (int index = 0; index < result.elements.size(); index++) {
                    //Initiate the fire station model
                    FireStation fireStation = new FireStation();
                    fireStation.setName(result.elements.get(index).tags.name);
                    fireStation.setCity(result.elements.get(index).tags.addressCity);
                    fireStation.setStreet(result.elements.get(index).tags.addressStreet);
                    fireStation.setOperator(result.elements.get(index).tags.operator);
                    fireStation.setCoordinate(result.elements.get(index).lat, result.elements.get(index).lon);
                    mFireStations.add(fireStation);

                    //Set the address of the firestation
                    new Thread(() -> {
                        try {
                            FireStation tmpFirestation = mFireStations.get(mFireStations.size() - 1);
                            String firestationAddress = GeoHelper.getAddressNameFromPoint(getActivity(), tmpFirestation.getCoordinate());
                            tmpFirestation.setAddress(firestationAddress);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();

                    //Set initial conditions
                    if (nearestFireStation == null) {
                        nearestFireStation = fireStation;
                        tmpDistance = GeoHelper.calculateDistanceBetweenTwoPoints(wildfireCoordinates, fireStation.getCoordinate());
                    }

                    //Verify the distance between the wildfire and the fire stations
                    double currentStationDistance = GeoHelper.calculateDistanceBetweenTwoPoints(wildfireCoordinates, fireStation.getCoordinate());
                    if (currentStationDistance < tmpDistance) {
                        nearestFireStation = fireStation;
                        tmpDistance = currentStationDistance;
                    }
                }
                //Print the nearest fire station information
                if (nearestFireStation != null) {
                    mNearestFireStation = nearestFireStation;
                }
            } else {
                Log.e(TAG, "Error getting the wildfires data!!");
            }
        });

        //Get the nearest rivers
        overpassWrapper.getOSMDataForRivers(1000, result -> {
            if (result != null) {
                Log.i(TAG, "Water resources result: " + result.elements.size());
                for (int index = 0; index < result.elements.size(); index++) {
                    WaterResource waterResource = new WaterResource();
                    waterResource.setName(result.elements.get(index).tags.name);
                    waterResource.setType(result.elements.get(index).tags.type);
                    waterResource.setCoordinate(result.elements.get(index).lat, result.elements.get(index).lon);
                    mWaterResources.add(waterResource);
                }
            } else {
                Log.e(TAG, "Error getting the rivers data!!");
            }
        });
    }

    public void showWildfireDetails() {
        this.mMapWebView.post(() -> mMapWebView.loadUrl("javascript:removeWildfireMessage()"));
        loadGeneralInfoInteraction();
    }

    public boolean isIsCurrentLocation() {
        return mIsCurrentLocation;
    }

    public void setIsCurrentLocation(boolean mIsCurrentLocation) {
        this.mIsCurrentLocation = mIsCurrentLocation;
    }

    // region TapOnCenterLocationInMap

    @Override
    public void centerOnLocation() {
        Log.d("ButtonAction","centerOnLocation");

        if ( mCurrentLocation == null ){
            Log.w("centerOnLocation","No location information yet.");
            Toast.makeText(getActivity(), R.string.msg_waiting_gps, Toast.LENGTH_LONG).show();
            return;
        }

        mMapWebView.post(() -> mMapWebView.loadUrl("javascript:moveToUserCurrentLocation()"));
    }

    public void setMapLocation(Location point) {
        if (point == null) {
            return;
        }

        mMapWebView.post(() -> mMapWebView.loadUrl("javascript:setUserCurrentLocation(" +
                        String.valueOf(point.getLatitude()) + ", " +
                        String.valueOf(point.getLongitude()) + ")"));
    }

    @Override
    public void changeBasemap(String basemapURL) {
        this.mMapWebView.loadUrl(basemapURL);
    }

    @Override
    public void reportLocationReady() {
        Log.d("ButtonAction","reportLocationReady");

        if ( mCurrentLocation == null ){
            Log.w("reportLocationReady","No location information yet.");
            Toast.makeText(getActivity(), R.string.msg_waiting_gps, Toast.LENGTH_LONG).show();
            return;
        }

        mMapWebView.post(() -> mMapWebView.loadUrl("javascript:prepareReportLocation()"));
    }

    // endregion

    // region Reports Creation

    @Override
    public void addReport() {
        Log.d("ButtonAction","addReport");

        if ( mCurrentLocation == null ){
            Log.w("addReport","No location information yet.");
            Toast.makeText(getActivity(), R.string.msg_waiting_gps, Toast.LENGTH_LONG).show();
            return;
        }

        mMapWebView.post(() -> mMapWebView.loadUrl(
                "javascript:addReportLocation()") );
        loadReportLocalizationInteraction();
    }

    public void openReportCreation(final Double pLatitude, final Double pLongitude){
        Log.d("FragmentsAction","openReportCreation");
        Intent intent = new Intent(getActivity(), CreateReportActivity.class);
        intent.putExtra("latitude",pLatitude);
        intent.putExtra("longitude",pLongitude);
        startActivityForResult(intent,REPORT_CREATION_REQUEST);
    }

    private void handleReportCreation(int resultCode ){
        switch( resultCode ){
            case CreateReportActivity.SUCCESS_RESULT:

                // Load default map menu.
                loadDefaultInteraction();

                // Report success.
                String msg = "Reported!";
                Log.i("Report",msg);
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

                // Remove marker.
                mMapWebView.post(() -> mMapWebView.loadUrl(
                        "javascript:clearReportLocation()") );
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REPORT_CREATION_REQUEST:
                handleReportCreation(resultCode);
                break;
            default:
                break;
        }
    }

    @Override
    public void showRouteOptions() {
        loadRouteInteraction();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void drawRoute(Location startPlace, Location endPlace) {
        final Location fstartPlace = startPlace;
        final Location fendPlace = endPlace;
        mMapWebView.post(() -> {
            if (fstartPlace != null && fendPlace != null) {
                String newRouteURL = "javascript:setRouteFromTwoPoints(" + String.valueOf(fstartPlace.getLatitude()) + ", " + String.valueOf(fstartPlace.getLongitude()) + ", " + String.valueOf(fendPlace.getLatitude()) + ", " + String.valueOf(fendPlace.getLongitude()) + ")";
                mMapWebView.loadUrl(newRouteURL);
            } else {
                Toast.makeText(getActivity(), "Error desplegando la ruta", Toast.LENGTH_LONG).show();
            }
        });
        loadDefaultInteraction();
    }

    public Fragment currentFragment(){
        int count = getActivity().getFragmentManager().getBackStackEntryCount();
        if ( count == 0 ){
            return null;
        }
        FragmentManager.BackStackEntry backEntry = getFragmentManager().getBackStackEntryAt(count - 1);
        String tag = backEntry.getName();
        Log.d("currentFragment", tag);
        return getFragmentManager().findFragmentByTag(tag);
    }

    public boolean onBackPressed() {
        // Remove marker.
        mMapWebView.post(() -> mMapWebView.loadUrl("javascript:clearReportLocation()") );
        setLocationText(mCurrentLocationText);

        return false;
    }

    @Override
    public void onGPSChanged(final Location pLocation, final String pLocationName) {

        mCurrentLocation = pLocation;
        mCurrentLocationText = pLocationName;
        if ( mMapWebView == null ){
            return;
        }

        setLocationText(pLocationName);
        mMapWebView.post(() -> mMapWebView.loadUrl("javascript:setUserCurrentLocation(" + String.valueOf(pLocation.getLatitude()) + ", " + String.valueOf(pLocation.getLongitude()) + ")"));

        if( mWebViewState == WebViewState.LOADED ){
            centerOnLocation();
            mWebViewState = WebViewState.READY;
        }
    }

    @Override
    public void onUnavailable() {
        changeGPSLabel("Cargando su ubicación...");
    }
}
