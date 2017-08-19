package com.bitrient.mcchymns;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bitrient.mcchymns.database.HymnContract;
import com.bitrient.mcchymns.database.HymnDbHelper;
import com.bitrient.mcchymns.util.FontCache;

public class HymnActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>  {

    static final String SELECTED_HYMN = "selected_hymn";

    private static final int SET_FAVORITE_INITIALIZE = 0;
    private static final int SET_FAVORITE_CLICKED = 1;
    private static final String SHOW_HELP_SNACK_BAR = "show_help_snack_bar";

    private int mFavoritesIconType = 0;
    private GestureDetector mGestureDetector;

    private static final int HYMN_LOADER_ID = 3;

    private static final String CHORUS_VISIBILITY = "chorus_visibility";
    private boolean mExpandBottomSheet = false;
    private String mHymnTitle;

    private LinearLayout mChorusContainer;
    private LinearLayout mStanzasContainer;
    private BottomSheetBehavior mBottomSheetBehavior;
    private FloatingActionButton mFab;

    private TextView mHymnSubject;
    private TextView mHymnTopic;
    private TextView mHymnNumber;
    private NestedScrollView mStanzaScrollView;
    private NestedScrollView mChorusScrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new ToggleFavoritesTask().execute(getHymnNumber(), SET_FAVORITE_INITIALIZE);

