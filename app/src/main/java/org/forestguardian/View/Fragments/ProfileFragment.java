package org.forestguardian.View.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by emma on 02/08/17.
 */

public class ProfileFragment extends Fragment {

    private static final int CAMERA_IMAGE_REQUEST = 101;
    private static final int GALLERY_IMAGE_REQUEST = 102;

    @BindView(R.id.report_list)
    ListView mListView;
    @BindView(R.id.profile_name)
    TextView mProfileNameView;
    @BindView(R.id.profile_city)                TextView    mProfileCity;
    @BindView(R.id.profile_created_reports)     TextView    mProfileCountCreatedReports;

    @BindView(R.id.profile_picture)             FrameLayout mProfilePictureContainer;

    private ProfileAvatarFragment mProfileAvatarFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.profile_layout, container, false);
        ButterKnife.bind(this, view);

        loadReportList();
        loadProfileName();
        loadProfileAvatarFragment();

        return view;
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

                    ReportListAdapter adapter = new ReportListAdapter(getActivity(), pReportList);
                    mListView.setAdapter(adapter);
                    mProfileCountCreatedReports.setText( String.valueOf(pReportList.size()) );

                }, e-> Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show() );
    }

}
