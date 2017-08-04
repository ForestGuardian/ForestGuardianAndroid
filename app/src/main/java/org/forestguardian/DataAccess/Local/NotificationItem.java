package org.forestguardian.DataAccess.Local;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by emma on 7/30/17.
 */

public class NotificationItem extends RealmObject{

    private String title;

    private String description;

    private long report_id;

    // url of image
    private String avatar;

    private byte[] avatar_data;

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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(final String pAvatar) {
        avatar = pAvatar;
    }

    public byte[] getAvatarData() {
        return avatar_data;
    }

    public void setAvatarData(final byte[] pAvatarData) {
        avatar_data = pAvatarData;
    }

    public long getReportId() {
        return report_id;
    }

    public void setReportId(final long pReportId) {
        report_id = pReportId;
    }

    public void downloadAvatarData(){
        final long itemReportId = report_id;
        Observable.create(e -> {
            if (avatar == null){
                if (!e.isDisposed()){
                    e.onError(null);
                    e.onComplete();
                }
                return;
            }
            try {
                Bitmap picture = BitmapFactory.decodeStream(new URL(avatar).openConnection().getInputStream());
                if (!e.isDisposed()){
                    e.onNext(picture);
                    e.onComplete();
                }
            }catch(MalformedURLException error){
                error.printStackTrace();
                if (!e.isDisposed()){
                    e.onError(error);
                    e.onComplete();
                }
                return;
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( bitmap -> {
                    if (bitmap != null) {

                        //Decode
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        ((Bitmap)bitmap).compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] data = stream.toByteArray();
                        ((Bitmap)bitmap).recycle();
                        stream.close();

                        // Persist
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        NotificationItem obj = realm.where(NotificationItem.class).equalTo("report_id", itemReportId ).findFirst();
                        obj.setAvatarData(data);
                        realm.commitTransaction();
                    }
                });
    }
}
