package org.forestguardian;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by emma on 09/04/17.
 */

public class ForestGuardianApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
    }
}
