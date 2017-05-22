package org.forestguardian.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import org.forestguardian.DataAccess.Local.Report;
import org.forestguardian.R;
import org.forestguardian.View.CustomViews.ReportItemView;

import java.util.List;

/**
 * Created by emma on 21/05/17.
 */

public class ReportListAdapter extends ArrayAdapter<Report> {

    private Context mContext;

    public ReportListAdapter(Context context, List<Report> reports) {
        super(context, 0, reports);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Data
        Report report = getItem(position);

        // Recycling
        if (convertView == null) {
            convertView = new ReportItemView(mContext,report);
        }

        return convertView;
    }


}
