package com.bitrient.mcchymns.fragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bitrient.mcchymns.HymnViewActivity;
import com.bitrient.mcchymns.R;
import com.bitrient.mcchymns.database.HymnContract;
import com.bitrient.mcchymns.fragment.dialog.GotoHymnDialogFragment;


/**
 * A placeholder fragment containing a simple view.
 */
public class HymnViewActivityFragment extends Fragment {
    @SuppressWarnings("unused")
    private static final String TAG = HymnViewActivityFragment.class.getSimpleName();

    private int mFavoritesIconType = 0;

    public static HymnViewActivityFragment newInstance(Bundle args) {
        HymnViewActivityFragment hymnView = new HymnViewActivityFragment();
        hymnView.setArguments(args);

        return hymnView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(Integer.toString(getHymnNumber()));

        return inflater.inflate(R.layout.fragment_hymn_view, container, false);
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
                        = GotoHymnDialogFragment.newInstance();
                hymnDialogFragment.show(getFragmentManager(), "HymnDialogFragment");
                return true;

            case R.id.action_add_to_favorite:
                Toast.makeText(getActivity(), "Add " + getHymnNumber() + " to favorites", Toast.LENGTH_SHORT).show();
                new HymnTask().execute(getHymnNumber());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem favoriteMenuItem = menu.findItem(R.id.action_add_to_favorite);

        switch (mFavoritesIconType) {
            case HymnTask.SHOW_ON:
                favoriteMenuItem.setIcon(R.mipmap.ic_action_favorite_on);
                break;
            case HymnTask.SHOW_OFF:
                favoriteMenuItem.setIcon(R.mipmap.ic_action_favorite_off);
                break;
        }

        super.onPrepareOptionsMenu(menu);
    }

    private int getHymnNumber() {
        return getArguments().getInt(HymnViewActivity.SELECTED_HYMN, 40);
    }

    private class HymnTask extends AsyncTask <Integer, Void, Integer> {
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
}
