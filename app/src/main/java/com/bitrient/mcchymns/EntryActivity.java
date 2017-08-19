package com.bitrient.mcchymns;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.bitrient.mcchymns.database.HymnDbHelper;

import java.io.File;

/**
 * @author Cinfwat Probity <czprobity@bitrient.com>
 * @since 6/30/15
 */
public class EntryActivity extends Activity {
    public static final String START_FAVORITES = "start_favorites";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.pref_appearance, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_information, false);

//        check for loadingActivity first
        if (databaseExists(getApplicationContext(), HymnDbHelper.DATABASE_NAME)) {

            final SharedPreferences preferences =
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            Bundle args = new Bundle();
            final String favourites_pref_key = getString(R.string.pref_key_favourites);
            if (preferences
                    .getBoolean(favourites_pref_key, false)) {

                args.putBoolean(START_FAVORITES, true);
            }

            Intent mainIntent = new Intent(getApplicationContext(), HomeActivity.class);
            mainIntent.putExtras(args);
            startActivity(mainIntent);


        } else {
            Intent splashIntent = new Intent(getApplicationContext(), SplashScreenActivity.class);
            startActivity(splashIntent);
        }

        finish();
    }

    private boolean databaseExists(Context context, String databaseName) {
        File dbFile = context.getDatabasePath(databaseName);

        return dbFile.exists();
    }
}