        setContentView(R.layout.activity_hymn);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        mHymnSubject = (TextView) findViewById(R.id.action_bar_hymn_subject);
        mHymnTopic = (TextView) findViewById(R.id.action_bar_hymn_topic);
        mHymnNumber = (TextView) findViewById(R.id.action_bar_hymn_number);

//        Chorus pane
        final View mChorusBottomSheet = findViewById(R.id.chorus_bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(mChorusBottomSheet);

        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                mExpandBottomSheet = BottomSheetBehavior.STATE_EXPANDED == newState;
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        mChorusContainer = (LinearLayout) findViewById(R.id.chorus_container);
        mStanzasContainer = (LinearLayout) findViewById(R.id.stanzas_container);
        mStanzaScrollView = (NestedScrollView) findViewById(R.id.stanzas_scroll_view);
        mChorusScrollView = (NestedScrollView) findViewById(R.id.chorus_scroll_view);

        mFab = (FloatingActionButton) findViewById(R.id.fab);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ToggleFavoritesTask().execute(getHymnNumber(), SET_FAVORITE_CLICKED);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState != null) {
            mExpandBottomSheet = savedInstanceState.getBoolean(CHORUS_VISIBILITY, false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(CHORUS_VISIBILITY, mExpandBottomSheet);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getSupportLoaderManager().initLoader(HYMN_LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hymn, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "MCCHymns: Sacred songs and solos");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.get_mcchymns_share, mHymnTitle));
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)));
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void prepareFab(int fabIconType) {
        mFavoritesIconType = fabIconType;

        switch (mFavoritesIconType) {
            case ToggleFavoritesTask.SHOW_ON:
                setFabIcon(R.drawable.ic_star_white_24dp);
                break;
            case ToggleFavoritesTask.SHOW_OFF:
                setFabIcon(R.drawable.ic_star_border_white_24dp);
                break;

            case ToggleFavoritesTask.SHOW_ON_WITH_TOAST:
                setFabIcon(R.drawable.ic_star_white_24dp, R.string.added_favourites);
                mFavoritesIconType = ToggleFavoritesTask.SHOW_ON; //so as not to show toast on redos
                break;
            case ToggleFavoritesTask.SHOW_OFF_WITH_TOAST:
                setFabIcon(R.drawable.ic_star_border_white_24dp, R.string.removed_favourites);
                mFavoritesIconType = ToggleFavoritesTask.SHOW_OFF;
                break;
        }

    }

    private void setFabIcon(@DrawableRes int iconResId, @StringRes int msgResId) {
        setFabIcon(iconResId);
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                msgResId, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new ToggleFavoritesTask().execute(getHymnNumber(), SET_FAVORITE_CLICKED);
                    }
                });
        snackbar.show();
    }

    private void setFabIcon(@DrawableRes int iconResId) {
        mFab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), iconResId));
    }

    private int getHymnNumber() {
        return getIntent().getIntExtra(SELECTED_HYMN, 40);
    }

    private void loadHymn(Cursor cursor) {
        final LayoutInflater inflater = this.getLayoutInflater();
        final View rootView = findViewById(R.id.chorus_bottom_sheet);
        final SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {

                if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    final TextView chorus = (TextView) mChorusContainer.findViewById(R.id.chorus);

                    if (!TextUtils.isEmpty(chorus.getText())) {
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }
        });

        mStanzaScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                return false;
            }
        });

        mChorusScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                return false;
            }
        });

        final boolean keep_screen_awake = preferences
                .getBoolean(getString(R.string.pref_key_keep_screen), false);
        if (keep_screen_awake) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        final String fontName = preferences.getString(getString(R.string.pref_key_font_style), "lilac_malaria.ttf");
        final Typeface typeface = FontCache.get(fontName, this);

        final int fontSize = Integer.parseInt(preferences.getString(getString(R.string.pref_key_font_size), getString(R.string.pref_default_font_size)));
        final int fontColor = preferences.getInt(getString(R.string.pref_key_font_color), Color.BLACK);

        float[] hsv = new float[3];
        Color.colorToHSV(fontColor, hsv);
        hsv[2] = 0.2f + 0.8f * hsv[2]; // hsv[2] = 1.0f - 0.8f * (1.0f - hsv[2]);

        final boolean showHelpSnackBar = preferences.getBoolean(SHOW_HELP_SNACK_BAR, true);
        final int backgroundColor = preferences.getInt(getString(R.string.pref_key_background_color), Color.WHITE);
        final boolean useTexture = preferences.getBoolean(getString(R.string.pref_key_background_texture), false);

        if (useTexture) {
            Drawable backgroundDrawable;

            backgroundDrawable = ContextCompat.getDrawable(this, R.drawable.hymn_background_gold);
            rootView.setBackground(backgroundDrawable);

            backgroundDrawable.setColorFilter(backgroundColor, PorterDuff.Mode.MULTIPLY);
        } else {
            rootView.setBackgroundColor(backgroundColor);
        }

        cursor.moveToFirst();

        int end = TextUtils.indexOf(cursor.getString(1), "\\n");
        final String title = TextUtils.substring(cursor.getString(1), 0, end);
        final String number = cursor.getString(2);
        final String topic = cursor.getString(3);
        final String subject = cursor.getString(4);


        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(title);
        mHymnTitle = title;

        mHymnSubject.setText(subject);
        mHymnTopic.setText(topic);
        mHymnNumber.setText(number);
        mStanzasContainer.removeAllViews();
        int chorusCount = 0;
        do {
            LinearLayout stanza = (LinearLayout) inflater.inflate(R.layout.stanza, null);
            CardView stanzaCard = (CardView) stanza.findViewById(R.id.stanza_card);
            TextView stanzaBody = (TextView) stanza.findViewById(R.id.stanza_body);

            if (useTexture) {
                Drawable backgroundDrawable;
                backgroundDrawable = ContextCompat.getDrawable(this, R.drawable.hymn_background_gold);
                stanzaCard.setBackground(backgroundDrawable);
                backgroundDrawable.setColorFilter(backgroundColor, PorterDuff.Mode.MULTIPLY);
            } else {
                stanzaCard.setBackgroundColor(backgroundColor);
            }

            String cursorStanzaBody = cursor.getString(1).replaceAll("\\\\n", "\\\n");
            String cursorStanzaNumber = cursor.getString(0);

            if (cursorStanzaNumber.equals("0")) { //Chorus
                TextView chorusTitle = (TextView) findViewById(R.id.chorus_title);
                TextView chorus = (TextView) findViewById(R.id.chorus);

                CharSequence oldValue = chorus.getText();
                CharSequence separator = "\n--------------------\n";

                if (!TextUtils.isEmpty(oldValue) && chorusCount != 0) {
                    oldValue = String.format("%S%S", oldValue, separator);
                } else {
                    oldValue = "";
                }

                chorus.setText(oldValue + cursorStanzaBody);
                chorusCount += 1; // hack to stop chorus from repeating

                chorusTitle.setTypeface(typeface);
                chorusTitle.setTextColor(fontColor);
                chorusTitle.setTextSize(fontSize + 1f);

                chorus.setTypeface(typeface);
                chorus.setTextColor(fontColor);
                chorus.setTextSize(fontSize);

                if (showHelpSnackBar) {
                    final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                            R.string.double_tap_screen, Snackbar.LENGTH_LONG);
                    snackbar.setAction("Got It", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean(SHOW_HELP_SNACK_BAR, false);
                        editor.apply();
                        }
                    });
                    snackbar.show();
                }

                if (mExpandBottomSheet) { // for rotations etc.
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                continue;
            }

            stanzaBody.setText(cursorStanzaNumber + "- " + cursorStanzaBody);
            stanzaBody.setTypeface(typeface);
            stanzaBody.setTextColor(fontColor);
            stanzaBody.setTextSize(fontSize);

            mStanzasContainer.addView(stanza);
        }  while (cursor.moveToNext());
    }

    public interface HymnSelectionListener {
        void onHymnSelectedInteraction(int hymnNumber);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new HymnCursorLoader(this, getHymnNumber());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        loadHymn(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private class ToggleFavoritesTask extends AsyncTask<Integer, Void, Integer> {
        static final int SHOW_ON = 1;
        static final int SHOW_ON_WITH_TOAST = 10;
        static final int SHOW_OFF = 2;
        static final int SHOW_OFF_WITH_TOAST = 20;

        @Override
        protected Integer doInBackground(Integer... params) {
            final ContentResolver contentResolver = getContentResolver();

            String[] projection = {HymnContract.HymnEntry.COLUMN_NAME_FAVOURITE};
            String selection = HymnContract.HymnEntry.COLUMN_NAME_HYMN_NUMBER + " = ?";
            String[] selectionArgs = {Integer.toString(getHymnNumber())};

            final Cursor cursor = contentResolver.query(HymnContract.HymnEntry.CONTENT_URI, projection, selection, selectionArgs, null);

            assert cursor != null;
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

            contentResolver.update(HymnContract.HymnEntry.CONTENT_URI, values, selection,
                    selectionArgs);

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            prepareFab(result);

            super.onPostExecute(result);
        }
    }

    /**
     * @author Cinfwat Probity <czprobity@bitrient.com>
     * @since 5/9/15
     */
    private static class HymnCursorLoader extends AsyncTaskLoader<Cursor> {
        final ForceLoadContentObserver mObserver;

        Cursor mCursor;
        int mHymnNumber;

        HymnCursorLoader(Context context, int hymnNumber) {
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
                    HymnContract.StanzaEntry.COLUMN_NAME_HYMN_NUMBER + ", " +
                    HymnContract.TopicEntry.COLUMN_NAME_TOPIC + ", " +
                    HymnContract.SubjectEntry.COLUMN_NAME_SUBJECT +
                    " FROM " + HymnContract.HYMNS_VIEW +
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
