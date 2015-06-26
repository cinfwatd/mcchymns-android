package com.bitrient.mcchymns.fragment.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.bitrient.mcchymns.HymnViewActivity;
import com.bitrient.mcchymns.R;

/**
 * @author Cinfwat Probity <czprobity@bitrient.com>
 * @since 6/24/15
 */
public class GotoHymnDialogFragment extends DialogFragment {

    public static GotoHymnDialogFragment newInstance() {
        return new GotoHymnDialogFragment();
    }

    @SuppressWarnings("unused")
    private static final String TAG = GotoHymnDialogFragment.class.getSimpleName();
    public static final String SELECTED_HYMN = "selected";

    View mDialogHeader;
    LinearLayout mDialogView;
    NumberPicker mNumberPicker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = getActivity().getLayoutInflater();


        mDialogHeader = inflater.inflate(R.layout.dialog_header, null);
        ImageView dialogIcon = (ImageView) mDialogHeader.findViewById(R.id.dialog_icon);
        dialogIcon.setImageResource(R.mipmap.ic_action_goto_hymn);

        int padding = getResources().getDimensionPixelOffset(R.dimen.tiny);

        dialogIcon.setPadding(padding, padding, padding, padding);
        TextView dialogTitle = (TextView) mDialogHeader.findViewById(R.id.dialog_title);
        dialogTitle.setText(R.string.select_hymn_number);

        mDialogView = (LinearLayout) inflater.inflate(R.layout.dialog_number_picker, null);
        mNumberPicker = (NumberPicker) mDialogView.findViewById(R.id.dialog_number_picker);

        mNumberPicker.setMinValue(1);
        mNumberPicker.setMaxValue(1200);

        if (savedInstanceState != null) {
            mNumberPicker.setValue(savedInstanceState.getInt(SELECTED_HYMN));
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle(getActivity().getResources().getString(R.string.select_hymn_number));
        builder.setCustomTitle(mDialogHeader);
        builder.setView(mDialogView);
        builder.setPositiveButton(getActivity().getResources().getString(R.string.open_hymn),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int selectedHymn = mNumberPicker.getValue();

                        Intent hymnIntent = new Intent(getActivity(), HymnViewActivity.class);
                        hymnIntent.putExtra(SELECTED_HYMN, selectedHymn);
                        startActivity(hymnIntent);
                    }
                });

        builder.setNegativeButton(getActivity().getResources().getString(R.string.confirm_decline),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_HYMN, mNumberPicker.getValue());
        super.onSaveInstanceState(outState);
    }
}
