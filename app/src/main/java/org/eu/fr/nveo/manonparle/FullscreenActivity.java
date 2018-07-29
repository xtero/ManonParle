package org.eu.fr.nveo.manonparle;

import android.annotation.SuppressLint;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import java.util.Locale;
import java.util.UUID;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    private static final String tag = "MyActivity";

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    private TextToSpeech tts;

    private final View.OnTouchListener btnHandler = new View.OnTouchListener() {
        @Override
        public boolean onTouch( View btn, MotionEvent event ) {
            ImageButton imgBtn = (ImageButton) btn;
            CharSequence text = imgBtn.getContentDescription();
            Log.v(tag, text.toString());
            String uid = UUID.randomUUID().toString();
            if (!tts.isSpeaking()) {
                tts.speak(text, TextToSpeech.QUEUE_ADD, null, uid);
            }
            return true;
        }
    };

    private int pointerMax = 0;
    private Barycentre barycentre;
    private final View.OnTouchListener barycentreHandler = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int nbPointer = event.getPointerCount();

            Log.v( tag, MotionEvent.actionToString( event.getAction() ) ) ;
            if( event.getAction() == MotionEvent.ACTION_DOWN ) {
                Log.v( tag, "Reset Barycentre" );
                pointerMax = 0;
                barycentre = new Barycentre();
            }

            if( nbPointer >= pointerMax ) {
                pointerMax = nbPointer;
                int i = 0;
                int barySize = barycentre.nbCoords();
                while( i < nbPointer ) {
                    float x = event.getX( i );
                    float y = event.getY( i );
                    if( i == barySize ) {
                        barycentre.add( x, y );
                        barySize = barycentre.nbCoords();
                    } else {
                        try {
                            barycentre.set( i , x, y  );
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    i++;
                }
            }

            if( event.getAction() == MotionEvent.ACTION_UP ) {
                MotionEvent.PointerCoords coords = barycentre.get();
                float x = coords.getAxisValue(MotionEvent.AXIS_X);
                float y = coords.getAxisValue(MotionEvent.AXIS_Y);
                long now = 1;
                Log.v(tag, "Fire new event" );
                MotionEvent newEvent = MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, x, y, event.getMetaState() );
                findViewById(R.id.fullscreen_content).dispatchTouchEvent(newEvent);
                return false;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mContentView = findViewById(R.id.fullscreen_content);
        tts = new TextToSpeech( this.getBaseContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                Locale fr = new Locale( "fr" );
                tts.setLanguage( fr );
            }
        });
        findViewById( R.id.btn1 ).setOnTouchListener( btnHandler );
        findViewById( R.id.btn2 ).setOnTouchListener( btnHandler );
        findViewById( R.id.mth ).setOnTouchListener( barycentreHandler );
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    protected void onResume() {
        super.onResume();

        delayedHide(100);
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

}
