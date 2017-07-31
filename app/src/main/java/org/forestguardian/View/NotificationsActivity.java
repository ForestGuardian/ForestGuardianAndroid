package org.forestguardian.View;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.forestguardian.Adapters.NotificationListAdapter;
import org.forestguardian.Adapters.ReportListAdapter;
import org.forestguardian.DataAccess.Local.NotificationItem;
import org.forestguardian.DataAccess.Local.Report;
import org.forestguardian.DataAccess.WebServer.ForestGuardianService;
import org.forestguardian.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

public class NotificationsActivity extends AppCompatActivity {

    @BindView(R.id.notifications_list) ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        ButterKnife.bind(this);

        loadReportList();
    }

    private void loadReportList() {

        Realm realm = Realm.getDefaultInstance();
        List<NotificationItem> reportList = realm.where(NotificationItem.class).findAll();

        NotificationListAdapter adapter = new NotificationListAdapter(this, reportList);
        mListView.setAdapter(adapter);

    }

}
