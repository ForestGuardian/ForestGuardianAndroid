package org.forestguardian.View;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ListView;

import org.forestguardian.Adapters.ReportListAdapter;
import org.forestguardian.DataAccess.Local.Report;
import org.forestguardian.DataAccess.WebServer.ForestGuardianService;
import org.forestguardian.R;

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

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile_layout);
        ButterKnife.bind(this);

        loadReportList();
    }

    private void loadReportList(){

        Observable<List<Report>> reportsService = ForestGuardianService.global().service().listReports();

        reportsService.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( pReportList -> {

                ReportListAdapter adapter = new ReportListAdapter(this, pReportList);
                mListView.setAdapter(adapter);

        });

    }
}
