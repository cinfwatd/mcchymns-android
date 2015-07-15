package com.bitrient.mcchymns.fragment;

import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bitrient.mcchymns.BuildConfig;
import com.bitrient.mcchymns.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class AboutFragment extends Fragment {

    public AboutFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

//        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

        inflater.inflate(R.menu.menu_about, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_about, container, false);

        ImageView logo = (ImageView) rootView.findViewById(R.id.left_icon);
        logo.setImageResource(R.mipmap.ic_launcher);

        TextView version = (TextView) rootView.findViewById(R.id.version);

        ProgressBar splashProgress = (ProgressBar) rootView.findViewById(R.id.splash_progress);
        splashProgress.setIndeterminate(false);
        splashProgress.setMax(10);
        splashProgress.setProgress(10);

        version.setText(BuildConfig.VERSION_NAME);


        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.feedback:
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/email");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"mcchymns@bitrient.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Android Feedback");
                StringBuilder textBuilder = new StringBuilder("Device Info");
                char divider = '\n';
                String separator = "-------------------";

                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);



                textBuilder.append(divider).append(separator);
                textBuilder.append(divider);
                textBuilder.append("VERSION --- ").append(Build.VERSION.SDK_INT);
                textBuilder.append(divider);
                textBuilder.append("MANUFACTURER --- ").append(Build.MANUFACTURER);
                textBuilder.append(divider);
                textBuilder.append("MODEL --- ").append(Build.MODEL);
                textBuilder.append(divider);
                textBuilder.append("DISPLAY --- ").append(Build.DISPLAY);
                textBuilder.append(divider);
                textBuilder.append("DISPLAY SIZE --- ").append(size.x).append(" X ").append(size.y);
                textBuilder.append(divider);
                textBuilder.append(separator).append(divider).append("Put feedback here ...");

                emailIntent.putExtra(Intent.EXTRA_TEXT, textBuilder.toString());

                startActivity(Intent.createChooser(emailIntent, "Send Feedback"));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
