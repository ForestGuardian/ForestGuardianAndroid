package org.forestguardian.View;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

import java.net.URL;
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
            Bitmap picture = BitmapFactory.decodeStream( new URL(currentUser.getAvatar()).openConnection().getInputStream() );
            if (!e.isDisposed()){
                e.onNext(picture);
                e.onComplete();
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
}
