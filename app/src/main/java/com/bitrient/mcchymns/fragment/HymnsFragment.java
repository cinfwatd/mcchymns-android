package com.bitrient.mcchymns.fragment;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.bitrient.mcchymns.R;
import com.bitrient.mcchymns.adapter.HymnAdapter;
import com.bitrient.mcchymns.database.HymnContract;
import com.bitrient.mcchymns.fragment.dialog.GotoHymnDialogFragment;
import com.bitrient.mcchymns.fragment.dialog.SortDialog;
import com.bitrient.mcchymns.view.EmptiableRecyclerView;

public class HymnsFragment extends Fragment implements HymnAdapter.ViewHolder.ClickListener,
        LoaderManager.LoaderCallbacks<Cursor>, SortDialog.SortDialogListener {
    @SuppressWarnings("unused")
    private static final String TAG = FavoritesFragment.class.getSimpleName();
    private static final int LOADER_ID = 5;
    private final String QUERY_STRING = "queryString4";

    private EmptiableRecyclerView mRecyclerView;
    private SearchView mSearchView;

    private HymnAdapter mHymnAdapter;

    private CharSequence mCurrentFilter;
    private boolean mIsSearchViewOpen;
    private int mSortType = -1;

    private GotoHymnDialogFragment.HymnSelectionListener mSelectionListener;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_hymns, container, false);

        mRecyclerView = (EmptiableRecyclerView) rootView.findViewById(R.id.hymns_recycler_view);
        mRecyclerView.setEmptyView(rootView.findViewById(R.id.empty_hymns));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mHymnAdapter = new HymnAdapter(null, this);
        mRecyclerView.setAdapter(mHymnAdapter);

        setRecyclerViewLayoutManager(mRecyclerView);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        add goto hymn dialog button
        inflater.inflate(R.menu.menu_goto_hymn, menu);

        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        if (getActivity() != null) mSearchView.setQueryHint(getActivity().getResources().getString(R.string.search_hint));

        View searchPlate = mSearchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        searchPlate.setBackgroundResource(R.drawable.textfield_search_selected_dashed);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                searchView.onActionViewCollapsed();

                InputMethodManager inputMethodManager =
                        (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getActivity().findViewById(android.R.id.content).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                // hack used to tell the RecyclerView that its empty due to a search filter so that
                // it can respond with the appropriate background.
                mRecyclerView.setSearch(!TextUtils.isEmpty(newText));

                mCurrentFilter = !TextUtils.isEmpty(newText) ? newText : null;
                getLoaderManager().restartLoader(LOADER_ID, null, HymnsFragment.this);

                return false;
            }


        });

        if (mIsSearchViewOpen) {
            mSearchView.setIconified(false);
            mSearchView.setQuery(mCurrentFilter, false);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_goto:
//                Log.d(TAG, "YES - got to clicked.");
                GotoHymnDialogFragment hymnDialogFragment
                        = GotoHymnDialogFragment.newInstance();
                hymnDialogFragment.show(getFragmentManager(), "HymnDialogFragment");
                return true;

            case R.id.action_sort:
                final SortDialog sortDialog = new SortDialog();
                sortDialog.setTargetFragment(this, 1);
                sortDialog.show(getFragmentManager(), "sortDialog");
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mSearchView != null && !mSearchView.isIconified()) {
            outState.putCharSequence(QUERY_STRING, mSearchView.getQuery());

//            Log.d(TAG, "YES - Is iconified. - " + mSearchView.getQuery());
        }

//        Log.d(TAG, "YES - saved instance state called. ");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(QUERY_STRING)) {
            mCurrentFilter = savedInstanceState.getCharSequence(QUERY_STRING);
            mIsSearchViewOpen = true;
        }

        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mSelectionListener = (GotoHymnDialogFragment.HymnSelectionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                " must implement the HymnSelectionListener interface.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSelectionListener = null;
    }

    private void setRecyclerViewLayoutManager(EmptiableRecyclerView recyclerView) {
        int scrollPosition = 0;

//        If a layout manager has already been set, get current scroll position
        if (recyclerView.getLayoutManager() != null) {
            scrollPosition =
                    ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onItemClicked(int position) {

        if (!mSearchView.isIconified() && TextUtils.isEmpty(mSearchView.getQuery())) {
            mSearchView.setIconified(true);
            return;
        }

        final long itemNumber = mHymnAdapter.getItemNumber(position);
        mSelectionListener.hymnSelected((int) itemNumber);
    }

    @Override
    public boolean onItemLongClicked(int position) {
        return false;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri;

        String[] projection;
        // Pick the base URI to use depending on whether we are currently filtering.
        if (mCurrentFilter != null) {
            baseUri = Uri.withAppendedPath(HymnContract.HymnEntry.CONTENT_FILTER_FTS_URI, Uri.encode(mCurrentFilter.toString()));
        } else {
            baseUri = HymnContract.HymnEntry.CONTENT_FTS_URI;
        }

        projection = new String[] {
                HymnContract.StanzaEntry.COLUMN_NAME_HYMN_NUMBER,
                HymnContract.StanzaEntry.COLUMN_NAME_STANZA,
                HymnContract.StanzaEntry.COLUMN_NAME_STANZA_NUMBER,
                HymnContract.StanzaEntry._ID
        };

        String sortType;
        if (mSortType == SortDialog.SORT_BY_FIRST_LINES) {
            sortType = HymnContract.StanzaEntry.FIRST_LINES_SORT_ORDER;
        } else {
            sortType = HymnContract.StanzaEntry.DEFAULT_SORT_ORDER;
        }

        return new CursorLoader(getActivity(), baseUri, projection, null, null, sortType);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        final boolean isSearch = mCurrentFilter != null;

        mHymnAdapter.setIsSearch(isSearch);
        mHymnAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mHymnAdapter.swapCursor(null);
    }

    @Override
    public void onSortTypeSelected(int which) {
        mSortType = which;
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }
}
