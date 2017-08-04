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

        if ( oldVersion == 3 ) {

            schema.get("NotificationItem")
                    .addField("avatar_data", byte[].class)
                    .addField("report_id", long.class);

            oldVersion++;
        }

        if ( oldVersion == 4 ) {

            schema.get("User").renameField("avatar", "avatar_url");

            oldVersion++;
        }

        if ( oldVersion == 5 ) {

            schema.get("User").addField("avatar", String.class)
                    .addField("id", long.class);

            oldVersion++;
        }

        if ( oldVersion < newVersion ){
            throw new IllegalStateException(String.format(Locale.US,"Migration missing from v%d to v&d", oldVersion, newVersion));
        }

    }
}
