package org.forestguardian;

import android.app.Application;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import org.forestguardian.DataAccess.Local.User;
import org.forestguardian.DataAccess.WebServer.ForestGuardianService;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by emma on 09/04/17.
 */

public class ForestGuardianApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(1) // Must be bumped when the schema changes
                .migration(new DataMigration()) // Migration to run instead of throwing an exception
                .build();
        Realm.setDefaultConfiguration(config);
    }

    /**
     * Load current database user information if any.
     */
    public void loadCurrentUser(){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<User> results = realm.where(User.class).findAll();
        if ( results.size() > 0 ){
            setCurrentUser(results.last());
        }
    }

    public void logout(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(User.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    public boolean signedIn(){
        return getCurrentUser() != null;
    }

    public User getCurrentUser() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<User> results = realm.where(User.class).findAll();
        if ( results.size() > 0 ){
            return results.first();
        }
        return null;
    }

    public void setCurrentUser(final User pCurrentUser) {
        ForestGuardianService.global().addAuthenticationHeaders( this, pCurrentUser.getAuth().getUid() );
        Log.e("headers",pCurrentUser.getAuth().toString());

        // Persist in realm. TODO: We retrieve by calling the last object. Improve this.
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(pCurrentUser);
        realm.commitTransaction();

        Log.e("Authenticated User:", pCurrentUser.getEmail());
    }
}
