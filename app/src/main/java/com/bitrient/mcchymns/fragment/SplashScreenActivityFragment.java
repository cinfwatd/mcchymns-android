package com.bitrient.mcchymns.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bitrient.mcchymns.R;
import com.bitrient.mcchymns.database.HymnDbHelper;


/**
 * A placeholder fragment containing a simple view.
 */
public class SplashScreenActivityFragment extends Fragment {

    SplashListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (SplashListener) activity;
        } catch (ClassCastException e) {
            throw new RuntimeException(activity.getLocalClassName() + " must implement the SplashListener interface.");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new DatabaseTask().execute(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash_screen, container, false);
    }

    private class DatabaseTask extends AsyncTask<Context, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            mListener.onLoadFinished();
        }

        @Override
        protected Void doInBackground(Context... params) {
//            mActivity = params[0];
            HymnDbHelper.getInstance(params[0]).getReadableDatabase();

            return null;
        }
    }

    public interface SplashListener {
        void onLoadFinished();
    }
}
