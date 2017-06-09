package org.forestguardian.View;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.forestguardian.DataAccess.Local.Report;
import org.forestguardian.DataAccess.WebServer.ForestGuardianService;
import org.forestguardian.R;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by emma on 30/04/17.
 */

@RuntimePermissions
public class CreateReportActivity extends AppCompatActivity {

    private static final int CAMERA_IMAGE_REQUEST = 101;
    private static final int GALLERY_IMAGE_REQUEST = 102;

    public final static int SUCCESS_RESULT = 0;

    @BindView(R.id.latitude_label) TextView mLatitudeLabel;
    @BindView(R.id.longitude_label) TextView mLongitudeLabel;
    @BindView(R.id.take_picture_btn) ImageButton mTakePictureBtn;
    @BindView(R.id.send_report_btn) Button mSendReportBtn;
    @BindView(R.id.new_report_title) TextView mTitle;
    @BindView(R.id.new_report_description) TextView mDescription;
    @BindView(R.id.new_report_comments) TextView mComments;

    private Bitmap mReportBitmap;
    private Double mLatitude;
    private Double mLongitude;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_report_layout);
        ButterKnife.bind(this);

        mLatitude = getIntent().getDoubleExtra("latitude",0.0);
        mLongitude = getIntent().getDoubleExtra("longitude",0.0);

        mLatitudeLabel.setText(String.valueOf(mLatitude));
        mLongitudeLabel.setText(String.valueOf(mLongitude));
    }

    @OnClick(R.id.take_picture_btn)
    public void captureImageAction(){

        CreateReportActivityPermissionsDispatcher.captureImageWithCheck(this);
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    public void captureImage(){
        // Creating image here
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, CAMERA_IMAGE_REQUEST);
    }


    @OnShowRationale(Manifest.permission.CAMERA)
    void showRationaleForCamera(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage("Necesitamos acceso a la camara.")
                .setPositiveButton("Aceptar", (dialog, button) -> request.proceed())
                .setNegativeButton("Cancelar", (dialog, button) -> request.cancel())
                .show();
    }

    @OnClick(R.id.send_report_btn)
    public void sendReportButton(){

        // Send report request.
        Report report = new Report();
        report.setTitle( mTitle.getText().toString() );
        report.setComments( mDescription.getText().toString() );
        report.setDescription( mComments.getText().toString() );
        report.setGeoLatitude( mLatitude );
        report.setGeoLongitude( mLongitude );

        // Check for a valid title.
        if ( TextUtils.isEmpty(report.getTitle()) ) {
            mTitle.setError( getString(R.string.empty_report_title_error) );
            mTitle.requestFocus();
            return;
        }

        // Check for a valid description.
        if ( TextUtils.isEmpty(report.getDescription()) ) {
            mDescription.setError( getString(R.string.empty_report_description_error) );
            mDescription.requestFocus();
            return;
        }

        // Check for a valid comment.
        if ( TextUtils.isEmpty(report.getComments()) ) {
            mComments.setError( getString(R.string.empty_report_comments_error) );
            mComments.requestFocus();
            return;
        }

        // use following method to convert bitmap to byte array:
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        mReportBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] reportPictureByteArray = byteArrayOutputStream .toByteArray();

        // encode to Base64
        String encodedPicture = Base64.encodeToString(reportPictureByteArray, Base64.DEFAULT);
        report.setPicture( "data:image/png;base64," + encodedPicture );

        Observable<Report> reportService = ForestGuardianService.global().service().createReport(report);
        reportService.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( pCreatedReport -> {

                    Log.i("Created Report", "id:" + String.valueOf( pCreatedReport.getId() ) );
                    Log.i("Created Report", "title:" + pCreatedReport.getTitle() );
                    Log.i("Created Report", "description:" + pCreatedReport.getDescription() );
                    Log.i("Created Report", "comments:" + pCreatedReport.getComments() );
                    Log.i("Created Report", "latitude:" + String.valueOf(pCreatedReport.getGeoLatitude()) );
                    Log.i("Created Report", "longitude:" + String.valueOf(pCreatedReport.getGeoLongitude()) );

                    // Check that server answered successfully.
                    if ( pCreatedReport.getId() == null ) {

                        String error = "There was a problem uploading the report. Please, try again.";
                        Log.e("ReportUploadError",error);
                        Toast.makeText(this, error,Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Success
                    this.setResult(SUCCESS_RESULT);
                    this.finish();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Log.d("CameraDemo", "Picture saved");

            Bundle extras = data.getExtras();
            mReportBitmap = (Bitmap) extras.get("data");
            mTakePictureBtn.setImageBitmap(mReportBitmap);
        }
    }

}
