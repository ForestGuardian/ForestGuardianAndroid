package org.forestguardian.View;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.forestguardian.Adapters.ReportListAdapter;
import org.forestguardian.DataAccess.Local.AuthData;
import org.forestguardian.DataAccess.Local.Report;
import org.forestguardian.DataAccess.Local.SessionData;
import org.forestguardian.DataAccess.Local.User;
import org.forestguardian.DataAccess.WebServer.ForestGuardianService;
import org.forestguardian.ForestGuardianApplication;
import org.forestguardian.Helpers.AuthenticationController;
import org.forestguardian.Helpers.HeadersHelper;
import org.forestguardian.R;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import okhttp3.Headers;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import retrofit2.adapter.rxjava2.Result;

/**
 * Created by emma on 21/05/17.
 */

@RuntimePermissions
public class ProfileActivity extends Activity {

    private static final int CAMERA_IMAGE_REQUEST = 101;
    private static final int GALLERY_IMAGE_REQUEST = 102;

    @BindView(R.id.report_list) ListView mListView;
    @BindView(R.id.profile_name) TextView mProfileNameView;
    @BindView(R.id.profile_city) TextView mProfileCity;
    @BindView(R.id.profile_created_reports) TextView mProfileCountCreatedReports;
    @BindView(R.id.picture_view) ImageView mProfileAvatar;
    @BindView(R.id.change_profile_picture) ImageView mChangeProfilePicture;
    @BindView(R.id.avatar_progress) ProgressBar mAvatarProgress;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile_layout);
        ButterKnife.bind(this);

        loadReportList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadProfileAvatar();
    }

    private void loadProfileAvatar(){
        mAvatarProgress.setVisibility(View.VISIBLE);
        User user = AuthenticationController.shared().getCurrentUser();
        mProfileNameView.setText(user.getName());

        Observable.create(e -> {
            User currentUser = AuthenticationController.shared().getCurrentUser();
            String avatar = currentUser.getAvatar();
            if (avatar == null){
                if (!e.isDisposed()){
                    e.onError(null);
                    e.onComplete();
                }
                return;
            }
            try {
                Bitmap picture = BitmapFactory.decodeStream(new URL(avatar).openConnection().getInputStream());
                if (!e.isDisposed()){
                    e.onNext(picture);
                    e.onComplete();
                }
            }catch(MalformedURLException error){
                error.printStackTrace();
                if (!e.isDisposed()){
                    e.onError(error);
                    e.onComplete();
                }
                return;
            }
        }).subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( bitmap -> {
                if (bitmap != null) {
                    mProfileAvatar.setImageBitmap((Bitmap) bitmap);
                }
                mAvatarProgress.setVisibility(View.GONE);
            }, e -> mAvatarProgress.setVisibility(View.GONE) );
    }

    private void loadReportList(){

        Observable<List<Report>> reportsService = ForestGuardianService.global().service().listReports();

        reportsService.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( pReportList -> {

                ReportListAdapter adapter = new ReportListAdapter(this, pReportList);
                mListView.setAdapter(adapter);
                mProfileCountCreatedReports.setText( String.valueOf(pReportList.size()) );

        }, e-> Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show() );
    }

    //region camera

    @OnClick(R.id.change_profile_picture)
    public void captureImageAction(){

        ProfileActivityPermissionsDispatcher.captureImageWithCheck(this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Log.d("CameraDemo", "Picture saved");

            Bundle extras = data.getExtras();
            Bitmap profileBitmap = (Bitmap) extras.get("data");
            mProfileAvatar.setImageBitmap(profileBitmap);

            User user = new User();

            // use following method to convert bitmap to byte array:
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            profileBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] reportPictureByteArray = byteArrayOutputStream .toByteArray();

            // encode to Base64
            String encodedPicture = Base64.encodeToString(reportPictureByteArray, Base64.DEFAULT);
            user.setAvatar( "data:image/png;base64," + encodedPicture );

            user.setEmail( AuthenticationController.shared().getCurrentUser().getEmail() );
            Observable<Result<SessionData>> userService = ForestGuardianService.global().service().updateAccount(user);

            // spin animation
            ProgressDialog dialog = ProgressDialog.show(this, "", "Uploading. Please wait...", true);
            dialog.show();

            userService.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe( pSessionDataResult -> {
                        dialog.dismiss();

                        if( pSessionDataResult.isError() || pSessionDataResult.response().body() == null ){
                            Toast.makeText(this,pSessionDataResult.response().message(),Toast.LENGTH_LONG).show();
                            return;
                        }

                        User currentUser = pSessionDataResult.response().body().getUser();

                        // Save authentication headers for future requests.
                        Headers authHeaders = pSessionDataResult.response().headers();
                        AuthData authData = HeadersHelper.parseHeaders(this, authHeaders );
                        if ( authData == null ){
                            /* Check for error messages are ready for user viewing. */
                            Log.e( getLocalClassName(), "Auth headers are invalid." );
                            Toast.makeText(this, "Auth headers are invalid.", Toast.LENGTH_LONG ).show();
                            return;
                        }

                        currentUser.setAuth( authData );

                        AuthenticationController.shared().updateCurrentUser( currentUser );
                        loadProfileAvatar();
                    }, e-> Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show() );
        }
    }

    //endregion



}
