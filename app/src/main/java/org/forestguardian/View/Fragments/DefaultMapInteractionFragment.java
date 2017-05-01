package org.forestguardian.View.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.forestguardian.R;
import org.forestguardian.View.MapActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by emma on 30/04/17.
 */
public class DefaultMapInteractionFragment extends Fragment{

    public interface DefaultInteractionListener{
        void addReport();
        void centerOnLocation();
    }

    private DefaultInteractionListener mListener;

    @BindView(R.id.add_report_btn) FloatingActionButton mAddReportBtn;
    @BindView(R.id.center_location_btn) FloatingActionButton mCenterLocationBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.map_bottom_default, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
    }

    @OnClick(R.id.add_report_btn)
    public void onAddReportBtnClick(){
        if ( mListener != null ){
            mListener.addReport();
        }
    }

    @OnClick(R.id.center_location_btn)
    public void onCenterLocationClick(){
        if ( mListener != null ){
            mListener.centerOnLocation();
        }
    }

    public DefaultInteractionListener getListener() {
        return mListener;
    }

    public void setListener(final DefaultInteractionListener pListener) {
        mListener = pListener;
    }
}
