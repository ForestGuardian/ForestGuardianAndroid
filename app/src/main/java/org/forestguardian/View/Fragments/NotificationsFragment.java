package org.forestguardian.View.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.forestguardian.Adapters.NotificationListAdapter;
import org.forestguardian.DataAccess.Local.NotificationItem;
import org.forestguardian.DataAccess.Local.Report;
import org.forestguardian.DataAccess.WebServer.ForestGuardianService;
import org.forestguardian.R;
import org.forestguardian.View.ProfileActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

/**
 * Created by emma on 02/08/17.
 */

public class NotificationsFragment extends Fragment {

    @BindView(R.id.notifications_list) ListView mListView;
    @BindView(R.id.warning_notifications_text) TextView mWarningView;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.activity_notifications,container,false);
        ButterKnife.bind(this, view);

        loadReportList();
        handleListViewEvents();

        return view;
    }

    private void loadReportList() {

        Realm realm = Realm.getDefaultInstance();
        List<NotificationItem> reportList = realm.where(NotificationItem.class).findAll();

        NotificationListAdapter adapter = new NotificationListAdapter(getActivity(), reportList);
        mListView.setAdapter(adapter);

        if ( reportList.size() == 0 ){
            mWarningView.setVisibility(View.VISIBLE);
        }else{
            mWarningView.setVisibility(View.GONE);
        }
    }

    private void handleListViewEvents() {
        if (mListView != null) {
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Observable<Report> reportService = ForestGuardianService.global().service().getReport(id);

                    reportService.subscribeOn(Schedulers.newThread())
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .subscribe( pReport -> {
                                // Load the wildfire detail screen
                                // TODO implement the intent here
                            }, e-> Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show() );
                }
            });
        }
    }


}
