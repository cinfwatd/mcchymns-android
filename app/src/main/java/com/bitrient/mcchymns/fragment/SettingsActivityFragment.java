package com.bitrient.mcchymns.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.bitrient.mcchymns.R;

/**
 * @author Cinfwat Probity <czprobity@bitrient.com>
 * @since 6/27/15
 */
public class SettingsActivityFragment extends PreferenceFragment{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preference);
    }
}
