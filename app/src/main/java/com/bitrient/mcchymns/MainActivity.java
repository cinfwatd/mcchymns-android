package com.bitrient.mcchymns;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;

import com.bitrient.mcchymns.adapter.NavigationDrawerAdapter;
import com.bitrient.mcchymns.fragment.FavoritesActivityFragment;
import com.bitrient.mcchymns.fragment.HymnsFragment;


public class MainActivity extends AppCompatActivity implements
        NavigationDrawerAdapter.ViewHolder.ClickListener {

    @SuppressWarnings("unused")
    private static final String TAG = MainActivity.class.getSimpleName();

    Toolbar mToolbar;
    SearchView mSearchView;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private RecyclerView mDrawerRecycleView;

    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private String mMenuTitles[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.preference, false);

        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

        mDrawerRecycleView = (RecyclerView) findViewById(R.id.navigation_recycler_view);
        mDrawerRecycleView.setHasFixedSize(true);

        mMenuTitles = new String[]{
                getResources().getString(R.string.favorites),
                getResources().getString(R.string.search),
                getResources().getString(R.string.settings),
                getResources().getString(R.string.help)
        };

        final int menuIcons[] = {
                R.mipmap.ic_action_badge,
                R.mipmap.ic_action_magnifier,
                R.mipmap.ic_action_sliders,
                R.mipmap.ic_action_bulb
        };
        RecyclerView.Adapter adapter = new NavigationDrawerAdapter(mMenuTitles, menuIcons, this);

        mDrawerRecycleView.setAdapter(adapter);
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

                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); //creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerTitle = getString(R.string.navigation_drawer_title);
        if (savedInstanceState == null) {
            mTitle = getString(R.string.app_name);
            HymnsFragment hymnsFragment = new HymnsFragment();
            replaceFragment(hymnsFragment);
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    /**
     * Replaces the current fragment with the passed-in fragment and also sets the title based on the position given
     * @see #replaceFragment(Fragment)
     * @param fragment The fragment to replace with
     * @param position The position of the menu Item. Used to get the title
     */
    private void replaceFragment(Fragment fragment, int position) {
        mTitle = mMenuTitles[position];
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

        super.onBackPressed();
    }

    @Override
    public void onItemClicked(int position) {
        switch (position) {
            case 0: // Favorites
                FavoritesActivityFragment favoritesFragment
                        = new FavoritesActivityFragment();
                replaceFragment(favoritesFragment, position);

                break;
            case 1: // Advance Search
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivity(searchIntent);
                break;
            case 2: // Settings
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case 3: // Help
                Intent helpIntent = new Intent(this, HelpActivity.class);
                startActivity(helpIntent);
                break;
        }

        mDrawerLayout.closeDrawers();
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
            menu.findItem(R.id.action_search).setVisible(!drawerOpen);
            menu.findItem(R.id.action_sort).setVisible(!drawerOpen);
        }

        if (mTitle.equals(getText(R.string.favorites))) {
            menu.findItem(R.id.action_remove_all).setVisible(!drawerOpen);
        }

        return super.onPrepareOptionsMenu(menu);
    }
}
