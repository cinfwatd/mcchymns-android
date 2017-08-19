package com.bitrient.mcchymns.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.bitrient.mcchymns.R;

/**
 * @author Cinfwat Probity <czprobity@bitrient.com>
 * @since 7/4/15
 */
public class SortDialogFragment extends DialogFragment {
    public static final int SORT_BY_FIRST_LINES = 1;

    SortDialogListener mDialogListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.sort)
                .setItems(R.array.sort_type, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        mDialogListener.onSortTypeSelected(which);
                    }
                });

        return builder.create();
    }

    interface SortDialogListener {
        void onSortTypeSelected(int which);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        final Fragment parentFragment = getTargetFragment();

        try {
            mDialogListener = (SortDialogListener) parentFragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(parentFragment.toString()
                    + " must implement SortDialogListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDialogListener = null;
    }
}
