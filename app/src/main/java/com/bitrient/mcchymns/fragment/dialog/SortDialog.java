package com.bitrient.mcchymns.fragment.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.bitrient.mcchymns.R;

/**
 * @author Cinfwat Probity <czprobity@bitrient.com>
 * @since 7/4/15
 */
public class SortDialog extends DialogFragment {
    public static final int SORT_BY_FIRST_LINES = 1;

    SortDialogListener mDialogListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialogWrapper.Builder builder = new AlertDialogWrapper.Builder(getActivity());
        builder.setTitle(R.string.sort_by)
                .setItems(R.array.sort_type, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDialogListener.onSortTypeSelected(which);
                    }
                });
        return builder.create();
    }

    public interface SortDialogListener {
        void onSortTypeSelected(int which);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

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
