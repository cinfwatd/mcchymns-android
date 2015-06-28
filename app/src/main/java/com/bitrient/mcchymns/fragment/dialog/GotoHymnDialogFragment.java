package com.bitrient.mcchymns.fragment.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bitrient.mcchymns.HymnViewActivity;
import com.bitrient.mcchymns.R;

/**
 * @author Cinfwat Probity <czprobity@bitrient.com>
 * @since 6/24/15
 */
public class GotoHymnDialogFragment extends DialogFragment implements View.OnClickListener{

    public static GotoHymnDialogFragment newInstance() {
        return new GotoHymnDialogFragment();
    }

    @SuppressWarnings("unused")
    private static final String TAG = GotoHymnDialogFragment.class.getSimpleName();

    View mDialogHeader;
    LinearLayout mDialogView;
    EditText mSelectedHymnTextView;

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

        Button acceptBtn = (Button) mDialogView.findViewById(R.id.btn_accept);
        Button declineBtn = (Button) mDialogView.findViewById(R.id.btn_cancel);

        Button clearBtn = (Button) mDialogView.findViewById(R.id.btn_clear);

        if (savedInstanceState != null) {
            mSelectedHymnTextView.setText(savedInstanceState.getString(HymnViewActivity.SELECTED_HYMN));
        }

        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(mSelectedHymnTextView.getText())
                        && (Integer.parseInt(String.valueOf(mSelectedHymnTextView.getText())) > 0)) {
                    Intent hymnIntent = new Intent(getActivity(), HymnViewActivity.class);
                    hymnIntent.putExtra(HymnViewActivity.SELECTED_HYMN, Integer.parseInt(String.valueOf(mSelectedHymnTextView.getText())));
                    startActivity(hymnIntent);
                    dismiss();
                } else {
                    Toast.makeText(getActivity(),
                            getResources().getString(R.string.empty_hymn_number), Toast.LENGTH_SHORT).show();
                }

            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = mSelectedHymnTextView.getText().toString();
                int length = value.length();
                if (length >= 1) mSelectedHymnTextView.setText(TextUtils.substring(value, 0, length -1));
            }
        });

        clearBtn.setOnLongClickListener(new View.OnLongClickListener() {
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle(getActivity().getResources().getString(R.string.select_hymn_number));
        builder.setCustomTitle(mDialogHeader);
        builder.setView(mDialogView);
        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(HymnViewActivity.SELECTED_HYMN,  mSelectedHymnTextView.getText().toString());
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
}
