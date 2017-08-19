package com.bitrient.mcchymns;

import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/email");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"mcchymns@bitrient.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Android Feedback");
        StringBuilder textBuilder = new StringBuilder("Device Info");
        char divider = '\n';
        String separator = "-------------------";

        Display display = this.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);



        textBuilder.append(divider).append(separator);
        textBuilder.append(divider);
        textBuilder.append("VERSION --- ").append(Build.VERSION.SDK_INT);
        textBuilder.append(divider);
        textBuilder.append("MANUFACTURER --- ").append(Build.MANUFACTURER);
        textBuilder.append(divider);
        textBuilder.append("MODEL --- ").append(Build.MODEL);
        textBuilder.append(divider);
        textBuilder.append("DISPLAY --- ").append(Build.DISPLAY);
        textBuilder.append(divider);
        textBuilder.append("DISPLAY SIZE --- ").append(size.x).append(" X ").append(size.y);
        textBuilder.append(divider);
        textBuilder.append(separator).append(divider).append("Put feedback here ...");

        emailIntent.putExtra(Intent.EXTRA_TEXT, textBuilder.toString());

        startActivity(Intent.createChooser(emailIntent, "Send Feedback"));

        finish();
    }
}
