package org.forestguardian.View;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
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

import org.forestguardian.DataAccess.IOverpassAPI;
import org.forestguardian.DataAccess.IWeather;
import org.forestguardian.DataAccess.OpenWeatherWrapper;
import org.forestguardian.DataAccess.OverpassWrapper;
import org.forestguardian.DataAccess.WebMapInterface;
import org.forestguardian.Helpers.GeoHelper;
import org.forestguardian.R;
import org.json.JSONException;
import org.json.JSONObject;

import hu.supercluster.overpasser.adapter.OverpassQueryResult;

public class MapActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static String TAG = "MapActivity";
    private WebView mMapWebView;
    private boolean mInDefaultMap;
    private boolean mIsCurrentLocation;
    private Location mCurrentLocation;
    private LocationManager mLocationManager;
    private WebMapInterface mMapInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MapActivity.this.mInDefaultMap) {
                    MapActivity.this.mInDefaultMap = false;
                    MapActivity.this.mMapWebView.loadUrl(getResources().getString(R.string.web_view_map_2_url));
                } else {
                    MapActivity.this.mInDefaultMap = true;
                    MapActivity.this.mMapWebView.loadUrl(getResources().getString(R.string.web_view_map_1_url));
                }
            }
        });

        FloatingActionButton currentLocationBtn = (FloatingActionButton) findViewById(R.id.current_location);
        currentLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MapActivity.this.mCurrentLocation != null) {
                    MapActivity.this.mMapWebView.loadUrl("javascript:setUserCurrentLocation(" + String.valueOf(MapActivity.this.mCurrentLocation.getLatitude()) + ", " + String.valueOf(MapActivity.this.mCurrentLocation.getLongitude()) + ")");
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Init the map
        initWebMap();
        //Init the GPS location
        initLocation();
        //Variable default values
        this.mCurrentLocation = null;
        this.mIsCurrentLocation = false;
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initWebMap() {
        //Init the web map
        this.mMapWebView = (WebView) findViewById(R.id.map_web_view);
        //Load the default map
        this.mMapWebView.loadUrl(getResources().getString(R.string.web_view_map_1_url));
        //Getting the webview settings
        WebSettings webSettings = this.mMapWebView.getSettings();
        //Enable javascript
        webSettings.setJavaScriptEnabled(true);
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
            return;
        }

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                MapActivity.this.mCurrentLocation = location;
                if (!MapActivity.this.mIsCurrentLocation) {
                    MapActivity.this.mMapWebView.loadUrl("javascript:setUserCurrentLocation(" + String.valueOf(location.getLatitude()) + ", " + String.valueOf(location.getLongitude()) + ")");
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

    public void processWildfireData(JSONObject modisData) {
        Log.i(TAG, "MODIS: " + modisData.toString());
        Location wildfireCoordinates = new Location("");
        try {
            wildfireCoordinates.setLatitude(modisData.getDouble(getResources().getString(R.string.open_weather_api_latitude)));
            wildfireCoordinates.setLongitude(modisData.getDouble(getResources().getString(R.string.open_weather_api_longitude)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Get the weather info
        OpenWeatherWrapper openWeatherWrapper = new OpenWeatherWrapper(this);
        openWeatherWrapper.requestCurrentForecastWeather(wildfireCoordinates, new IWeather() {
            @Override
            public void onForecastRequest(OpenWeatherWrapper openWeatherWrapper) {
                Log.i(TAG, "Temperature: " + openWeatherWrapper.getTemperature()
                        + ", humidity: " + openWeatherWrapper.getHumidity()
                        + ", pressure: " + openWeatherWrapper.getPressure()
                        + ", wind speed: " + openWeatherWrapper.getWind().getSpeed()
                        + ", wind degree: " + openWeatherWrapper.getWind().getDeg());
            }
        });

        //Get the nearest fire stations
        OverpassWrapper overpassWrapper = new OverpassWrapper();
        overpassWrapper.setOSMPoint(wildfireCoordinates);
        overpassWrapper.getOSMDataForFireStations(100000, new IOverpassAPI() {
            @Override
            public void overpassCallback(OverpassQueryResult result) {
                if (result != null) {
                    OverpassQueryResult.Element nearestFireStation = null;
                    double tmpDistance = 0;

                    Log.i(TAG, "Result: " + result.elements.size());
                    for (int index = 0; index < result.elements.size(); index++) {
                        //Initiate the coordinates of the fire stations
                        Location fireStationCoordinates = new Location("");
                        fireStationCoordinates.setLatitude(result.elements.get(index).lat);
                        fireStationCoordinates.setLongitude(result.elements.get(index).lon);

                        //Set initial conditions
                        if (nearestFireStation == null) {
                            nearestFireStation = result.elements.get(index);
                            tmpDistance = GeoHelper.calculateDistanceBetweenTwoPoints(wildfireCoordinates, fireStationCoordinates);
                        }

                        //Verify the distance between the wildfire and the fire stations
                        double currentStationDistance = GeoHelper.calculateDistanceBetweenTwoPoints(wildfireCoordinates, fireStationCoordinates);
                        if (currentStationDistance < tmpDistance) {
                            nearestFireStation = result.elements.get(index);
                            tmpDistance = currentStationDistance;
                        }
                    }
                    //Print the nearest fire station information
                    Log.i(TAG, "Name: " + nearestFireStation.tags.name);
                    Log.i(TAG, "City: " + nearestFireStation.tags.addressCity);
                    Log.i(TAG, "Street: " + nearestFireStation.tags.addressStreet);
                    Log.i(TAG, "Operator: " + nearestFireStation.tags.operator);
                } else {
                    Log.e(TAG, "Nothing have been found!!");
                }
            }
        });
    }

    public boolean isIsCurrentLocation() {
        return mIsCurrentLocation;
    }

    public void setIsCurrentLocation(boolean mIsCurrentLocation) {
        this.mIsCurrentLocation = mIsCurrentLocation;
    }
}
