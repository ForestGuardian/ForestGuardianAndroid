package org.forestguardian.DataAccess.Local;

import io.realm.RealmObject;

/**
 * Created by emma on 06/05/17.
 */

public class AuthData extends RealmObject {

    private String uid;
    private String client;
    private String tokenType;
    private String accessToken;
    private String expiry;

    public String getUid() {
        return uid;
    }

    public void setUid(final String pUid) {
        uid = pUid;
    }

    public String getClient() {
        return client;
    }

    public void setClient(final String pClient) {
        client = pClient;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(final String pTokenType) {
        tokenType = pTokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(final String pAccessToken) {
        accessToken = pAccessToken;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(final String pExpiry) {
        expiry = pExpiry;
    }
}
