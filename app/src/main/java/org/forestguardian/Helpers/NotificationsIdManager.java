package org.forestguardian.Helpers;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.forestguardian.DataAccess.Local.DeviceInfo;
import org.forestguardian.DataAccess.Local.SessionData;
import org.forestguardian.DataAccess.Local.User;
import org.forestguardian.DataAccess.WebServer.ForestGuardianService;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import retrofit2.adapter.rxjava2.Result;

/**
 * Created by luisalonsomurillorojas on 25/7/17.
 */

public class NotificationsIdManager extends FirebaseInstanceIdService {

    private static final String TAG = NotificationsIdManager.class.getSimpleName();

    //Token management

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        AuthenticationController.shared().updateFirebaseRegistrationToken(refreshedToken);
    }
}
