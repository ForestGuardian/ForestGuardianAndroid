package org.forestguardian.DataAccess.Local;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.google.gson.annotations.Expose;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by emma on 17/03/17.
 */

public class User extends RealmObject {

    @PrimaryKey
    private long id;

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

    private String avatar_url;

    @Expose
    private String avatar;

    @Expose
    private String firebase_registration_token;

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

    public String getAvatarUrl() {
        return avatar_url;
    }

    public void setAvatarUrl(final String pAvatarUrl) {
        avatar_url = pAvatarUrl;
    }

    public String getFirebaseRegistrationToken() {
        return firebase_registration_token;
    }

    public void setFirebaseRegistrationToken(final String pFirebase_registration_token) {
        firebase_registration_token = pFirebase_registration_token;
    }

    public String getAvatar() {
        return avatar;
    }

    @SuppressWarnings("WeakerAccess")
    public byte[] getBase64DecodedAvatar(){
        if (avatar == null){
            return null;
        }

        String avatarData = avatar.replace("data:image/png;base64,","");
        return Base64.decode( avatarData, Base64.DEFAULT );
    }

    public Bitmap getUncompressedAvatar(){
        byte[] decodedString = getBase64DecodedAvatar();
        if (decodedString == null){
            return null;
        }
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public void setAvatar(final String pAvatar) {
        avatar = pAvatar;
    }

    public void updateAvatar(){
        final long id = this.id;
        Observable.create(e -> {
            String avatar_url = Realm.getDefaultInstance().where(User.class).equalTo("id", id ).findFirst().getAvatarUrl();
            if (avatar_url == null){
                if (!e.isDisposed()){
                    e.onError(null);
                    e.onComplete();
                }
                return;
            }
            try {
                Bitmap picture = BitmapFactory.decodeStream(new URL(avatar_url).openConnection().getInputStream());
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

                    // Decode PNG
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    ((Bitmap)bitmap).compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] data = stream.toByteArray();
                    ((Bitmap)bitmap).recycle();
                    stream.close();

                    String base64Encoded = "data:image/png;base64," + Base64.encodeToString( data, Base64.DEFAULT );

                    // Persist
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    User obj = realm.where(User.class).equalTo("id", id ).findFirst();
                    obj.setAvatar(base64Encoded);
                    obj.setUsername(UUID.randomUUID().toString());
                    realm.commitTransaction();
                }
            }, e -> {});
    }

    public void setId(final long pId) {
        id = pId;
    }

    public long getId() {
        return id;
    }
}
