package com.bitrient.mcchymns.fragment;

import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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

    private static final int NUM_PAGES = 5;

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_help, container, false);

        mPager = (ViewPager) rootView.findViewById(R.id.help_pager);
        mPagerAdapter = new HelpSlidePagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        return rootView;
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

    private class HelpSlidePagerAdapter extends FragmentStatePagerAdapter {

        public HelpSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return HelpPageFragment.newInstance("Cinfwat", "Probity");
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
