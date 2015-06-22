package com.bitrient.mcchymns.fragment.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bitrient.mcchymns.R;
import com.bitrient.mcchymns.database.HymnContract;
import com.bitrient.mcchymns.database.HymnDbHelper;

/**
 * @author Cinfwat Probity <czprobity@bitrient.com>
 * @since 6/21/15
 */
public class TopicDialogFragment extends DialogFragment implements LoaderManager.LoaderCallbacks{
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
        CharSequence subject = getArguments().getCharSequence(TITLE);
        int subjectId = getArguments().getInt(INDEX);

        HymnDbHelper dbHelper = HymnDbHelper.getInstance(getActivity());
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = new String[] {
                HymnContract.TopicEntry._ID,
//                HymnContract.TopicEntry.COLUMN_NAME_SUBJECT_ID,
                HymnContract.TopicEntry.COLUMN_NAME_TOPIC
        };

        String selection = HymnContract.TopicEntry.COLUMN_NAME_SUBJECT_ID + " = ?";
        String[] selectionArgs = new String[] {
                subject.toString()
        };

        String groupBy = null;
        String having = null;
        String orderBy = null;

        final Cursor cursor = db.query(HymnContract.TopicEntry.TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy);


        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View dialogHeader = inflater.inflate(R.layout.custom_dialog_header, null);

        final TextView dialogTitle = (TextView) dialogHeader.findViewById(R.id.dialog_title);
        final ProgressBar dialogProgressBar = (ProgressBar) dialogHeader.findViewById(R.id.dialog_progress_spinner);

        dialogTitle.setText(subject);
//            dialogProgressBar

        ContextThemeWrapper wrapper = new ContextThemeWrapper(getActivity(), R.style.AlertDialogTheme);

        AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);
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

        builder.setMultiChoiceItems(cursor, null, HymnContract.TopicEntry.COLUMN_NAME_TOPIC, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

            }
        });
//        CharSequence[] charsequeneArr = new  CharSequence[] {
//                "Love",
//                "Peace"
//        };
//
//        boolean[] checked = new boolean[2];
//        checked[0] = true;
//
//        int checkedInt = 0;
//        builder.setMultiChoiceItems(charsequeneArr, checked, new DialogInterface.OnMultiChoiceClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//
//            }
//        });
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

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {

    }

    @Override
    public void onLoaderReset(Loader loader) {

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
