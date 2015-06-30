package com.bitrient.mcchymns.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bitrient.mcchymns.HymnViewActivity;
import com.bitrient.mcchymns.R;
import com.bitrient.mcchymns.fragment.dialog.GotoHymnDialogFragment;


/**
 * A placeholder fragment containing a simple view.
 */
public class HymnViewActivityFragment extends Fragment {

    public static HymnViewActivityFragment newInstance(Bundle args) {
        HymnViewActivityFragment hymnView = new HymnViewActivityFragment();
        hymnView.setArguments(args);

        return hymnView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(Integer.toString(getHymnNumber()));

        return inflater.inflate(R.layout.fragment_hymn_view, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_goto_hymn, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_goto:
                GotoHymnDialogFragment hymnDialogFragment
                        = GotoHymnDialogFragment.newInstance();
                hymnDialogFragment.show(getFragmentManager(), "HymnDialogFragment");
                return true;

            case R.id.action_add_to_favorite:
                Toast.makeText(getActivity(), "Add " + getHymnNumber() + " to favorites", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private int getHymnNumber() {
        return getArguments().getInt(HymnViewActivity.SELECTED_HYMN, 40);
    }
}
