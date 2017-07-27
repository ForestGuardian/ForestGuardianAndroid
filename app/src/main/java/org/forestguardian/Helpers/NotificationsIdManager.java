package org.forestguardian.Helpers;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.forestguardian.DataAccess.Local.SessionData;
import org.forestguardian.DataAccess.Local.User;
import org.forestguardian.DataAccess.WebServer.ForestGuardianService;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
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

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        User user = AuthenticationController.shared().getCurrentUser();
        user.setFirebase_registration_token(token);

        Observable<Result<SessionData>> service = ForestGuardianService.global().service().updateAccount(user);
        service.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( pResult -> Log.d("FirebaseToken", pResult.toString()));

    }
}
