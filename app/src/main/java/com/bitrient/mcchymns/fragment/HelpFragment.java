package com.bitrient.mcchymns.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Dear bitrient ..");

                startActivity(Intent.createChooser(emailIntent, "Send Feedback"));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
