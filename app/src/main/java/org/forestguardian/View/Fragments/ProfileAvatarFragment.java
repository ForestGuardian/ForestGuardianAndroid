package org.forestguardian.View.Fragments;

import android.Manifest;
import android.app.Fragment;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.forestguardian.DataAccess.Local.SessionData;
import org.forestguardian.DataAccess.Local.User;
import org.forestguardian.DataAccess.WebServer.ForestGuardianService;
import org.forestguardian.Helpers.AuthenticationController;
import org.forestguardian.R;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.ObjectChangeSet;
import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObjectChangeListener;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import retrofit2.adapter.rxjava2.Result;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by emma on 10/07/17.
 */

//TODO: complete and replace profile views for this custom class.
@RuntimePermissions
public class ProfileAvatarFragment extends Fragment{

    private static final int CAMERA_IMAGE_REQUEST = 101;
    private static final int GALLERY_IMAGE_REQUEST = 102;

    @BindView(R.id.avatar_progress)         ProgressBar mAvatarProgress;
    @BindView(R.id.picture_view)            ImageView   mPictureView;
    @BindView(R.id.change_profile_picture)  ImageView   mChangeProfilePicture;

    private int mEditableVisibility = GONE;

    private RealmObjectChangeListener mRealmListener;
    private User mCurrentUser;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_picture_view,container,false);
        ButterKnife.bind(this,view);
        mChangeProfilePicture.setVisibility(mEditableVisibility);
        loadProfilePicture();

        mCurrentUser = AuthenticationController.shared().getCurrentUser();
        mRealmListener = (RealmObjectChangeListener<User>) (pUser, changeSet) -> {
            if ( changeSet.isFieldChanged("avatar") ){
                mPictureView.setImageBitmap(pUser.getUncompressedAvatar());
            }
        };
        mCurrentUser.addChangeListener(mRealmListener);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void loadProfilePicture(){
        mAvatarProgress.setVisibility(View.VISIBLE);
        mPictureView.setImageBitmap(AuthenticationController.shared().getCurrentUser().getUncompressedAvatar());
        mAvatarProgress.setVisibility(GONE);
    }

    public void enableEdit(){
        mEditableVisibility = VISIBLE;
    }

    @OnClick(R.id.change_profile_picture)
    public void captureImageAction(){
        ProfileAvatarFragmentPermissionsDispatcher.captureImageWithCheck(this);
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    public void captureImage(){
        // Creating image here
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, CAMERA_IMAGE_REQUEST);
    }

    @OnShowRationale(Manifest.permission.CAMERA)
    void showRationaleForCamera(final PermissionRequest request) {
        new AlertDialog.Builder(getActivity())
                .setMessage("Necesitamos acceso a la camara.")
                .setPositiveButton("Aceptar", (dialog, button) -> request.proceed())
                .setNegativeButton("Cancelar", (dialog, button) -> request.cancel())
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Log.d("CameraDemo", "Picture saved");

            Bundle extras = data.getExtras();
            Bitmap profileBitmap = (Bitmap) extras.get("data");

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
            ProgressDialog dialog = ProgressDialog.show(getActivity(), "", "Uploading. Please wait...", true);
            dialog.show();

            userService.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe( pSessionDataResult -> {
                        dialog.dismiss();

                        if( pSessionDataResult.isError() || pSessionDataResult.response().body() == null ){
                            Toast.makeText(getActivity(),pSessionDataResult.response().message(),Toast.LENGTH_LONG).show();
                            return;
                        }

                        User newUserInfo = pSessionDataResult.response().body().getUser();

                        Log.d("CurrentUserUpdateA", AuthenticationController.shared().getCurrentUser().toString());

                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        User currentUser = AuthenticationController.shared().getCurrentUser(realm);
                        currentUser.setAvatarUrl(newUserInfo.getAvatarUrl());
                        currentUser.updateAvatar();
                        realm.commitTransaction();

                        Log.d("NewUserUpdate", newUserInfo.getAvatarUrl());
                        Log.d("CurrentUserUpdateB", AuthenticationController.shared().getCurrentUser().toString());

                    }, e-> Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show() );
        }
    }

}
