package org.forestguardian.View;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.forestguardian.Adapters.ReportListAdapter;
import org.forestguardian.DataAccess.Local.Report;
import org.forestguardian.DataAccess.Local.User;
import org.forestguardian.DataAccess.WebServer.ForestGuardianService;
import org.forestguardian.ForestGuardianApplication;
import org.forestguardian.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

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

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile_layout);
        ButterKnife.bind(this);

        loadReportList();
        loadProfileAvatar();
    }

    private void loadProfileAvatar(){
        Observable.create(e -> {
            User currentUser = ((ForestGuardianApplication)getApplicationContext()).getCurrentUser();

            mProfileNameView.setText(currentUser.getName());

            String avatar = currentUser.getAvatar();
            if (avatar == null){
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
                return;
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( bitmap -> mProfileAvatar.setImageBitmap((Bitmap)bitmap) );
    }

    private void loadReportList(){

        Observable<List<Report>> reportsService = ForestGuardianService.global().service().listReports();

        reportsService.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( pReportList -> {

                ReportListAdapter adapter = new ReportListAdapter(this, pReportList);
                mListView.setAdapter(adapter);
                mProfileCountCreatedReports.setText( String.valueOf(pReportList.size()) );

        });
    }

//    @OnClick(R.id.signup_profile_picture)
//    public void captureImageAction(){
//
//        ProfileActivityPermissionsDispatcher.captureImageWithCheck(this);
//    }

    @NeedsPermission(Manifest.permission.CAMERA)
    public void captureImage(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, CAMERA_IMAGE_REQUEST);

    }



}
