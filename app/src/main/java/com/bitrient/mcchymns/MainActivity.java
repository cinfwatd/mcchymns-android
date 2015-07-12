package com.bitrient.mcchymns;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bitrient.mcchymns.adapter.NavigationDrawerAdapter;
import com.bitrient.mcchymns.fragment.FavoritesFragment;
import com.bitrient.mcchymns.fragment.HelpFragment;
import com.bitrient.mcchymns.fragment.HymnsFragment;
import com.bitrient.mcchymns.fragment.HymnsViewFragment;
import com.bitrient.mcchymns.fragment.SearchFragment;
import com.bitrient.mcchymns.fragment.dialog.GotoHymnDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements
        NavigationDrawerAdapter.ViewHolder.ClickListener, GotoHymnDialogFragment.HymnSelectionListener {

    @SuppressWarnings("unused")
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String SELECTED_MENU_ITEM = "selected_menu_item";

    Toolbar mToolbar;
    SearchView mSearchView;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private RecyclerView mDrawerRecycleView;

    private static final String KEY_TITLE = "mTitle";
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private HashMap<Integer, String> mMenuTitles;
    private NavigationDrawerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.preference, false);

        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

        mDrawerRecycleView = (RecyclerView) findViewById(R.id.navigation_recycler_view);
        mDrawerRecycleView.setHasFixedSize(true);

        mMenuTitles = new HashMap<>(5);
        mMenuTitles.put(0, getString(R.string.app_name));
        mMenuTitles.put(1, getString(R.string.favorites));
        mMenuTitles.put(2, getString(R.string.search));
        mMenuTitles.put(3, getString(R.string.settings));
        mMenuTitles.put(4, getString(R.string.help));

        final int menuIcons[] = {
                R.mipmap.ic_action_queue_music,
                R.mipmap.ic_action_badge,
                R.mipmap.ic_action_magnifier,
                R.mipmap.ic_action_sliders,
                R.mipmap.ic_action_bulb
        };
        mAdapter = new NavigationDrawerAdapter(mMenuTitles, menuIcons, this);

        mDrawerRecycleView.setAdapter(mAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        mDrawerRecycleView.setLayoutManager(layoutManager);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.open_navigation_drawer_description, R.string.close_navigation_drawer_desc) {
            /**
             * Called when a drawer has settled in a completely closed state
             * @param view
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);

                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                if (!mSearchView.isIconified()) {
                    mSearchView.setQuery(null, false);
                    mSearchView.setIconified(true);
                }

                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); //creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
                if (fragment != null) {
                    updateTitle(fragment);
                    toggleSelection();
                }
            }
        });

        mDrawerTitle = getString(R.string.navigation_drawer_title);
        if (savedInstanceState == null) {
            mAdapter.toggleSelection(0); // mark the first menu item as selected.

            final boolean startFavorites = getIntent().getBooleanExtra(EntryActivity.START_FAVORITES, false);

            HymnsFragment hymnsFragment = new HymnsFragment();
            if (startFavorites) {
                mTitle = getText(R.string.favorites);
                FavoritesFragment favoritesFragment =
                        new FavoritesFragment();

                // Adding the hymns fragment for backstack presence
                replaceFragment(hymnsFragment);
                replaceFragment(favoritesFragment);
            } else {
                mTitle = getText(R.string.app_name);
                replaceFragment(hymnsFragment);
            }
        } else {
            mTitle = savedInstanceState.getCharSequence(KEY_TITLE, getText(R.string.app_name));
        }
    }

    /**
     * Updates the toolbar/drawerlayout title to the backstack fragment (The fragment returned to)
     * @param fragment The fragment returned to.
     */
    private void updateTitle(Fragment fragment) {
        final String fragmentClassName = fragment.getClass().getName();
//        Log.d("TAG", "YES _ class name = " + fragmentClassName);

        if (fragmentClassName.equals(FavoritesFragment.class.getName())) {
            setTitle(getText(R.string.favorites));
        } else if (fragmentClassName.equals(HelpFragment.class.getName())) {
            setTitle(getText(R.string.help));
        } else if (fragmentClassName.equals(HymnsFragment.class.getName())) {
            setTitle(getText(R.string.app_name));
        } else if (fragmentClassName.equals(SearchFragment.class.getName())) {
            setTitle(R.string.search);
        }
    }

    private void replaceFragment(Fragment fragment) {
        final Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        final String backStateName = fragment.getClass().getName();
        final String fragmentTag = backStateName;

        final FragmentManager fragmentManager = getSupportFragmentManager();

        //if Main hymns fragment and not equal to current fragment, remove all from backstack and add it.
        if (backStateName.equals(HymnsFragment.class.getName())) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            transaction.replace(R.id.content_frame, fragment, fragmentTag);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {

            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            transaction.replace(R.id.content_frame, fragment, fragmentTag);
            transaction.addToBackStack(null);
            transaction.commit();

        }
    }

    /**
     * Replaces the current fragment with the passed-in fragment and also sets the title based on the position given
     * @see #replaceFragment(Fragment)
     * @param fragment The fragment to replace with
     * @param position The position of the menu Item. Used to get the title
     */
    private void replaceFragment(Fragment fragment, int position) {
        mTitle = mMenuTitles.get(position);
        replaceFragment(fragment);
    }

    /**
     * Replaces the current fragment with the passed-in fragment and also set the title
     * @see #replaceFragment(Fragment)
     * @param fragment The fragment to replace with
     * @param title The title to set
     */
    private void replaceFragment(Fragment fragment, CharSequence title) {
        mTitle = title;
        setTitle(mTitle);
        replaceFragment(fragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        return true;
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }

        if (!mSearchView.isIconified()) {
            mSearchView.setIconified(true);
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putCharSequence(KEY_TITLE, mTitle);
        outState.putIntegerArrayList(SELECTED_MENU_ITEM, new ArrayList<>(mAdapter.getSelectedItems()));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mAdapter.setSelectedItems(savedInstanceState.getIntegerArrayList(SELECTED_MENU_ITEM));
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onItemClicked(int position) {
        mDrawerLayout.closeDrawers();

        toggleSelection(position);

        switch (position) {
            case 0: // Favorites
                final HymnsFragment hymnsFragment
                        = new HymnsFragment();
                replaceFragment(hymnsFragment, position);

                break;

            case 1: // Favorites
                final FavoritesFragment favoritesFragment
                        = new FavoritesFragment();
                replaceFragment(favoritesFragment, position);

                break;
            case 2: // Advance Search
                final SearchFragment searchFragment
                        = new SearchFragment();
                replaceFragment(searchFragment, position);

                break;
            case 3: // Settings
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case 4: // Help
                final HelpFragment helpFragment = new HelpFragment();
                replaceFragment(helpFragment, position);
                break;
        }
    }

    /**
     * toggles the selection state of the menus. clear Selection before adding to
     * make the selection mutually exclusive.
     * @param position The position to toggle.
     */
    private void toggleSelection(int position) {
        if (position == 3) return; //ignore settings.
        mAdapter.clearSelection();
        mAdapter.toggleSelection(position);
    }

    private void toggleSelection() {
        toggleSelection(getKey(mTitle.toString()));
    }

    public int getKey(String value) {
        for (Map.Entry<Integer, String> e : mMenuTitles.entrySet()) {
            if (value.equals(e.getValue())) return e.getKey();
        }

        // put a non-existing position
        return 5;
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerRecycleView);

        if (mTitle.equals(getText(R.string.favorites)) ||
                mTitle.equals(getText(R.string.app_name))) {

            MenuItem menuSearch = menu.findItem(R.id.action_search);
            MenuItem menuSort = menu.findItem(R.id.action_sort);

            menuSearch.setVisible(!drawerOpen);
            menuSort.setVisible(!drawerOpen);
        }

        if (mTitle.equals(getText(R.string.favorites))) {
            MenuItem menuFav = menu.findItem(R.id.action_remove_all);
            menuFav.setVisible(!drawerOpen);
        }

        /**
         * Can't use the title (mTittle) as it changes.
         */
        final Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (currentFragment != null && currentFragment.getClass().getName()
                .equals(HymnsViewFragment.class.getName())) {
            menu.findItem(R.id.action_add_to_favorite).setVisible(!drawerOpen);
            menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void hymnSelected(int hymnNumber) {

        Bundle args = new Bundle();
        args.putInt(HymnsViewFragment.SELECTED_HYMN, hymnNumber);
        HymnsViewFragment hymnView =
                HymnsViewFragment.newInstance(args);
        replaceFragment(hymnView, getText(R.string.hymn));
    }
}
