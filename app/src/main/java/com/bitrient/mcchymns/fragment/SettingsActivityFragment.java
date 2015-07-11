package com.bitrient.mcchymns.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.text.TextUtils;
import android.widget.Toast;

import com.bitrient.mcchymns.R;

/**
 * @author Cinfwat Probity <czprobity@bitrient.com>
 * @since 6/27/15
 */
public class SettingsActivityFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String KEY_PREF_FONTS = "pref_hymn_fonts";
    public static final String KEY_PREF_FONT_SIZE = "pref_hymn_font_size";
    public static final String KEY_PREF_FONT_COLOR = "pref_hymn_font_color";
    public static final String KEY_PREF_SHOW_FAVORITES = "pref_show_favorites";
    public static final String KEY_PREF_HYMNS_CATEGORY = "pref_hymns_category";

    public static final String KEY_PREF_HYMN_BACKGROUND_COLOR = "pref_hymn_background_color";

    public static final String SHOW_HYMNS_CAT_ONLY = "show_hymns_category_only";
    public static final String KEY_PREF_USE_TEXTURE = "pref_use_texture_switch";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preference);

        Preference fontsPreference = findPreference(KEY_PREF_FONTS);

        String selectedFont = removeExtension(getPreferenceScreen().getSharedPreferences().getString(KEY_PREF_FONTS, ""));
        if (TextUtils.isEmpty(selectedFont)) {
            String defaultFont = removeExtension(getActivity().getString(R.string.pref_default_font));
            fontsPreference.setSummary(defaultFont);
        } else {
            fontsPreference.setSummary(selectedFont);
        }

        Bundle args = getArguments();
        if (args != null && args.getBoolean(SHOW_HYMNS_CAT_ONLY, false)) {

            PreferenceCategory hymnsCategory = (PreferenceCategory) findPreference(KEY_PREF_HYMNS_CATEGORY);
            hymnsCategory.setTitle(null);

            PreferenceScreen preferenceScreen = getPreferenceScreen();
            Preference favoritesPreference = findPreference(KEY_PREF_SHOW_FAVORITES);
            preferenceScreen.removePreference(favoritesPreference);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * Called when a shared preference is changed, added, or removed. This
     * may be called even if a preference is set to its existing value.
     * <p/>
     * <p>This callback will be run on your main thread.
     *
     * @param sharedPreferences The {@link SharedPreferences} that received
     *                          the change.
     * @param key               The key of the preference that was changed, added, or
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(KEY_PREF_FONTS)) {
            Preference fontsPreference = findPreference(key);
            fontsPreference.setSummary(removeExtension(sharedPreferences.getString(key, "")));
        } else if (key.equals(KEY_PREF_FONT_COLOR) || key.equals(KEY_PREF_HYMN_BACKGROUND_COLOR)) {
            checkBackgroundAndFontColor(sharedPreferences);
        }
    }

    private String removeExtension(String fileName) {
        String noExt;
        try {
            noExt = fileName.substring(0, fileName.lastIndexOf('.'));
        } catch (IndexOutOfBoundsException e) {
            noExt = "";
        }

        return removeUnderscore(noExt);
    }

    private String removeUnderscore(String fileName) {
        return fileName.replace('_', ' ');
    }

    private void checkBackgroundAndFontColor(SharedPreferences sharedPreferences) {
        final boolean isSame = sharedPreferences.getInt(KEY_PREF_HYMN_BACKGROUND_COLOR, 0) ==
                sharedPreferences.getInt(KEY_PREF_FONT_COLOR, 0);
        if (isSame) {
            Toast.makeText(getActivity(), "Font and Background color should not be the same.", Toast.LENGTH_LONG).show();
        }
    }
}
