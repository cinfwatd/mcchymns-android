package com.bitrient.mcchymns;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;

import com.bitrient.mcchymns.adapter.NavigationDrawerAdapter;
import com.bitrient.mcchymns.fragment.dialog.GotoHymnDialogFragment;


public class MainActivity extends AppCompatActivity implements
        NavigationDrawerAdapter.ViewHolder.ClickListener {

    @SuppressWarnings("unused")
    private static final String TAG = MainActivity.class.getSimpleName();

    Toolbar mToolbar;
    SearchView mSearchView;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.navigation_recycler_view);
        recyclerView.setHasFixedSize(true);

        final String TITLES[] = {
                getResources().getString(R.string.favorites),
                getResources().getString(R.string.search),
                getResources().getString(R.string.settings),
                getResources().getString(R.string.help)
        };

        final int ICONS[] = {
                R.mipmap.ic_action_badge,
                R.mipmap.ic_action_magnifier,
                R.mipmap.ic_action_sliders,
                R.mipmap.ic_action_bulb
        };
        RecyclerView.Adapter adapter = new NavigationDrawerAdapter(TITLES, ICONS, this);

        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.open_navigation_drawer_description, R.string.close_navigation_drawer_desc);

        mDrawerLayout.setDrawerListener(mDrawerToggle);
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
        mDrawerLayout.closeDrawers();

        switch (position) {
            case 0: // Favorites
                Intent favorites = new Intent(this, FavoritesActivity.class);
                startActivity(favorites);
                break;
            case 1: // Advance Search
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivity(searchIntent);
                break;
            case 2: // Settings
                break;
            case 3: // About
                break;
        }
    }
}
