package org.forestguardian.DataAccess.Local;

import io.realm.RealmObject;

/**
 * Created by emma on 17/03/17.
 */

public class Session extends RealmObject {

    private String email;
    private String token;

    public String getEmail() {
        return email;
    }

    public void setEmail(final String pEmail) {
        email = pEmail;
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String pToken) {
        token = pToken;
    }
}
