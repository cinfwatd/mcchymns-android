package com.bitrient.mcchymns.fragment;


import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitrient.mcchymns.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HelpPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HelpPageFragment extends Fragment {
    // the fragment initialization parameters,
    private static final String ARG_POSITION = "position";

    private int mPosition;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param position The position of the fragment in the viewpager.
     * @return A new instance of fragment HelpPageFragment.
     */
    public static HelpPageFragment newInstance(int position) {
        HelpPageFragment fragment = new HelpPageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public HelpPageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPosition = getArguments().getInt(ARG_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_help_page, container, false);

        ImageView imageView = (ImageView) rootView.findViewById(R.id.slide_show);
        TextView titleView = (TextView) rootView.findViewById(R.id.title);
        TextView detailsView = (TextView) rootView.findViewById(R.id.details);

        switch (mPosition) {
            case 0:
                imageView.setImageResource(R.mipmap.ic_action_queue_music);
                titleView.setText(R.string.sliding_navigation_pane);
                detailsView.setText(R.string.sliding_navigation_pane_details);

                break;
            case 1:
                imageView.setImageResource(R.mipmap.ic_action_goto_hymn);
                titleView.setText(R.string.quick_goto_hymn);
                detailsView.setText(R.string.quick_goto_hymn_details);

                break;
            case 2:
                imageView.setBackgroundResource(R.drawable.help_animation_one);
                AnimationDrawable frameAnimation = (AnimationDrawable) imageView.getBackground();
                frameAnimation.start();

                titleView.setText(R.string.hymn_chorus_pane);
                detailsView.setText(R.string.hymn_chorus_pane_details);

                break;
            case 3:
                imageView.setImageResource(R.mipmap.help_favorites);
                titleView.setText(R.string.favorites_collection);
                detailsView.setText(R.string.favorites_collection_details);

                break;
            default:
                imageView.setImageResource(R.mipmap.ic_launcher);
                titleView.setText(R.string.app_name);
                detailsView.setText(R.string.ssands);
        }

        return rootView;
    }


}
