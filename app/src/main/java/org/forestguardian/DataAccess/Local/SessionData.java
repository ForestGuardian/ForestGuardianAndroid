package org.forestguardian.DataAccess.Local;

import com.google.gson.annotations.SerializedName;

/**
 * Created by emma on 26/03/17.
 */

public class SessionData {

    @SerializedName("data")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(final User pUser) {
        user = pUser;
    }
}
