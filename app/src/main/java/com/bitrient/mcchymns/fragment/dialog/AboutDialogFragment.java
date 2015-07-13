package com.bitrient.mcchymns.fragment.dialog;

import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bitrient.mcchymns.BuildConfig;
import com.bitrient.mcchymns.R;

/**
 * @author Cinfwat Probity <czprobity@bitrient.com>
 * @since 7/12/15
 */
public class AboutDialogFragment extends DialogFragment {
    RelativeLayout mCustomTitle;
    RelativeLayout mMainContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater layoutInflater = getLayoutInflater(savedInstanceState);
        mCustomTitle = (RelativeLayout) layoutInflater.inflate(R.layout.custom_dialog_title, null);

        ImageView logo = (ImageView) mCustomTitle.findViewById(R.id.left_icon);
        logo.setImageResource(R.mipmap.ic_launcher);
        TextView title = (TextView) mCustomTitle.findViewById(R.id.title);
        title.setText(R.string.about);
        TextView version = (TextView) mCustomTitle.findViewById(R.id.version);

        version.setText(BuildConfig.VERSION_NAME);

        mMainContent = (RelativeLayout) layoutInflater.inflate(R.layout.fragment_splash_screen, null);
        ProgressBar splashProgress = (ProgressBar) mMainContent.findViewById(R.id.splash_progress);
        splashProgress.setIndeterminate(false);
        splashProgress.setMax(10);
        splashProgress.setProgress(10);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setCustomTitle(mCustomTitle);
        builder.setView(mMainContent);
        return builder.create();
    }
}
