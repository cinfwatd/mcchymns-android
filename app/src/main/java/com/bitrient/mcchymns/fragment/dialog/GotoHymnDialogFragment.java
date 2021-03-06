package com.bitrient.mcchymns.fragment.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bitrient.mcchymns.R;
import com.bitrient.mcchymns.fragment.HymnsViewFragment;

/**
 * @author Cinfwat Probity <czprobity@bitrient.com>
 * @since 6/24/15
 */
public class GotoHymnDialogFragment extends DialogFragment implements View.OnClickListener{

    private static final String CURRENT_HYMN = "currentHymn";

    public static GotoHymnDialogFragment newInstance() {
        return new GotoHymnDialogFragment();
    }
    public static GotoHymnDialogFragment newInstance(int currentHymnNumber) {
        final GotoHymnDialogFragment hymnDialogFragment = new GotoHymnDialogFragment();
        Bundle args = new Bundle();
        args.putInt(CURRENT_HYMN, currentHymnNumber);
        hymnDialogFragment.setArguments(args);

        return hymnDialogFragment;
    }

    @SuppressWarnings("unused")
    private static final String TAG = GotoHymnDialogFragment.class.getSimpleName();

    LinearLayout mDialogView;
    EditText mSelectedHymnTextView;

    HymnSelectionListener mSelectionListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        mDialogView = (LinearLayout) inflater.inflate(R.layout.dialog_keypad, null);
        mSelectedHymnTextView = (EditText) mDialogView.findViewById(R.id.selected_hymn);

        Button oneBtn = (Button) mDialogView.findViewById(R.id.btn_one);
        oneBtn.setOnClickListener(this);
        Button twoBtn = (Button) mDialogView.findViewById(R.id.btn_two);
        twoBtn.setOnClickListener(this);
        Button threeBtn = (Button) mDialogView.findViewById(R.id.btn_three);
        threeBtn.setOnClickListener(this);
        Button fourBtn = (Button) mDialogView.findViewById(R.id.btn_four);
        fourBtn.setOnClickListener(this);
        Button fiveBtn = (Button) mDialogView.findViewById(R.id.btn_five);
        fiveBtn.setOnClickListener(this);
        Button sixBtn = (Button) mDialogView.findViewById(R.id.btn_six);
        sixBtn.setOnClickListener(this);
        Button sevenBtn = (Button) mDialogView.findViewById(R.id.btn_seven);
        sevenBtn.setOnClickListener(this);
        Button eightBtn = (Button) mDialogView.findViewById(R.id.btn_eight);
        eightBtn.setOnClickListener(this);
        Button nineBtn = (Button) mDialogView.findViewById(R.id.btn_nine);
        nineBtn.setOnClickListener(this);
        Button zeroBtn = (Button) mDialogView.findViewById(R.id.btn_zero);
        zeroBtn.setOnClickListener(this);

        if (savedInstanceState != null) {
            mSelectedHymnTextView.setText(savedInstanceState.getString(HymnsViewFragment.SELECTED_HYMN));
        }

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
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());

        builder.customView(mDialogView, true);
        builder.negativeText(R.string.confirm_decline);
        builder.autoDismiss(false);

        builder.positiveText(R.string.confirm_accept);

        builder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                if (!TextUtils.isEmpty(mSelectedHymnTextView.getText())
                        && (Integer.parseInt(String.valueOf(mSelectedHymnTextView.getText())) > 0)) {

                    int selectedHymn = Integer.parseInt(String.valueOf(mSelectedHymnTextView.getText()));

                    // check if the selected Hymn == current hymnn
                    final Bundle arguments = getArguments();
                    if (arguments != null && arguments.getInt(CURRENT_HYMN, 0) == selectedHymn) {
                        dismiss();
                        return;
                    }


                    mSelectionListener.hymnSelected(selectedHymn);
                    dismiss();
                } else {
                    Toast.makeText(getActivity(),
                            getResources().getString(R.string.empty_hymn_number), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onNegative(MaterialDialog dialog) {
                dismiss();
            }
        });

        return builder.build();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(HymnsViewFragment.SELECTED_HYMN,  mSelectedHymnTextView.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        TextView clickedBtn = (TextView) v;
        String value = String.valueOf(clickedBtn.getText());
        String curValue = String.valueOf(mSelectedHymnTextView.getText());

        int newValue = Integer.parseInt(String.format("%s%s", curValue, value));

        if (newValue > 0 && newValue <= 1200) mSelectedHymnTextView.setText(Integer.toString(newValue));
    }

    public interface HymnSelectionListener {
        void hymnSelected(int hymnNumber);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mSelectionListener = (HymnSelectionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                " must implement the HymnSelectionListener.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSelectionListener = null;
    }
}
