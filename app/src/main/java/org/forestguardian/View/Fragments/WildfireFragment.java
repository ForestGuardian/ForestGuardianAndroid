package org.forestguardian.View.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.forestguardian.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WildfireFragment extends Fragment {


    public WildfireFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.wildfire_details,container,false);

        return view;
    }

}
