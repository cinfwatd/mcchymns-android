package com.bitrient.mcchymns;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bitrient.mcchymns.fragment.FavouritesFragment;
import com.bitrient.mcchymns.fragment.HymnsFragment;

public class HomeActivity extends AppCompatActivity implements
        HymnDialerDialogFragment.HymnDialerInteractionListener, HymnActivity.HymnSelectionListener{

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    displayNavigationItem(R.id.navigation_home);
                    return true;
                case R.id.navigation_favourites:
                    displayNavigationItem(R.id.navigation_favourites);
                    return true;
                case R.id.navigation_settings:
                    displayNavigationItem(R.id.navigation_settings);
                    return false;
                case R.id.navigation_hymn_dialer:
                    HymnDialerDialogFragment dialerDialogFragment = new HymnDialerDialogFragment();
                    dialerDialogFragment.show(getSupportFragmentManager(), dialerDialogFragment.getTag());
                    return false;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final View hymnDialerBottomSheet = findViewById(R.id.hymn_dialer_bottom_sheet);
        final BottomSheetBehavior dialerSheetBehavior = BottomSheetBehavior.from(hymnDialerBottomSheet);
        dialerSheetBehavior.setHideable(true);
        dialerSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (savedInstanceState == null) {
            final boolean startFavorites = getIntent().getBooleanExtra(EntryActivity.START_FAVORITES, false);
            if (startFavorites) {

                mOnNavigationItemSelectedListener.onNavigationItemSelected(navigation.getMenu().findItem(R.id.navigation_favourites));
//                navigation.set(R.id.nav_favourites);
            } else {

                mOnNavigationItemSelectedListener.onNavigationItemSelected(navigation.getMenu().findItem(R.id.navigation_home));
//                navigationView.setCheckedItem(R.id.nav_hymns);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void displayNavigationItem(int id) {

        Fragment fragment = null;
        switch (id) {
            case R.id.navigation_home:
                fragment = new HymnsFragment();
                break;
            case R.id.navigation_favourites:
                fragment = new FavouritesFragment();
                break;
            case R.id.navigation_settings:
                Intent settingsIntent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
        }

        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, fragment);
            transaction.commit();
        }
    }

    @Override
    public void onHymnDialerInteraction(int number) {
        openHymn(number);
    }

    @Override
    public void onHymnSelectedInteraction(int number) {
        openHymn(number);
    }

    private void openHymn(int number) {
        Bundle args = new Bundle();
        args.putInt(HymnActivity.SELECTED_HYMN, number);
        Intent intent = new Intent(this, HymnActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }
}
