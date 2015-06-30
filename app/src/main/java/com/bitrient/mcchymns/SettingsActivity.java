package com.bitrient.mcchymns;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.bitrient.mcchymns.fragment.SettingsActivityFragment;

/**
 * @author Cinfwat Probity <czprobity@bitrient.com>
 * @since 6/27/15
 */
public class SettingsActivity extends AppCompatActivity {
    public static final String KEY_PREF_SHOW_FAVORITES = "pref_show_favorites";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction()
                .replace(R.id.settings_content_frame, new SettingsActivityFragment()).commit();
    }
}
