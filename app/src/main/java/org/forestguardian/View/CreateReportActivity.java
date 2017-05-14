package org.forestguardian.View;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import org.forestguardian.R;
import org.w3c.dom.Text;

import java.io.File;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by emma on 30/04/17.
 */

public class CreateReportActivity extends AppCompatActivity {

    private static final int CAMERA_IMAGE_REQUEST = 101;
    private static final int GALLERY_IMAGE_REQUEST = 102;

    @BindView(R.id.latitude_label) TextView mLatitudeLabel;
    @BindView(R.id.longitude_label) TextView mLongitudeLabel;
    @BindView(R.id.take_picture_btn) ImageButton mTakePictureBtn;

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

    @OnClick(R.id.take_picture_btn)
    public void captureImage(){

        // Creating image here
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, CAMERA_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Log.d("CameraDemo", "Picture saved");

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mTakePictureBtn.setImageBitmap(imageBitmap);
        }
    }

}
