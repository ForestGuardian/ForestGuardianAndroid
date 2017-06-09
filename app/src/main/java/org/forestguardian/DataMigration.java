package org.forestguardian;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by emma on 09/06/17.
 */

public class DataMigration implements RealmMigration {
    @Override
    public void migrate(final DynamicRealm realm, final long oldVersion, final long newVersion) {
        // DynamicRealm exposes an editable schema
//        RealmSchema schema = realm.getSchema();

        // Check https://realm.io/docs/java/latest/#migrations

    }
}
