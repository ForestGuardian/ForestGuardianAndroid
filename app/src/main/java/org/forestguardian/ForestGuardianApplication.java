package org.forestguardian;

import android.app.Application;
import android.util.Log;

import org.forestguardian.DataAccess.Local.User;
import org.forestguardian.DataAccess.WebServer.ForestGuardianService;

import io.realm.Realm;

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

    public User getCurrentUser() {
        return mCurrentUser;
    }

    public void setCurrentUser(final User pCurrentUser) {
        mCurrentUser = pCurrentUser;
        ForestGuardianService.global().addAuthenticationHeaders(pCurrentUser.getEmail(), pCurrentUser.getToken());

        // Persist in realm. TODO: We retrieve by calling the last object. Improve this.
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(pCurrentUser);
        realm.commitTransaction();

        Log.e("Authenticated User:", pCurrentUser.getEmail());
    }
}
