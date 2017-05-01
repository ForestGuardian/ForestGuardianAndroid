package org.forestguardian.View;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.forestguardian.R;

/**
 * Created by emma on 30/04/17.
 */

public class CreateReportActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState, @Nullable final PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        setContentView(R.layout.new_report_layout);
    }
}
