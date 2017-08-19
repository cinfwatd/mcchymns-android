package com.bitrient.mcchymns;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;


public class HymnDialerDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

//    private static final String ARG_HYMN_NUMBER = "hymn_number";
//
//    private int mHymnNumber;

    private HymnDialerInteractionListener mListener;
    private EditText mSelectedHymnTextView;

    public HymnDialerDialogFragment() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param hymnNumber Parameter 1.
//     * @return A new instance of fragment HymnDialerDialogFragment.
//     */
//    public static HymnDialerDialogFragment newInstance(int hymnNumber) {
//        HymnDialerDialogFragment fragment = new HymnDialerDialogFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_HYMN_NUMBER, hymnNumber);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mHymnNumber = getArguments().getInt(ARG_HYMN_NUMBER);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View contentView =  inflater.inflate(R.layout.fragment_hymn_dialer_dialog, container, false);
        mSelectedHymnTextView = (EditText) contentView.findViewById(R.id.selected_hymn);


        Button oneBtn = (Button) contentView.findViewById(R.id.btn_one);
        oneBtn.setOnClickListener(this);
        Button twoBtn = (Button) contentView.findViewById(R.id.btn_two);
        twoBtn.setOnClickListener(this);
        Button threeBtn = (Button) contentView.findViewById(R.id.btn_three);
        threeBtn.setOnClickListener(this);
        Button fourBtn = (Button) contentView.findViewById(R.id.btn_four);
        fourBtn.setOnClickListener(this);
        Button fiveBtn = (Button) contentView.findViewById(R.id.btn_five);
        fiveBtn.setOnClickListener(this);
        Button sixBtn = (Button) contentView.findViewById(R.id.btn_six);
        sixBtn.setOnClickListener(this);
        Button sevenBtn = (Button) contentView.findViewById(R.id.btn_seven);
        sevenBtn.setOnClickListener(this);
        Button eightBtn = (Button) contentView.findViewById(R.id.btn_eight);
        eightBtn.setOnClickListener(this);
        Button nineBtn = (Button) contentView.findViewById(R.id.btn_nine);
        nineBtn.setOnClickListener(this);
        Button zeroBtn = (Button) contentView.findViewById(R.id.btn_zero);
        zeroBtn.setOnClickListener(this);

        Button cancel = (Button) contentView.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        Button open = (Button) contentView.findViewById(R.id.btn_open);
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(mSelectedHymnTextView.getText())
                        && (Integer.parseInt(String.valueOf(mSelectedHymnTextView.getText())) > 0)) {

                    int selectedHymn = Integer.parseInt(String.valueOf(mSelectedHymnTextView.getText()));

                    onOpenPressed(selectedHymn);
                    dismiss();
                } else {
                    Toast.makeText(getActivity(),
                            getResources().getString(R.string.empty_hymn_number), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSelectedHymnTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = mSelectedHymnTextView.getText().toString();
                int length = value.length();
                if (length >= 1) mSelectedHymnTextView.setText(TextUtils.substring(value, 0, length -1));
            }
        });

        mSelectedHymnTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String value = mSelectedHymnTextView.getText().toString();
                if (!TextUtils.isEmpty(value)) mSelectedHymnTextView.setText("");
                return true;
            }
        });

        return contentView;
    }

    public void onOpenPressed(int number) {
        if (mListener != null) {
            mListener.onHymnDialerInteraction(number);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HymnDialerInteractionListener) {
            mListener = (HymnDialerInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement HymnDialerInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        TextView clickedBtn = (TextView) v;
        String value = String.valueOf(clickedBtn.getText());
        String curValue = String.valueOf(mSelectedHymnTextView.getText());

        int newValue = Integer.parseInt(String.format("%s%s", curValue, value));

        if (newValue > 0 && newValue <= 1200) mSelectedHymnTextView.setText(String.format(Locale.UK, "%d", newValue));

    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    interface HymnDialerInteractionListener {
        void onHymnDialerInteraction(int number);
    }
}
