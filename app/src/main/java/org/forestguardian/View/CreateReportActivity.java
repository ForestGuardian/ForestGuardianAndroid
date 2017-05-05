package org.forestguardian.View;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.forestguardian.R;
import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by emma on 30/04/17.
 */

public class CreateReportActivity extends AppCompatActivity {

    @BindView(R.id.latitude_label) TextView mLatitudeLabel;
    @BindView(R.id.longitude_label) TextView mLongitudeLabel;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_report_layout);
        ButterKnife.bind(this);

        Double latitude = getIntent().getDoubleExtra("latitude",0.0);
        Double longitude = getIntent().getDoubleExtra("longitude",0.0);

        mLatitudeLabel.setText(String.valueOf(latitude));
        mLongitudeLabel.setText(String.valueOf(longitude));
    }
}
