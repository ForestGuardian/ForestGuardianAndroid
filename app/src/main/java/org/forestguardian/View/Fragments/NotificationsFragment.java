package org.forestguardian.View.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.forestguardian.Adapters.NotificationListAdapter;
import org.forestguardian.DataAccess.Local.NotificationItem;
import org.forestguardian.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by emma on 02/08/17.
 */

public class NotificationsFragment extends Fragment {

    @BindView(R.id.notifications_list) ListView mListView;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.activity_notifications,container,false);
        ButterKnife.bind(this, view);

        loadReportList();
        return view;
    }

    private void loadReportList() {

        Realm realm = Realm.getDefaultInstance();
        List<NotificationItem> reportList = realm.where(NotificationItem.class).findAll();

        NotificationListAdapter adapter = new NotificationListAdapter(getActivity(), reportList);
        mListView.setAdapter(adapter);

    }


}
