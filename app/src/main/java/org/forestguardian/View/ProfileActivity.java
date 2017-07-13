package org.forestguardian.View;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.forestguardian.Adapters.ReportListAdapter;
import org.forestguardian.DataAccess.Local.Report;
import org.forestguardian.DataAccess.Local.User;
import org.forestguardian.DataAccess.WebServer.ForestGuardianService;
import org.forestguardian.Helpers.AuthenticationController;
import org.forestguardian.R;
import org.forestguardian.View.Fragments.ProfileAvatarFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by emma on 21/05/17.
 */

public class ProfileActivity extends Activity {

    private static final int CAMERA_IMAGE_REQUEST = 101;
    private static final int GALLERY_IMAGE_REQUEST = 102;

    @BindView(R.id.report_list)                 ListView    mListView;
    @BindView(R.id.profile_name)                TextView    mProfileNameView;
    @BindView(R.id.profile_city)                TextView    mProfileCity;
    @BindView(R.id.profile_created_reports)     TextView    mProfileCountCreatedReports;

    @BindView(R.id.profile_picture)             FrameLayout mProfilePictureContainer;
    private ProfileAvatarFragment mProfileAvatarFragment;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile_layout);
        ButterKnife.bind(this);

        loadReportList();
        loadProfileName();
        loadProfileAvatarFragment();
    }

    private void loadProfileAvatarFragment(){
        mProfileAvatarFragment = new ProfileAvatarFragment();
        mProfileAvatarFragment.enableEdit();
        getFragmentManager().beginTransaction().add( R.id.profile_picture, mProfileAvatarFragment ).commit();
    }

    private void loadProfileName(){
        User user = AuthenticationController.shared().getCurrentUser();
        mProfileNameView.setText(user.getName());
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

}
