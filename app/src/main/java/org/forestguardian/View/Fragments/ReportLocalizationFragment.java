package org.forestguardian.View.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.forestguardian.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by emma on 30/04/17.
 */

public class ReportLocalizationFragment extends Fragment {

    @BindView(R.id.next_btn) FloatingActionButton mNextButton;

    private DefaultMapInteractionFragment.DefaultInteractionListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.map_bottom_report_localization, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public DefaultMapInteractionFragment.DefaultInteractionListener getListener() {
        return mListener;
    }

    public void setListener(final DefaultMapInteractionFragment.DefaultInteractionListener pListener) {
        mListener = pListener;
    }
}
