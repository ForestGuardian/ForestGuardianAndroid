package org.forestguardian.DataAccess.Local;

import com.google.gson.annotations.Expose;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import io.realm.RealmObject;

/**
 * Created by emma on 17/03/17.
 */

public class User extends RealmObject {

    /*Fields */
    private String username;
    @Expose
    private String email;
    @Expose
    private String password;

    private String password_confirmation;

    private AuthData auth;

    @Expose
    private String name;

    @Expose
    private String avatar;

    /*Setters and Getters*/

    public String getEmail() {
        return email;
    }

    public void setEmail(final String pEmail) {
        email = pEmail;
    }

    public AuthData getAuth() {
        return auth;
    }

    public void setAuth(final AuthData pAuth) {
        auth = pAuth;
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

    public String getName() {
        return name;
    }

    public void setName(final String pName) {
        name = pName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String pUsername) {
        username = pUsername;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(final String pAvatar) {
        avatar = pAvatar;
    }
}
