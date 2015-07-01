package com.bitrient.mcchymns;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bitrient.mcchymns.fragment.SplashScreenActivityFragment;


public class SplashScreenActivity extends AppCompatActivity implements SplashScreenActivityFragment.SplashListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
    }

    @Override
    public void onLoadFinished() {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);

        finish();
    }
}
