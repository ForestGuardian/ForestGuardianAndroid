package org.forestguardian.DataAccess.Local;

import android.os.Environment;
import android.util.Log;

import com.google.gson.annotations.Expose;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.realm.RealmObject;

/**
 * Created by emma on 14/05/17.
 */

public class Report extends RealmObject{

    private Integer id;

    @Expose
    private String title;
    @Expose
    private String description;
    @Expose
    private String comments;
    @Expose
    private Double geo_latitude;
    @Expose
    private Double geo_longitude;
    @Expose
    private String picture;

    private boolean isPictureDownloaded;

    /** Getters and Setters **/

    public Integer getId() {
        return id;
    }

    public void setId(final Integer pId) {
        id = pId;
    }

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

    public String getComments() {
        return comments;
    }

    public void setComments(final String pComments) {
        comments = pComments;
    }

    public Double getGeoLatitude() {
        return geo_latitude;
    }

    public void setGeoLatitude(final Double pGeo_latitude) {
        geo_latitude = pGeo_latitude;
    }

    public Double getGeoLongitude() {
        return geo_longitude;
    }

    public void setGeoLongitude(final Double pGeo_longitude) {
        geo_longitude = pGeo_longitude;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(final String pPicture) {
        picture = pPicture;
    }

//    public Observable<String> getPictureData() {
//        return Observable.create(e -> {
//
//            String pictureData;
//            if ( isPictureDownloaded ) {
//                pictureData = getPicture();
//            } else{
//                pictureData = download(getPicture());
//            }
//
//            if (!e.isDisposed()) {
//                e.onNext( pictureData );
//                e.onComplete();
//            }
//        });
//    }
//
//    private String download(String stringUrl){
//        String pictureData;
//        int count;
//        try {
//            URL url = new URL(stringUrl);
//            URLConnection connection = url.openConnection();
//            connection.connect();
//
//            // download the file
//            InputStream input = new BufferedInputStream(url.openStream(),
//                    8192);
//
//            // Output stream
//            ByteArrayOutputStream output = new ByteArrayOutputStream();
//
//            byte data[] = new byte[1024];
//
//            while ((count = input.read(data)) != -1) {
//                // writing data to file
//                output.write(data, 0, count);
//            }
//
//            pictureData = new String(output.toByteArray());
//
//            // closing streams
//            output.close();
//            input.close();
//
//        } catch (Exception e) {
//            Log.e("Error: ", e.getMessage());
//            return null;
//        }
//        return pictureData;
//    }
}
