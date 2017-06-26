package org.forestguardian.Helpers;

import android.content.Context;
import android.util.Log;

import org.forestguardian.DataAccess.Local.User;
import org.forestguardian.DataAccess.WebServer.ForestGuardianService;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by emma on 25/06/17.
 */

public class AuthenticationController {

    private static AuthenticationController mInstance;
    private static Context mContext;

    public static AuthenticationController shared(){
        return mInstance;
    }

    public static void initialize( Context pContext ){
        mInstance = new AuthenticationController();
        mContext = pContext;
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
        ForestGuardianService.global().addAuthenticationHeaders( mContext, pCurrentUser.getAuth().getUid() );
        Log.e("headers",pCurrentUser.getAuth().toString());

        // Persist in realm. TODO: We retrieve by calling the last object. Improve this.
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(pCurrentUser);
        realm.commitTransaction();

        Log.e("Authenticated User:", pCurrentUser.getEmail());
    }

}
