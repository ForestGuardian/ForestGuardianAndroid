package org.forestguardian.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import org.forestguardian.Helpers.AuthenticationController;
import org.forestguardian.R;

/**
 * Created by emma on 09/04/17.
 */

public class SplashActivity extends Activity {

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AuthenticationController.shared().loadCurrentUser();

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent;
            if ( AuthenticationController.shared().signedIn() ) {
                AuthenticationController.shared().refreshUserData();
                intent = new Intent(getApplicationContext(), MainActivity.class);
            }else{
                intent = new Intent(getApplicationContext(), SignInActivity.class);
            }
            startActivity(intent);
            finish();
        },2000);
    }
}
