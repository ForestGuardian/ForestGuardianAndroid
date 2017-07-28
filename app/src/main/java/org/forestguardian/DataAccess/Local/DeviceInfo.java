package org.forestguardian.DataAccess.Local;

import com.google.gson.annotations.Expose;

import io.realm.RealmObject;

/**
 * Created by emma on 27/07/17.
 */

public class DeviceInfo extends RealmObject{

    @Expose
    private String firebase_registration_token;

    public String getFirebase_registration_token() {
        return firebase_registration_token;
    }

    public void setFirebase_registration_token(final String pFirebase_registration_token) {
        firebase_registration_token = pFirebase_registration_token;
    }
}
