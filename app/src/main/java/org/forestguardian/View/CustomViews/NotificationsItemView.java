package org.forestguardian.View.CustomViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.forestguardian.DataAccess.Local.NotificationItem;
import org.forestguardian.DataAccess.Local.Report;
import org.forestguardian.R;
import org.forestguardian.View.Fragments.ProfileAvatarFragment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by emma on 7/30/17.
 */

public class NotificationsItemView extends LinearLayout {

    @BindView(R.id.notification_title) TextView titleView;
    @BindView(R.id.notification_body) TextView descriptionView;
    @BindView(R.id.notification_picture) ImageView pictureView;

    private NotificationItem mNotificationItem;

    public NotificationsItemView(final Context context, NotificationItem pNotification) {
        super(context);
        inflate(context, R.layout.list_notification_item, this);
        ButterKnife.bind(this);

        mNotificationItem = pNotification;
        titleView.setText(mNotificationItem.getTitle());
        descriptionView.setText(mNotificationItem.getDescription());

        if ( mNotificationItem.getAvatarData() != null ){
            pictureView.setImageBitmap( decodeBitmap( mNotificationItem.getAvatarData() ) );
        }else{
            Log.d("NotificationsItemView", "No avatar data downloaded." );
        }
    }

    private Bitmap decodeBitmap( byte[] data ){
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

}
