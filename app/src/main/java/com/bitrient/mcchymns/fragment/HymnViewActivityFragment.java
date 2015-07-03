package com.bitrient.mcchymns.fragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
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

    public static HymnViewActivityFragment newInstance(Bundle args) {
        HymnViewActivityFragment hymnView = new HymnViewActivityFragment();
        hymnView.setArguments(args);

        return hymnView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new AddFavoriteTask().execute(getHymnNumber(), SET_FAVORITE_INITIALIZE);
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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_hymn_view, container, false);

        mGestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                Log.d(TAG, "YES _ Double tap");
                return super.onDoubleTapEvent(e);
            }
        });
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("TAG", "YES");

                mGestureDetector.onTouchEvent(event);
                return true;
            }
        });
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
                new AddFavoriteTask().execute(getHymnNumber(), SET_FAVORITE_CLICKED);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem favoriteMenuItem = menu.findItem(R.id.action_add_to_favorite);

        switch (mFavoritesIconType) {
            case AddFavoriteTask.SHOW_ON:
                favoriteMenuItem.setIcon(R.mipmap.ic_action_favorite_on);
                break;
            case AddFavoriteTask.SHOW_OFF:
                favoriteMenuItem.setIcon(R.mipmap.ic_action_favorite_off);
                break;
        }

        super.onPrepareOptionsMenu(menu);
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

        cursor.moveToFirst();

        int end = TextUtils.indexOf(cursor.getString(1), "\\n");
        String title = TextUtils.substring(cursor.getString(1), 0, end);
        mActionBar.setTitle(String.valueOf(title));
        final Typeface typeface = FontCache.get("lilac_malaria.ttf", getActivity());

        do {
            RelativeLayout stanza = (RelativeLayout) inflater.inflate(R.layout.stanza, null);
            TextView stanzaBody = (TextView) stanza.findViewById(R.id.stanza_body);
            TextView stanzaNumber = (TextView) stanza.findViewById(R.id.stanza_number);

            String cursorStanzaBody = cursor.getString(1).replaceAll("\\\\n", "\\\n");
            String cursorStanzaNumber = cursor.getString(0);

            if (cursorStanzaNumber.equals("0")) {
//                TextView chorusTitle = (TextView) rootView.findViewById(R.id.chorus_title);
                TextView chorus = (TextView) rootView.findViewById(R.id.chorus);
                chorus.setText(cursorStanzaBody);
                chorus.setTypeface(typeface);
                chorusContainer.setVisibility(View.VISIBLE);
                continue;
            }
            stanzaNumber.setText(cursorStanzaNumber);
            stanzaBody.setText(cursorStanzaBody);
            stanzaNumber.setTypeface(typeface);
            stanzaBody.setTypeface(typeface);
            stanzaBody.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);

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

    private class AddFavoriteTask extends AsyncTask <Integer, Void, Integer> {
        static final int SHOW_ON = 1;
        static final int SHOW_OFF = 2;

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
                result = SHOW_ON;
            } else {
                values.putNull(HymnContract.HymnEntry.COLUMN_NAME_FAVOURITE);
                result = SHOW_OFF;
            }

            contentResolver.update(HymnContract.HymnEntry.CONTENT_URI, values, selection, selectionArgs);

//            Log.d(TAG, "YES - " + isNotFavorite);
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            mFavoritesIconType = result;
            getActivity().invalidateOptionsMenu();

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
