package org.forestguardian.View.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.forestguardian.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by emma on 30/04/17.
 */

public class ReportLocalizationFragment extends Fragment {

    public interface OnReportLocalizationListener{
        void reportLocationReady();
    }

    @BindView(R.id.next_btn) Button mNextButton;

    private OnReportLocalizationListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.map_bottom_report_localization, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.next_btn)
    public void onNextBtnClick(){
        if (mListener != null){
            mListener.reportLocationReady();
        }
    }

    public OnReportLocalizationListener getListener() {
        return mListener;
    }

    public void setListener(final OnReportLocalizationListener pListener) {
        mListener = pListener;
    }
}
