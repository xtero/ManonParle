package org.eu.nveo.manonparle.helper;

import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Fullscreen {

    private AppCompatActivity act;
    private final Handler mHideHandler = new Handler();

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {

            // Delayed removal of status and navigation bar
            ActionBar actionBar = act.getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            act.findViewById( android.R.id.content ).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    public void ensureFullscreen() {
        mHideHandler.post(mHideRunnable);
    }

    public Fullscreen( AppCompatActivity act){
        this.act = act;
    }
}
