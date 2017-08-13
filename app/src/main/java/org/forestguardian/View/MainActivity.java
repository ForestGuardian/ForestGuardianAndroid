package org.forestguardian.View;

import android.Manifest;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.forestguardian.DataAccess.Local.User;
import org.forestguardian.DataAccess.Location.LocationController;
import org.forestguardian.Helpers.AuthenticationController;
import org.forestguardian.Helpers.GeoHelper;
import org.forestguardian.R;
import org.forestguardian.View.Fragments.AboutFragment;
import org.forestguardian.View.Fragments.MapFragment;
import org.forestguardian.View.Fragments.NotificationsFragment;
import org.forestguardian.View.Fragments.ProfileFragment;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.ObjectChangeSet;
import io.realm.RealmObjectChangeListener;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, RealmObjectChangeListener<User>, LocationController.SimpleLocationListener {

    public final static int REPORT_CREATION_REQUEST = 2;

    private static String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.main_layout)             FrameLayout       mMainLayout;
    @BindView(R.id.nav_view)                NavigationView    mNavView;
    @BindView(R.id.drawer_layout)           DrawerLayout      mDrawerLayout;
    @BindView(R.id.toolbar)                 Toolbar           mToolbar;
    private NavigationHolder navHolder;

    private ProfileFragment mProfileFragment;
    private MapFragment mMapFragment;
    private NotificationsFragment mNotificationsFragment;
    private AboutFragment mAboutFragment;

    private RealmObjectChangeListener mRealmListener;
    private User mCurrentUser;
    private LocationController mLocationController;

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
        navHolder.header.avatar.setImageBitmap( BitmapFactory.decodeResource(getResources(), R.drawable.ic_perfil2) );
        navHolder.header.avatar.setVisibility(View.VISIBLE);

        mCurrentUser = AuthenticationController.shared().getCurrentUser();
        mRealmListener = (RealmObjectChangeListener<User>) (pUser, changeSet) -> {
            if ( changeSet.isFieldChanged("avatar") ){
                navHolder.header.avatar.setImageBitmap(pUser.getUncompressedAvatar());
            }
        };
        mCurrentUser.addChangeListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavView.setNavigationItemSelectedListener(this);

        //Load Fragment
        loadProfileAvatar();
        mCurrentUser.updateAvatar();

        mMapFragment = new MapFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace( R.id.main_layout, mMapFragment);
        transaction.commit();

        MainActivityPermissionsDispatcher.initLocationWithCheck(this);
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void initLocation() {
        mLocationController = new LocationController(this);
        mLocationController.addListener(this);
        mLocationController.addListener(mMapFragment);
        mLocationController.notifyGPS();
    }

    @OnShowRationale({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void showRationaleForCamera(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage("Necesitamos averiguar tu localizacion para poder crear un reporte.")
                .setPositiveButton("Aceptar", (dialog, button) -> request.proceed())
                .setNegativeButton("Cancelar", (dialog, button) -> request.cancel())
                .show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Check for search queries
        checkSearchIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.i(TAG, "New intent in the MainActivity");
        setIntent(intent);
        checkSearchIntent(intent);
    }

    private void checkSearchIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.i(TAG, "QUERY: " + query);
            processSearchQuery(query);
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            String locationData = intent.getData().getLastPathSegment();
            Location locationPoint = GeoHelper.convertStringToLocation(locationData);
            if (locationPoint != null && mMapFragment != null) {
                mMapFragment.setMapLocation(locationPoint);
                mMapFragment.centerOnLocation();
            }
        }
    }

    private void processSearchQuery(String query) {
        Log.i(TAG, "Searching for: " + query);
        new Thread(() -> {
            Location searchPoint = null;
            try {
                searchPoint = GeoHelper.getPointFromAddressName(this, query);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (searchPoint != null && mMapFragment != null) {
                mMapFragment.setMapLocation(searchPoint);
                mMapFragment.centerOnLocation();
            }
        }).start();
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

            if (mNotificationsFragment == null){
                mNotificationsFragment = new NotificationsFragment();
            }
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace( R.id.main_layout, mNotificationsFragment );
            transaction.commit();
            mToolbar.setTitle("NOTIFICACIONES");

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map){

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace( R.id.main_layout, mMapFragment );
            transaction.commit();
            mLocationController.notifyGPS();

            mToolbar.setTitle("REPORTAR");


        } else if (id == R.id.nav_reports) {

            if (mProfileFragment == null){
                mProfileFragment = new ProfileFragment();
            }
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace( R.id.main_layout, mProfileFragment );
            transaction.commit();

            mToolbar.setTitle("PERFIL");


        } else if (id == R.id.nav_notifications) {

            if (mNotificationsFragment == null){
                mNotificationsFragment = new NotificationsFragment();
            }
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace( R.id.main_layout, mNotificationsFragment );
            transaction.commit();

            mToolbar.setTitle("NOTIFICACIONES");


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

            if (mAboutFragment == null){
                mAboutFragment = new AboutFragment();
            }
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace( R.id.main_layout, mAboutFragment );
            transaction.commit();

            mToolbar.setTitle("¿QUIÉNES SÓMOS?");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void loadProfileAvatar(){
        navHolder.header.progress.setVisibility(View.VISIBLE);
        navHolder.header.avatar.setImageBitmap( AuthenticationController.shared().getCurrentUser().getUncompressedAvatar() );
        navHolder.header.progress.setVisibility(View.GONE);
    }

    @Override
    public void onChange(final User pCurrentUser, final ObjectChangeSet changeSet) {
        if ( changeSet.isFieldChanged("avatar") ){
            navHolder.header.avatar.setImageBitmap(pCurrentUser.getUncompressedAvatar());
        }
    }

    @Override
    public void onGPSChanged(final Location pLocation, final String pLocationName) {

    }

    @Override
    public void onUnavailable() {

    }

    // endregion

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

    @Override
    public void onBackPressed() {
        // Delegates onBackPressed to map fragment.
        if ( mMapFragment.isVisible() && !mMapFragment.onBackPressed() ){
            super.onBackPressed();
        }
    }



}
