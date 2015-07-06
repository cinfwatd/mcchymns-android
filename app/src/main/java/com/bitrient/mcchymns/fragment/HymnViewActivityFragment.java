package com.bitrient.mcchymns.fragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bitrient.mcchymns.HymnViewActivity;
import com.bitrient.mcchymns.R;
import com.bitrient.mcchymns.SettingsActivity;
import com.bitrient.mcchymns.database.HymnContract;
import com.bitrient.mcchymns.database.HymnDbHelper;
import com.bitrient.mcchymns.fragment.dialog.GotoHymnDialogFragment;
import com.bitrient.mcchymns.util.FontCache;


/**
 * A placeholder fragment containing a simple view.
 */
public class HymnViewActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    @SuppressWarnings("unused")
    private static final String TAG = HymnViewActivityFragment.class.getSimpleName();

    /**
     * Used to figure out if to initialize the favorite icon or set it based on the clicked event
     */
    private static final int SET_FAVORITE_INITIALIZE = 0;
    private static final int SET_FAVORITE_CLICKED = 1;

    private int mFavoritesIconType = 0;
    private ActionBar mActionBar;
    private GestureDetector mGestureDetector;

    private static final int HYMN_LOADER_ID = 3;

    private static final String CHORUS_VISIBILITY = "chorus_visibility";
    private boolean mHideChorus = false;

    public static HymnViewActivityFragment newInstance(Bundle args) {
        HymnViewActivityFragment hymnView = new HymnViewActivityFragment();
        hymnView.setArguments(args);

        return hymnView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ToggleFavoritesTask().execute(getHymnNumber(), SET_FAVORITE_INITIALIZE);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            mHideChorus = savedInstanceState.getBoolean(CHORUS_VISIBILITY, false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(CHORUS_VISIBILITY, mHideChorus);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(HYMN_LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        mActionBar.setHomeAsUpIndicator(R.mipmap.ic_queue_music_white_24dp);
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_hymn_view, container, false);


        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_goto_hymn, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_goto:
                GotoHymnDialogFragment hymnDialogFragment
                        = GotoHymnDialogFragment.newInstance(getHymnNumber());
                hymnDialogFragment.show(getFragmentManager(), "HymnDialogFragment");
                return true;

            case R.id.action_add_to_favorite:
//                Toast.makeText(getActivity(), "Add " + getHymnNumber() + " to favorites", Toast.LENGTH_SHORT).show();
                new ToggleFavoritesTask().execute(getHymnNumber(), SET_FAVORITE_CLICKED);
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
                settingsIntent.putExtra(SettingsActivityFragment.SHOW_HYMNS_CAT_ONLY, true);
                startActivity(settingsIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem favoriteMenuItem = menu.findItem(R.id.action_add_to_favorite);

        switch (mFavoritesIconType) {
            case ToggleFavoritesTask.SHOW_ON:
                setMenuIcon(favoriteMenuItem, R.mipmap.ic_action_favorite_on);
                break;
            case ToggleFavoritesTask.SHOW_OFF:
                setMenuIcon(favoriteMenuItem, R.mipmap.ic_action_favorite_off);
                break;

            case ToggleFavoritesTask.SHOW_ON_WITH_TOAST:
                setMenuIcon(favoriteMenuItem, R.mipmap.ic_action_favorite_on, R.string.added_to_favorites);
                break;
            case ToggleFavoritesTask.SHOW_OFF_WITH_TOAST:
                setMenuIcon(favoriteMenuItem,R.mipmap.ic_action_favorite_off, R.string.removed_from_favorites);
                break;
        }

        mFavoritesIconType = 0;
        super.onPrepareOptionsMenu(menu);
    }

    private void setMenuIcon(MenuItem menuItem, @DrawableRes int iconResId, @StringRes int resId) {
        menuItem.setIcon(iconResId);
        Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT).show();
    }

    private void setMenuIcon(MenuItem menuItem, @DrawableRes int iconResId) {
        menuItem.setIcon(iconResId);
    }

    private int getHymnNumber() {
        return getArguments().getInt(HymnViewActivity.SELECTED_HYMN, 40);
    }

    private void loadHymn(Cursor cursor) {
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View rootView = getView();

        final LinearLayout chorusContainer = (LinearLayout) rootView.findViewById(R.id.chorus_container);
        final LinearLayout hymnContainer = (LinearLayout) rootView.findViewById(R.id.hymnContainer);
        final ScrollView scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);

        mGestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (chorusContainer.getVisibility() == View.VISIBLE) {
                    chorusContainer.setVisibility(View.GONE);
                    mHideChorus = true;
                } else {
                    final TextView chorus = (TextView) chorusContainer.findViewById(R.id.chorus);

                    if (!TextUtils.isEmpty(chorus.getText())) {
                        chorusContainer.setVisibility(View.VISIBLE);
                        mHideChorus = false;
                    }
                }
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }
        });
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                return false;
            }
        });

        final SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());

        final String fontName = preferences.getString(SettingsActivityFragment.KEY_PREF_FONTS, "lilac_malaria.ttf");
        final Typeface typeface = FontCache.get(fontName, getActivity());

        final int fontSize = Integer.parseInt(preferences.getString(SettingsActivityFragment.KEY_PREF_FONT_SIZE, ""));
        final int fontColor = preferences.getInt(SettingsActivityFragment.KEY_PREF_FONT_COLOR, Color.BLACK);

        final String bGPath = preferences.getString(SettingsActivityFragment.KEY_PREF_HYMN_BACKGROUND, "");
        final String bGName = bGPath.substring(bGPath.lastIndexOf('/') + 1, bGPath.lastIndexOf('.'));

        final int backgroundId = getActivity().getResources().getIdentifier(bGName, "drawable", getActivity().getPackageName());
        rootView.setBackgroundResource(backgroundId);

        cursor.moveToFirst();

        int end = TextUtils.indexOf(cursor.getString(1), "\\n");
        String title = TextUtils.substring(cursor.getString(1), 0, end);
        String number = cursor.getString(3);

        mActionBar.setTitle(number + " - " + title);

        do {
            RelativeLayout stanza = (RelativeLayout) inflater.inflate(R.layout.stanza, null);
            TextView stanzaBody = (TextView) stanza.findViewById(R.id.stanza_body);
            TextView stanzaNumber = (TextView) stanza.findViewById(R.id.stanza_number);

            String cursorStanzaBody = cursor.getString(1).replaceAll("\\\\n", "\\\n");
            String cursorStanzaNumber = cursor.getString(0);

            if (cursorStanzaNumber.equals("0")) {
                TextView chorusTitle = (TextView) rootView.findViewById(R.id.chorus_title);
                TextView chorus = (TextView) rootView.findViewById(R.id.chorus);
                chorus.setText(cursorStanzaBody);

                chorusTitle.setTypeface(typeface);
                chorusTitle.setTextColor(fontColor);
                chorusTitle.setTextSize(fontSize + 1f);

                chorus.setTypeface(typeface);
                chorus.setTextColor(fontColor);
                chorus.setTextSize(fontSize);

                if (!mHideChorus) chorusContainer.setVisibility(View.VISIBLE);
                continue;
            }
            stanzaNumber.setText(cursorStanzaNumber);
            stanzaBody.setText(cursorStanzaBody);

            stanzaNumber.setTypeface(typeface);
            stanzaNumber.setTextColor(fontColor);
            stanzaNumber.setTextSize(fontSize);

            stanzaBody.setTypeface(typeface);
            stanzaBody.setTextColor(fontColor);
            stanzaBody.setTextSize(fontSize);

            hymnContainer.addView(stanza);
        }  while (cursor.moveToNext());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new HymnCursorLoader(getActivity(), getHymnNumber());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "Cursor loading finished - " + data.getCount());

        loadHymn(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private class ToggleFavoritesTask extends AsyncTask <Integer, Void, Integer> {
        static final int SHOW_ON = 1;
        static final int SHOW_ON_WITH_TOAST = 10;
        static final int SHOW_OFF = 2;
        static final int SHOW_OFF_WITH_TOAST = 20;

        @Override
        protected Integer doInBackground(Integer... params) {
            final ContentResolver contentResolver = getActivity().getContentResolver();

            String[] projection = {HymnContract.HymnEntry.COLUMN_NAME_FAVOURITE};
            String selection = HymnContract.HymnEntry.COLUMN_NAME_HYMN_NUMBER + " = ?";
            String[] selectionArgs = {Integer.toString(getHymnNumber())};

            final Cursor cursor = contentResolver.query(HymnContract.HymnEntry.CONTENT_URI, projection, selection, selectionArgs, null);

            cursor.moveToFirst();
            final boolean isNotFavorite = cursor.isNull(0);
            cursor.close();

            if (params[1] == SET_FAVORITE_INITIALIZE) {
                return isNotFavorite ? SHOW_OFF : SHOW_ON;
            }

            ContentValues values = new ContentValues();
            int result;
            if (isNotFavorite) {
                values.put(HymnContract.HymnEntry.COLUMN_NAME_FAVOURITE, "True");
                result = SHOW_ON_WITH_TOAST;
            } else {
                values.putNull(HymnContract.HymnEntry.COLUMN_NAME_FAVOURITE);
                result = SHOW_OFF_WITH_TOAST;
            }

            contentResolver.update(HymnContract.HymnEntry.CONTENT_URI, values, selection, selectionArgs);

//            Log.d(TAG, "YES - " + isNotFavorite);
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            mFavoritesIconType = result;
            if (getActivity() != null) getActivity().invalidateOptionsMenu();

//            Log.d(TAG, "Finished");
            super.onPostExecute(result);
        }
    }

    /**
     * @author Cinfwat Probity <czprobity@bitrient.com>
     * @since 5/9/15
     */
    public static class HymnCursorLoader extends AsyncTaskLoader<Cursor> {
        final ForceLoadContentObserver mObserver;

        Cursor mCursor;
        int mHymnNumber;

        public HymnCursorLoader(Context context, int hymnNumber) {
            super(context);
            mHymnNumber = hymnNumber;
            mObserver = new ForceLoadContentObserver();
        }

        @Override
        public Cursor loadInBackground() {

            SQLiteDatabase database;

            database = HymnDbHelper.getInstance(getContext()).getReadableDatabase();

            String query = "SELECT " + HymnContract.StanzaEntry.COLUMN_NAME_STANZA_NUMBER +
                    ", " + HymnContract.StanzaEntry.COLUMN_NAME_STANZA + ", " +
                    HymnContract.StanzaEntry.COLUMN_NAME_IS_CHORUS + ", " +
                    HymnContract.StanzaEntry.COLUMN_NAME_HYMN_NUMBER +
                    " FROM " + HymnContract.StanzaEntry.TABLE_NAME +
                    " WHERE " + HymnContract.StanzaEntry.COLUMN_NAME_HYMN_NUMBER +
                    " MATCH ?";

            String[] selectionArgs = {Integer.toString(mHymnNumber)};

            return database.rawQuery(query, selectionArgs);
        }

        /* Runs on the UI thread */
        @Override
        public void deliverResult(Cursor cursor) {
            if (isReset()) {
                // An async query came in while the loader is stopped
                if (cursor != null) {
                    cursor.close();
                }
                return;
            }
            Cursor oldCursor = mCursor;
            mCursor = cursor;

            if (isStarted()) {
                super.deliverResult(cursor);
            }

            if (oldCursor != null && oldCursor != cursor && !oldCursor.isClosed()) {
                oldCursor.close();
            }
        }

        @Override
        protected void onStartLoading() {
            if (mCursor != null) {
                deliverResult(mCursor);
            }
            if (takeContentChanged() || mCursor == null) {
                forceLoad();
            }
        }

        /**
         * Must be called from the UI thread
         */
        @Override
        protected void onStopLoading() {
            // Attempt to cancel the current load task if possible.
            cancelLoad();
        }

        @Override
        public void onCanceled(Cursor cursor) {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        @Override
        protected void onReset() {
            super.onReset();

            // Ensure the loader is stopped
            onStopLoading();

            if (mCursor != null && !mCursor.isClosed()) {
                mCursor.close();
            }
            mCursor = null;
        }
    }
}
