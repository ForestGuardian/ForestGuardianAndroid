package org.forestguardian;

import android.app.Application;
import android.util.Log;

import org.forestguardian.DataAccess.Local.User;
import org.forestguardian.DataAccess.WebServer.ForestGuardianService;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by emma on 09/04/17.
 */

public class ForestGuardianApplication extends Application {

    public User mCurrentUser;

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
    }

    /**
     * Load current database user information if any.
     */
    public void loadCurrentUser(){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<User> results = realm.where(User.class).findAll();
        if ( results.size() > 0 ){
            mCurrentUser = results.last();
        }
    }

    public void logout(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(User.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    public boolean signedIn(){
        return mCurrentUser != null;
    }

    public User getCurrentUser() {
        return mCurrentUser;
    }

    public void setCurrentUser(final User pCurrentUser) {
        mCurrentUser = pCurrentUser;
        ForestGuardianService.global().addAuthenticationHeaders( this, pCurrentUser.getAuth() );

        // Persist in realm. TODO: We retrieve by calling the last object. Improve this.
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(pCurrentUser);
        realm.commitTransaction();

        Log.e("Authenticated User:", pCurrentUser.getEmail());
    }
}
