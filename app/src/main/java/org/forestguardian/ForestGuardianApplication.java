package org.forestguardian;

import android.app.Application;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import org.forestguardian.DataAccess.Local.User;
import org.forestguardian.DataAccess.WebServer.ForestGuardianService;
import org.forestguardian.Helpers.AuthenticationController;

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
//                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

        AuthenticationController.initialize(this);
    }
}
