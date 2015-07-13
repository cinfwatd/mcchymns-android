package com.bitrient.mcchymns.fragment;

import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bitrient.mcchymns.R;
import com.bitrient.mcchymns.fragment.dialog.AboutDialogFragment;


/**
 * A placeholder fragment containing a simple view.
 */
public class HelpFragment extends Fragment {

    public HelpFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

        inflater.inflate(R.menu.menu_help, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help, container, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
//                Intent aboutIntent = new Intent(getActivity(), AboutActivity.class);
//                startActivity(aboutIntent);
                AboutDialogFragment aboutFragment = new AboutDialogFragment();
                aboutFragment.show(getFragmentManager(), "about");
                break;
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
                textBuilder.append("VERSION --- " + Build.VERSION.SDK_INT);
                textBuilder.append(divider);
                textBuilder.append("MANUFACTURER --- " + Build.MANUFACTURER);
                textBuilder.append(divider);
                textBuilder.append("MODEL --- " + Build.MODEL);
                textBuilder.append(divider);
                textBuilder.append("DISPLAY --- " + Build.DISPLAY);
                textBuilder.append(divider);
                textBuilder.append("DISPLAY SIZE --- " + size.x + " X " + size.y).append(divider);
                textBuilder.append(separator).append(divider).append("Put feedback here ...");

                emailIntent.putExtra(Intent.EXTRA_TEXT, textBuilder.toString());

                startActivity(Intent.createChooser(emailIntent, "Send Feedback"));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
