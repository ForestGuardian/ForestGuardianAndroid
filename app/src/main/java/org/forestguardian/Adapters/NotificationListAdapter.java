package org.forestguardian.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.forestguardian.DataAccess.Local.NotificationItem;
import org.forestguardian.View.CustomViews.NotificationsItemView;

import java.util.List;

/**
 * Created by emma on 7/30/17.
 */

public class NotificationListAdapter extends ArrayAdapter<NotificationItem> {

    private Context mContext;

    public NotificationListAdapter(Context context, List<NotificationItem> reports) {
        super(context, 0, reports);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Data
        NotificationItem notificationItem = getItem(position);

        // Recycling
        if (convertView == null) {
            convertView = new NotificationsItemView(mContext,notificationItem);
        }

        return convertView;
    }

}
