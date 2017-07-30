package org.forestguardian.DataAccess.Local;

import io.realm.RealmObject;

/**
 * Created by emma on 7/30/17.
 */

public class NotificationItem extends RealmObject{

    private String title;

    private String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(final String pTitle) {
        title = pTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String pDescription) {
        description = pDescription;
    }
}
