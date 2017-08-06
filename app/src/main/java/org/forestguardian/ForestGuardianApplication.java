package org.forestguardian;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import org.forestguardian.Helpers.AuthenticationController;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

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
                .schemaVersion(10) // Must be bumped when the schema changes
//                .migration(new DataMigration()) // Migration to run instead of throwing an exception
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

        AuthenticationController.initialize(this);
    }
}
