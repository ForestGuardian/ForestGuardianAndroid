package org.forestguardian.DataAccess.Local;

import com.google.gson.annotations.Expose;

import io.realm.RealmObject;

/**
 * Created by emma on 17/03/17.
 */

public class User extends RealmObject {

    @Expose
    private String email;
    @Expose
    private String password;
    @Expose
    private String password_confirmation;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(final String pPassword) {
        password = pPassword;
    }

    public String getPasswordConfirmation() {
        return password_confirmation;
    }

    public void setPasswordConfirmation(final String pPassword_confirmation) {
        password_confirmation = pPassword_confirmation;
    }
}
