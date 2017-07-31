package org.forestguardian.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.forestguardian.DataAccess.Local.User;
import org.forestguardian.DataAccess.NASA.MODIS;
import org.forestguardian.DataAccess.OSM.FireStation;
import org.forestguardian.DataAccess.OSM.OverpassWrapper;
import org.forestguardian.DataAccess.OSM.WaterResource;
import org.forestguardian.DataAccess.Weather.OpenWeatherWrapper;
import org.forestguardian.DataAccess.WebMapInterface;
import org.forestguardian.DataAccess.WebServer.ForestGuardianAPI;
import org.forestguardian.Helpers.AuthenticationController;
import org.forestguardian.Helpers.GeoHelper;
import org.forestguardian.Helpers.IContants;
import org.forestguardian.Helpers.NotificationsIdManager;
import org.forestguardian.R;
import org.forestguardian.View.Fragments.DefaultMapInteractionFragment;
import org.forestguardian.View.Fragments.ReportLocalizationFragment;
import org.forestguardian.View.Fragments.RouteMapInteractionFragment;
import org.forestguardian.View.Fragments.WildfireResourcesMapInteractionFragment;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static android.view.View.GONE;

@RuntimePermissions
public class MapActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DefaultMapInteractionFragment.OnDefaultInteractionListener, ReportLocalizationFragment.OnReportLocalizationListener,
        WildfireResourcesMapInteractionFragment.OnGeneralInteractionListener, RouteMapInteractionFragment.OnRouteInteractionListener {

    public final static int REPORT_CREATION_REQUEST = 234;

    private static String TAG = MapActivity.class.getSimpleName();
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

    @BindView(R.id.main_layout)             FrameLayout       mMainLayout;
    @BindView(R.id.nav_view)                NavigationView    mNavView;
    @BindView(R.id.drawer_layout)           DrawerLayout      mDrawerLayout;
    @BindView(R.id.toolbar)                 Toolbar           mToolbar;
    @BindView(R.id.map_container)           RelativeLayout    mMapContainer;
    @BindView(R.id.map_web_view)            WebView           mMapWebView;
    @BindView(R.id.map_interaction_layout)  FrameLayout       mInteractionLayout;

    private NavigationHolder        navHolder;
    private Fragment                mMapInteractionFragment;
    private Fragment                mMapGeneralInteractionFragment;
    private Fragment                mMapRouteInteractionFragment;
    private Fragment                mReportLocalizationFragment;
    private Fragment                mCurrentFragment;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        navHolder = new NavigationHolder(mNavView);
        navHolder.header.email.setText(AuthenticationController.shared().getCurrentUser().getEmail());
        navHolder.header.name.setText(AuthenticationController.shared().getCurrentUser().getName());
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
        MapActivityPermissionsDispatcher.initLocationWithCheck(this);
        //Variable default values
        this.mCurrentLocation = null;
        this.mIsCurrentLocation = false;

        //Load Fragment
        loadDefaultInteraction();
        loadProfileAvatar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Check for search queries
        checkSearchIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        checkSearchIntent(intent);
    }

    private void checkSearchIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            processSearchQuery(query);
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            String locationData = intent.getData().getLastPathSegment();
            Location locationPoint = GeoHelper.convertStringToLocation(locationData);
            if (locationPoint != null) {
                setMapLocation(locationPoint);
            }
        }
    }

    private void processSearchQuery(String query) {
        Log.i(TAG, "Searching for: " + query);
        new Thread(() -> {
            Location searchPoint = null;
            try {
                searchPoint = GeoHelper.getPointFromAddressName(MapActivity.this, query);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (searchPoint != null) {
                setMapLocation(searchPoint);
            }
        }).start();
    }

    @Override
    public void onBackPressed() {

        // Remove marker.
        MapActivity.this.mMapWebView.post(() -> MapActivity.this.mMapWebView.loadUrl(
                "javascript:clearReportLocation()") );

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
        getMenuInflater().inflate(R.menu.notification, menu);
        getMenuInflater().inflate(R.menu.search, menu);
        //getMenuInflater().inflate(R.menu.map, menu);
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
        if (id == R.id.action_search) {
            onSearchRequested();
            return true;
        }
        if (id == R.id.action_notification) {
            Log.i(TAG, "NOTIFICATION ICON PRESSED IN THE ACTION BAR!!");
            Intent intent = new Intent(getApplicationContext(), NotificationsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_reportes) {

            // Load Profile activity who also contains a list of all reports.
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_notifications) {


        } else if (id == R.id.nav_logout) {

            /* Show confirmation dialog */
            DialogInterface.OnClickListener listener = (dialog, option) -> {
                switch (option) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // destroy session and go to SignInActivity
                        AuthenticationController.shared().logout();
                        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
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

        } else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

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
        this.mMapInterface = new WebMapInterface(this);
        this.mMapWebView.addJavascriptInterface(this.mMapInterface, "mobile");
        //Capture load errors
        this.mMapWebView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Log.e(TAG, "Error loading the URL");
                //TODO: set an error page
                //this.mMapWebView.loadUrl("file:///android_asset/myerrorpage.html");

            }
        });
    }

    private void loadProfileAvatar(){
        navHolder.header.progress.setVisibility(View.VISIBLE);
        Observable.create(e -> {
            User currentUser = AuthenticationController.shared().getCurrentUser();

            String avatar = currentUser.getAvatar();
            if (avatar == null){
                if (!e.isDisposed()){
                    e.onError(null);
                    e.onComplete();
                }
                return;
            }
            try {
                Bitmap picture = BitmapFactory.decodeStream(new URL(avatar).openConnection().getInputStream());
                if (!e.isDisposed()){
                    e.onNext(picture);
                    e.onComplete();
                }
            }catch(MalformedURLException error){
                error.printStackTrace();
                if (!e.isDisposed()){
                    e.onError(null);
                    e.onComplete();
                }
                return;
            }

        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( bitmap -> {
                    if ( bitmap != null ){
                        navHolder.header.avatar.setImageBitmap((Bitmap) bitmap);
                    }else{
                        Toast.makeText(this,"bitmap is null",Toast.LENGTH_SHORT).show();
                    }
                    navHolder.header.progress.setVisibility(View.GONE);
                }, e -> navHolder.header.progress.setVisibility(View.GONE) );
    }


    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    void initLocation() {

        /* init the location manager */
        this.mLocationManager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onLocationChanged(Location location) {

                if (MapActivity.this.mCurrentLocation == null) {
                    //TODO: Move this string to the string.xml file
                    Toast.makeText(MapActivity.this, "Información de GPS lista.", Toast.LENGTH_LONG).show();
                }

                MapActivity.this.mCurrentLocation = location;
                Log.i("Location", "Changed to: " + String.valueOf(location.getLatitude()) + ", " + String.valueOf(location.getLongitude()));
                setLocationText();
                if (!MapActivity.this.mIsCurrentLocation) {
                    MapActivity.this.mMapWebView.post(() -> MapActivity.this.mMapWebView.loadUrl("javascript:setUserCurrentLocation(" + String.valueOf(location.getLatitude()) + ", " + String.valueOf(location.getLongitude()) + ")"));
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

                /* Check the status of the location signal */
                String toastMessage = "";
                switch (status) {
                    case LocationProvider.OUT_OF_SERVICE:
                        //TODO: Move this string to the string.xml file
                        toastMessage = "Señal de GPS no disponible";
                        //TODO: Move this string to the string.xml file
                        changeGPSLabel("GPS no disponible");
                        break;
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        //TODO: Move this string to the string.xml file
                        toastMessage = "La señal de GPS presenta problemas";
                        //TODO: Move this string to the string.xml file
                        changeGPSLabel("Cargando ubicación...");
                        break;
                    case LocationProvider.AVAILABLE:
                        //TODO: Move this string to the string.xml file
                        toastMessage = "Señal de GPS disponible";
                        break;
                }
                /* Show the toast message */
//                Toast.makeText(MapActivity.this, toastMessage, Toast.LENGTH_LONG).show();
                Log.w("Location", toastMessage);
            }

            @Override
            public void onProviderEnabled(String provider) {
                /* Update the message text */
                //TODO: Move this string to the string.xml file
                changeGPSLabel("Cargando ubicación...");
            }

            @Override
            public void onProviderDisabled(String provider) {
                /* Update the message text */
                //TODO: Move this string to the string.xml file
                changeGPSLabel("GPS no disponible");
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Location permission was granted");
            if (this.mLocationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
                this.mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
            }
            if (this.mLocationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
                this.mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);
            }
        } else {
            Log.e(TAG, "Location permission was not granted");
        }
    }

    private void changeGPSLabel(String message) {
        if (MapActivity.this.mMapInteractionFragment != null) {
            Log.i(TAG,"Changing the text to: " + message);
            ((DefaultMapInteractionFragment) MapActivity.this.mMapInteractionFragment).setLocationLabelText(message);
        }
    }

    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    void showRationaleForCamera(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage("Necesitamos averiguar tu localizacion para poder crear un reporte.")
                .setPositiveButton("Aceptar", (dialog, button) -> request.proceed())
                .setNegativeButton("Cancelar", (dialog, button) -> request.cancel())
                .show();
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

                    //Set the address of the firestation
                    new Thread(() -> {
                        try {
                            FireStation tmpFirestation = MapActivity.this.mFireStations.get(MapActivity.this.mFireStations.size() - 1);
                            String firestationAddress = GeoHelper.getAddressNameFromPoint(MapActivity.this, tmpFirestation.getCoordinate());
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
            Toast.makeText(this, R.string.msg_waiting_gps, Toast.LENGTH_LONG).show();
            return;
        }

        MapActivity.this.mMapWebView.post(() ->
            MapActivity.this.mMapWebView.loadUrl("javascript:setUserCurrentLocation(" +
            String.valueOf(mCurrentLocation.getLatitude()) + ", " +
            String.valueOf(mCurrentLocation.getLongitude()) + ")"));
    }

    private void setMapLocation(Location point) {
        if (point == null) {
            return;
        }

        MapActivity.this.mMapWebView.post(() ->
                MapActivity.this.mMapWebView.loadUrl("javascript:setUserCurrentLocation(" +
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
            Toast.makeText(this, R.string.msg_waiting_gps, Toast.LENGTH_LONG).show();
            return;
        }

        MapActivity.this.mMapWebView.post(() ->
                MapActivity.this.mMapWebView.loadUrl("javascript:prepareReportLocation()"));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setLocationText() {
        if (mCurrentLocation == null) {
            return;
        }

        new Thread(() -> {
            String locationText = "";
            try {
                locationText = GeoHelper.getAddressNameFromPoint(MapActivity.this, mCurrentLocation);
                if (MapActivity.this.mMapInteractionFragment != null && mCurrentLocation != null) {
                    Log.i(TAG, "Resetting the location information");
                    ((DefaultMapInteractionFragment) MapActivity.this.mMapInteractionFragment).setCurrentLocation(locationText);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // endregion

    // region Reports Creation

    @Override
    public void addReport() {
        Log.d("ButtonAction","addReport");

        if ( mCurrentLocation == null ){
            Log.w("addReport","No location information yet.");
            Toast.makeText(this, R.string.msg_waiting_gps, Toast.LENGTH_LONG).show();
            return;
        }

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
        startActivityForResult(intent,REPORT_CREATION_REQUEST);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void handleReportCreation(int resultCode ){
        switch( resultCode ){
            case CreateReportActivity.SUCCESS_RESULT:

                // Load default map menu.
                loadDefaultInteraction();

                // Report success.
                String msg = "Reported!";
                Log.i("Report",msg);
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

                // Remove marker.
                MapActivity.this.mMapWebView.post(() -> MapActivity.this.mMapWebView.loadUrl(
                        "javascript:clearReportLocation()") );
                break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
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
        MapActivity.this.mMapWebView.post(() -> {
            if (fstartPlace != null && fendPlace != null) {
                String newRouteURL = "javascript:setRouteFromTwoPoints(" + String.valueOf(fstartPlace.getLatitude()) + ", " + String.valueOf(fstartPlace.getLongitude()) + ", " + String.valueOf(fendPlace.getLatitude()) + ", " + String.valueOf(fendPlace.getLongitude()) + ")";
                MapActivity.this.mMapWebView.loadUrl(newRouteURL);
            } else {
                Toast.makeText(this, "Error desplegando la ruta", Toast.LENGTH_LONG).show();
            }
        });
        loadDefaultInteraction();
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
        @BindView(R.id.nav_user_pic_progress) ProgressBar progress;


        public NavigationHeaderHolder(View view){
            ButterKnife.bind(this,view);
        }
    }

    private void loadNewInteraction(Fragment fragment){

        Log.d("Replacing Fragment",fragment.getClass().getCanonicalName());
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        if ( mCurrentFragment != null ){
            transaction.replace(R.id.map_interaction_layout, fragment);
            transaction.addToBackStack(null);
        }else{
            transaction.add(R.id.map_interaction_layout, fragment);
        }
        mCurrentFragment = fragment;
        transaction.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void loadDefaultInteraction(){

        if (mMapInteractionFragment == null) {
            mMapInteractionFragment = new DefaultMapInteractionFragment();
            ((DefaultMapInteractionFragment) mMapInteractionFragment).setListener(this);
        }
        loadNewInteraction(mMapInteractionFragment);

        //TODO: Move this string to the string.xml file
        changeGPSLabel("Cargando ubicación...");
        setLocationText();
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
            Toast.makeText(this, R.string.msg_waiting_gps, Toast.LENGTH_LONG).show();
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

}
