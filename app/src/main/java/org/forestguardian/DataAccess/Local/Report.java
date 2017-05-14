package org.forestguardian.DataAccess.Local;

import com.google.gson.annotations.Expose;

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

    public Double getGeo_latitude() {
        return geo_latitude;
    }

    public void setGeo_latitude(final Double pGeo_latitude) {
        geo_latitude = pGeo_latitude;
    }

    public Double getGeo_longitude() {
        return geo_longitude;
    }

    public void setGeo_longitude(final Double pGeo_longitude) {
        geo_longitude = pGeo_longitude;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(final String pPicture) {
        picture = pPicture;
    }
}
