package org.forestguardian.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.forestguardian.DataAccess.NASA.MODIS;
import org.forestguardian.DataAccess.OSM.FireStation;
import org.forestguardian.DataAccess.OSM.WaterResource;
import org.forestguardian.DataAccess.Weather.OpenWeatherWrapper;
import org.forestguardian.DataAccess.OSM.OverpassWrapper;
import org.forestguardian.DataAccess.WebMapInterface;
import org.forestguardian.DataAccess.WebServer.ForestGuardianAPI;
import org.forestguardian.ForestGuardianApplication;
import org.forestguardian.Helpers.GeoHelper;
import org.forestguardian.R;
import org.forestguardian.View.CustomViews.RouteDetails;
import org.forestguardian.View.CustomViews.WildfireDetails;
import org.forestguardian.View.Fragments.DefaultMapInteractionFragment;
import org.forestguardian.View.Fragments.ReportLocalizationFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DefaultMapInteractionFragment.OnDefaultInteractionListener, ReportLocalizationFragment.OnReportLocalizationListener
{

    private static String TAG = "MapActivity";
    private boolean mInDefaultMap;
    private boolean mIsCurrentLocation;
    private Location mCurrentLocation;
    private LocationManager mLocationManager;
    private WebMapInterface mMapInterface;
    private ArrayList<FireStation> mFireStations;
    private FireStation mNearestFireStation;
    private ArrayList<WaterResource> mWaterResources;
    private OpenWeatherWrapper mWeather;
    private MODIS mMODIS;

    @BindView(R.id.nav_view) NavigationView mNavView;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.map_container) RelativeLayout mMapContainer;
    @BindView(R.id.map_web_view) WebView mMapWebView;
    @BindView(R.id.map_interaction_layout) FrameLayout mInteractionLayout;
    private NavigationHolder navHolder;

    private Fragment mMapInteractionFragment;
    private Fragment mReportLocalizationFragment;
    private Fragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        navHolder = new NavigationHolder(mNavView);
        navHolder.header.email.setText( ((ForestGuardianApplication)getApplication()).getCurrentUser().getEmail() );
        navHolder.header.name.setText( "Welcome random citizen!" );
        // TODO: navHolder.header.name.setText( ((ForestGuardianApplication)getApplication()).getCurrentUser().getName() );
        // TODO: same but with avatar. Is this required?

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavView.setNavigationItemSelectedListener(this);

        //Init some attributes
        this.mFireStations = new ArrayList<FireStation>();
        this.mNearestFireStation = null;
        this.mWaterResources = new ArrayList<WaterResource>();
        this.mWeather = null;
        this.mMODIS = null;
        //Init the map
        initWebMap();
        //Init the GPS location
        initLocation();
        //Variable default values
        this.mCurrentLocation = null;
        this.mIsCurrentLocation = false;

        //Load Fragment
        loadDefaultInteraction();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_reportes) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.logout) {

            /* Show confirmation dialog */
            DialogInterface.OnClickListener listener = (dialog, option) -> {
                switch (option){
                    case DialogInterface.BUTTON_POSITIVE:
                        // destroy session and go to SignInActivity
                        ((ForestGuardianApplication)getApplication()).logout();
                        Intent intent = new Intent(getApplicationContext(),SignInActivity.class);
                        startActivity(intent);
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // Nothing
                        break;
                }
            };

            AlertDialog.Builder logoutConfirmation = new AlertDialog.Builder(this);
            logoutConfirmation.setMessage("Are you sure?")
                    .setPositiveButton("Logout", listener)
                    .setNegativeButton("Cancel", listener)
                    .show();

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebMap() {
        //Load the default map
        // TODO: Improve path assignation.
        this.mMapWebView.loadUrl(ForestGuardianAPI.FOREST_GUARDIAN_WEB_SERVICE_ENDPOINT + getResources().getString(R.string.web_view_map_1_url));
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
        this.mMapInterface = new WebMapInterface(this);
        this.mMapWebView.addJavascriptInterface(this.mMapInterface, "mobile");
    }

    private void initLocation() {
        this.mLocationManager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e("Permissions","not enough permissions");
            return;
        }

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                MapActivity.this.mCurrentLocation = location;
                Log.i("Location","Changed to: " + String.valueOf(location.getLatitude()) + ", " + String.valueOf(location.getLongitude()));
                if (!MapActivity.this.mIsCurrentLocation) {
                    MapActivity.this.mMapWebView.post(() -> MapActivity.this.mMapWebView.loadUrl("javascript:setUserCurrentLocation(" + String.valueOf(location.getLatitude()) + ", " + String.valueOf(location.getLongitude()) + ")"));
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        this.mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
        this.mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);
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

    public void processWildfireData(MODIS modisData) {
        //Reset attribute's values
        this.resetAttributes();
        //Reset route
        this.mMapWebView.post(() -> {
            MapActivity.this.mMapWebView.loadUrl("javascript:removeRoute()");
            MapActivity.this.mMapWebView.loadUrl("javascript:removeFireStationMark()");
            MapActivity.this.mMapWebView.loadUrl("javascript:removeWildfireMessage()");
        });

        //Create the wildfire coordinate
        this.mMODIS = modisData;
        Location wildfireCoordinates = modisData.getCoordinate();

        //Get the weather info
        OpenWeatherWrapper openWeatherWrapper = new OpenWeatherWrapper(this);
        openWeatherWrapper.requestCurrentForecastWeather(wildfireCoordinates, openWeatherWrapper1 -> {
            MapActivity.this.mWeather = openWeatherWrapper1;
            MapActivity.this.mMapWebView.post(() -> {
                Log.d(TAG, "Creating popup");
                MapActivity.this.mMapWebView.loadUrl("javascript:addWildfireMessage(" + String.valueOf(MapActivity.this.mMODIS.getCoordinate().getLatitude()) + "," +
                        String.valueOf(MapActivity.this.mMODIS.getCoordinate().getLongitude()) + "," +
                        String.valueOf(MapActivity.this.mMODIS.getBrightness()) + "," +
                        String.valueOf(MapActivity.this.mWeather.getTemperature()) + "," +
                        String.valueOf(MapActivity.this.mWeather.getHumidity()) + ")");
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
                    MapActivity.this.mFireStations.add(fireStation);

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
                    MapActivity.this.mNearestFireStation = nearestFireStation;
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
                    MapActivity.this.mWaterResources.add(waterResource);
                }
            } else {
                Log.e(TAG, "Error getting the rivers data!!");
            }
        });
    }

    public void showWildfireDetails() {
        this.mMapWebView.post(() -> MapActivity.this.mMapWebView.loadUrl("javascript:removeWildfireMessage()"));

        runOnUiThread(() -> {
            RouteDetails routeDetails = new RouteDetails(MapActivity.this);
            routeDetails.setMODISData(MapActivity.this.mMODIS);
            if (MapActivity.this.mWaterResources.size() > 0) {
                routeDetails.setOSMData(MapActivity.this.mNearestFireStation, MapActivity.this.mWaterResources.get(0));
            } else {
                routeDetails.setOSMData(MapActivity.this.mNearestFireStation, null);
            }
            MapActivity.this.mMapContainer.addView(routeDetails);
        });

    }

    public void removeWildfireDetails(RouteDetails routeDetails) {
        final RouteDetails tmpRouteDetails = routeDetails;
        runOnUiThread(() -> MapActivity.this.mMapContainer.removeView(tmpRouteDetails));
    }

    public void showRouteDetails(RouteDetails routeDetails) {
        removeWildfireDetails(routeDetails);

        final FireStation tmpNearestFireStation = this.mNearestFireStation;
        MapActivity.this.mMapWebView.post(() -> {
            if (MapActivity.this.mNearestFireStation != null) {
                MapActivity.this.mMapWebView.loadUrl("javascript:setRouteFromTwoPoints(" + String.valueOf(MapActivity.this.mMODIS.getCoordinate().getLatitude()) + ", " + String.valueOf(MapActivity.this.mMODIS.getCoordinate().getLongitude()) + ", " + String.valueOf(tmpNearestFireStation.getCoordinate().getLatitude()) + ", " + String.valueOf(tmpNearestFireStation.getCoordinate().getLongitude()) + ")");
                MapActivity.this.mMapWebView.loadUrl("javascript:addFireStationMark(" + String.valueOf(tmpNearestFireStation.getCoordinate().getLatitude()) + ", " + String.valueOf(tmpNearestFireStation.getCoordinate().getLongitude()) + ")");
            }
        });

        runOnUiThread(() -> {
            WildfireDetails wildfireDetails = new WildfireDetails(MapActivity.this);
            wildfireDetails.setMODISData(MapActivity.this.mMODIS);
            wildfireDetails.setWeatherData(MapActivity.this.mWeather);
            MapActivity.this.mMapContainer.addView(wildfireDetails);
        });
    }

    public void removeRouteDetails(WildfireDetails wildfireDetails) {
        final WildfireDetails tmpWildfireDetails = wildfireDetails;
        runOnUiThread(() -> MapActivity.this.mMapContainer.removeView(tmpWildfireDetails));
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
        MapActivity.this.mMapWebView.post(() ->
            MapActivity.this.mMapWebView.loadUrl("javascript:setUserCurrentLocation(" +
            String.valueOf(mCurrentLocation.getLatitude()) + ", " +
            String.valueOf(mCurrentLocation.getLongitude()) + ")"));;
    }

    @Override
    public void reportLocationReady() {
        Log.d("ButtonAction","reportLocationReady");
        MapActivity.this.mMapWebView.post(() ->
                MapActivity.this.mMapWebView.loadUrl("javascript:prepareReportLocation()"));
    }

    // endregion

    // region Reports Creation

    @Override
    public void addReport() {
        Log.d("ButtonAction","addReport");
        MapActivity.this.mMapWebView.post(() -> MapActivity.this.mMapWebView.loadUrl(
            "javascript:addReportLocation(" + String.valueOf(mCurrentLocation.getLatitude()) +
            ", " + String.valueOf(mCurrentLocation.getLongitude()) + ")") );
        loadReportLocalizationInteraction();
    }

    public void openReportCreation(final Double pLatitude, final Double pLongitude){
        Log.d("FragmentsAction","openReportCreation");
        Intent intent = new Intent(this, CreateReportActivity.class);
        intent.putExtra("latitude",pLatitude);
        intent.putExtra("longitude",pLongitude);
        startActivity(intent);
    }

    // endregion

    /**
     *  The following static classes are required to properly use Butterknife.bind
     *  in certain nested news.
     *  Check https://guides.codepath.com/android/Reducing-View-Boilerplate-with-Butterknife
     * */

    static class NavigationHolder {

        public NavigationHeaderHolder header;

        public NavigationHolder(NavigationView view){
            ButterKnife.bind(this,view);
            header = new NavigationHeaderHolder( view.getHeaderView(0) );
        }
    }

    static class NavigationHeaderHolder {
        @BindView(R.id.nav_user_name) TextView name;
        @BindView(R.id.nav_user_email) TextView email;
        @BindView(R.id.nav_user_pic) ImageView avatar;

        public NavigationHeaderHolder(View view){
            ButterKnife.bind(this,view);
        }
    }

    private void loadNewInteraction(Fragment fragment){

        Log.d("Replacing Fragment",fragment.getClass().getCanonicalName());

        if ( mCurrentFragment != null ){
            getFragmentManager().beginTransaction().replace(R.id.map_interaction_layout, fragment).commit();
        }else{
            getFragmentManager().beginTransaction().add(R.id.map_interaction_layout, fragment).commit();
        }
        mCurrentFragment = fragment;
    }

    private void loadDefaultInteraction(){
        if (mMapInteractionFragment == null) {
            mMapInteractionFragment = new DefaultMapInteractionFragment();
            ((DefaultMapInteractionFragment) mMapInteractionFragment).setListener(this);
        }
        loadNewInteraction(mMapInteractionFragment);
    }

    private void loadReportLocalizationInteraction(){
        if (mReportLocalizationFragment == null) {
            mReportLocalizationFragment = new ReportLocalizationFragment();
            ((ReportLocalizationFragment) mReportLocalizationFragment).setListener(this);
        }
        loadNewInteraction(mReportLocalizationFragment);
    }

}
