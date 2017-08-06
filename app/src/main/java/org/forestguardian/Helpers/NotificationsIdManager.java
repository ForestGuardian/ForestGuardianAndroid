package org.forestguardian.Helpers;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

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
