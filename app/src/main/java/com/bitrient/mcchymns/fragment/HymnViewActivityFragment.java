package com.bitrient.mcchymns.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bitrient.mcchymns.R;


/**
 * A placeholder fragment containing a simple view.
 */
public class HymnViewActivityFragment extends Fragment {

    public HymnViewActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hymn_view, container, false);
    }
}
