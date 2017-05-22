package org.forestguardian.View;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import org.forestguardian.R;

/**
 * Created by emma on 21/05/17.
 */

public class ProfileActivity extends Activity {


    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile_layout);
    }
}
