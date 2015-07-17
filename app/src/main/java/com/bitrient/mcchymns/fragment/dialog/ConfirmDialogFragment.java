package com.bitrient.mcchymns.fragment.dialog;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.bitrient.mcchymns.R;
import com.bitrient.mcchymns.database.HymnContract;

/**
 * @author Cinfwat Probity <czprobity@bitrient.com>
 * @since 6/21/15
 */
public class ConfirmDialogFragment extends DialogFragment {

    public static ConfirmDialogFragment newInstance() {
        return new ConfirmDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialogWrapper.Builder builder = new AlertDialogWrapper.Builder(getActivity());
        builder.setTitle(getActivity().getResources().getString(R.string.favorites_confirm_remove));
        builder.setIcon(R.mipmap.ic_action_discard);
        builder.setPositiveButton(getActivity().getResources().getString(R.string.favorites_confirm_accept),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new RemoveTask().execute();
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

    private class RemoveTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            ContentValues values = new ContentValues();
            values.putNull(HymnContract.HymnEntry.COLUMN_NAME_FAVOURITE);

            String selection = HymnContract.HymnEntry.COLUMN_NAME_FAVOURITE + " IS NOT NULL";
            getActivity().getContentResolver().update(HymnContract.HymnEntry.CONTENT_FTS_URI, values, selection, null);

            return null;
        }
    }
}
