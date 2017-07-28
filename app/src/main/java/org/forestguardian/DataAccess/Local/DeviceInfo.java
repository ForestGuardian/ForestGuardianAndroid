package org.forestguardian.DataAccess.Local;

import com.google.gson.annotations.Expose;

import io.realm.RealmObject;

/**
 * Created by emma on 27/07/17.
 */

public class DeviceInfo extends RealmObject{

    @Expose
    private String firebase_registration_token;

    public String getFirebaseRegistrationToken() {
        return firebase_registration_token;
    }

    public void setFirebaseRegistrationToken(final String pFirebase_registration_token) {
        firebase_registration_token = pFirebase_registration_token;
    }
}
