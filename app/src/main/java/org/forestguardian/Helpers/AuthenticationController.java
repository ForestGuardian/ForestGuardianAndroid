package org.forestguardian.Helpers;

import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;

import org.forestguardian.DataAccess.Local.DeviceInfo;
import org.forestguardian.DataAccess.Local.SessionData;
import org.forestguardian.DataAccess.Local.User;
import org.forestguardian.DataAccess.WebServer.ForestGuardianService;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.adapter.rxjava2.Result;

/**
 * Created by emma on 25/06/17.
 */

public class AuthenticationController {

    private static final String TAG = "AuthenticationContr";
    private static AuthenticationController mInstance;
    private static Context mContext;

    public static AuthenticationController shared(){
        return mInstance;
    }

    public static void initialize( Context pContext ){
        mInstance = new AuthenticationController();
        mContext = pContext;
    }

    /**
     * Load current database user information if any.
     */
    public void loadCurrentUser(){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<User> results = realm.where(User.class).findAll();
        if ( results.size() > 0 ){
            setCurrentUser(results.last());
        }
    }

    public void logout(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(User.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    public boolean signedIn(){
        return getCurrentUser() != null;
    }

    public boolean forcedLogoutOldVersionData(){
        User user = getCurrentUser();
        if (user != null && user.getId() == 0){
            logout();
            return true;
        }
        return false;
    }

    public User getCurrentUser(Realm realm) {
        return realm.where(User.class).findFirst();
    }

    public User getCurrentUser(){
        Realm realm = Realm.getDefaultInstance();
        return getCurrentUser(realm);
    }


    public void setCurrentUser(final User pCurrentUser) {
        ForestGuardianService.global().addAuthenticationHeaders( mContext, pCurrentUser.getAuth().getUid() );
        Log.e("headers",pCurrentUser.getAuth().toString());

        Realm realm = Realm.getDefaultInstance();

        // Persist in realm. TODO: We retrieve by calling the last object. Improve this.
        realm.beginTransaction();
        realm.copyToRealm(pCurrentUser);
        realm.commitTransaction();

        Log.e("Authenticated User:", pCurrentUser.getEmail());

        // Fabric
        Crashlytics.setUserIdentifier(pCurrentUser.getEmail());
        Crashlytics.setUserEmail(pCurrentUser.getEmail());
        Crashlytics.setUserName(pCurrentUser.getName());

        //Test the token generation
        updateFirebaseRegistrationToken(FirebaseInstanceId.getInstance().getToken());
        updateFirebaseRegistrationTokenForUser();
    }

    public void updateCurrentUser(final User pCurrentUser){
        logout();
        setCurrentUser(pCurrentUser);
    }

    public void updateFirebaseRegistrationToken(String token){

        invalidateFirebaseRegistrationToken();

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        DeviceInfo info = realm.createObject(DeviceInfo.class);
        info.setFirebaseRegistrationToken(token);
        realm.commitTransaction();

        if ( signedIn() ){
            AuthenticationController.shared().updateFirebaseRegistrationTokenForUser();
        }
    }

    public boolean isFirebaseRegistrationTokenReady(){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<DeviceInfo> results = realm.where(DeviceInfo.class).findAll();
        if ( results.size() != 1 || results.first().getFirebaseRegistrationToken() == null ){
            Log.e("Firebase","DeviceInfo is ambiguous or not ready yet.");
            return false;
        }
        return true;
    }

    public void updateFirebaseRegistrationTokenForUser(){

        RealmResults<DeviceInfo> results = Realm.getDefaultInstance().where(DeviceInfo.class).findAll();
        String token = results.first().getFirebaseRegistrationToken();

        User user = new User();
        user.setEmail(getCurrentUser().getEmail());
        user.setFirebaseRegistrationToken(token);

        Observable<Result<SessionData>> service = ForestGuardianService.global().service().updateAccount(user);
        service.subscribeOn( Schedulers.newThread() )
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe( pResult -> {
                    Log.d("FirebaseToken", pResult.toString());

                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    getCurrentUser().setFirebaseRegistrationToken(token);
                    realm.commitTransaction();

                } );
    }

    private void invalidateFirebaseRegistrationToken(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        if ( signedIn() ) {
            User user = getCurrentUser();
            user.setFirebaseRegistrationToken(null);
        }
        realm.where(DeviceInfo.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }


    public void refreshUserData(){
        // Send SignIn Request
        Observable<User> sessionService = ForestGuardianService.global().service().showUser(getCurrentUser().getId());
        sessionService.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( pUser -> {
                    Realm realm = Realm.getDefaultInstance();
                    User user = getCurrentUser(realm);
                    realm.beginTransaction();
                    user.setAvatarUrl( pUser.getAvatarUrl() );
                    realm.commitTransaction();
                    user.updateAvatar();
                });
    }

}
