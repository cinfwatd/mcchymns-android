package com.bitrient.mcchymns.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bitrient.mcchymns.R;

/**
 * @author Cinfwat Probity <czprobity@bitrient.com>
 * @since 6/21/15
 */
public class TopicDialogFragment extends DialogFragment {
    private static String INDEX = "index";
    private static String TITLE = "title";

    TopicDialogListener mListener;

    public static TopicDialogFragment getInstance(int index, String subject) {

        TopicDialogFragment dialogFragment = new TopicDialogFragment();
        Bundle args = new Bundle();
        args.putInt(INDEX, index);
        args.putCharSequence(TITLE, subject);

        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        CharSequence title = getArguments().getCharSequence(TITLE);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View dialogHeader = inflater.inflate(R.layout.custom_dialog_header, null);

        final TextView dialogTitle = (TextView) dialogHeader.findViewById(R.id.dialog_title);
        final ProgressBar dialogProgressBar = (ProgressBar) dialogHeader.findViewById(R.id.dialog_progress_spinner);

        dialogTitle.setText(title);
//            dialogProgressBar


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCustomTitle(dialogHeader);

//            builder.setIcon(R.mipmap.ic_action_view_as_list);
        builder.setPositiveButton(getActivity().getResources().getString(R.string.apply_filter),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogPositiveClicked(TopicDialogFragment.this);
                    }
                });

        builder.setNegativeButton(getActivity().getResources().getString(R.string.clear_filter),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogNegativeClicked(TopicDialogFragment.this);
                    }
                });
        CharSequence[] charsequeneArr = new  CharSequence[] {
                "I Love Matjey",
                "I Love Matlong"
        };

        boolean[] checked = new boolean[2];
        checked[0] = true;

        int checkedInt = 0;
        builder.setMultiChoiceItems(charsequeneArr, checked, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

//            verify that the host activity inplements the callback interface
        final Fragment parentFragment = getTargetFragment();

        try {
            mListener = (TopicDialogListener) parentFragment;
        } catch (ClassCastException e) {
//                The activity doesn't implement the interface, throw exception.
            throw new ClassCastException(parentFragment.toString()
                    + " must implement TopicDialogListener");
        }
    }



    /**
     * The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passess the DialogFragment in case the host needs to query it.
     */
    public interface TopicDialogListener {
        void onDialogPositiveClicked(DialogFragment dialog);
        void onDialogNegativeClicked(DialogFragment dialog);
    }
}
