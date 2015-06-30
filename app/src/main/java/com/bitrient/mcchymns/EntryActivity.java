package com.bitrient.mcchymns;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.TaskStackBuilder;

/**
 * @author Cinfwat Probity <czprobity@bitrient.com>
 * @since 6/30/15
 */
public class EntryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        check for loadingActivity first


        final SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (preferences
                .getBoolean(SettingsActivity.KEY_PREF_SHOW_FAVORITES, false)) {

            Intent favoritesIntent = new Intent(getApplicationContext(), FavoritesActivity.class);

            TaskStackBuilder.create(getApplicationContext())
                    .addNextIntentWithParentStack(favoritesIntent).startActivities();
        } else {
            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainIntent);
        }

        finish();
    }
}
