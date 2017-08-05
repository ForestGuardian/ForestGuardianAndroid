package org.forestguardian;

import java.util.Locale;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by emma on 09/06/17.
 */

public class DataMigration implements RealmMigration {
    @Override
    public void migrate(final DynamicRealm realm, long oldVersion, final long newVersion) {
        // DynamicRealm exposes an editable schema
        RealmSchema schema = realm.getSchema();

        // Check https://realm.io/docs/java/latest/#migrations
        // Delete all data before this version
        if (oldVersion < 10){
            oldVersion = 10;
        }

        if ( oldVersion < newVersion ){
            throw new IllegalStateException(String.format(Locale.US,"Migration missing from v%d to v&d", oldVersion, newVersion));
        }

    }
}
