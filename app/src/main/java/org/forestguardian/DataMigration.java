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
        if (oldVersion == 1){

            schema.get("User").addField("firebase_registration_token", String.class);
            schema.create("DeviceInfo").addField("firebase_registration_token",String.class);

            oldVersion++;
        }

        if (oldVersion == 2){

            schema.get("Report").addField("location_name", String.class);
            schema.create("NotificationItem")
                    .addField("title", String.class)
                    .addField("description", String.class);

            oldVersion++;
        }

        if ( oldVersion < newVersion ){
            throw new IllegalStateException(String.format(Locale.US,"Migration missing from v%d to v&d", oldVersion, newVersion));
        }

    }
}
