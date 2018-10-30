package org.eu.nveo.manonparle;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.ImageButton;
import org.eu.nveo.manonparle.Activity.BaseActivity;
import org.eu.nveo.manonparle.helper.Barycentre;
import org.eu.nveo.manonparle.helper.RotationComputer;
import org.eu.nveo.manonparle.model.Picto;
import org.eu.nveo.manonparle.view.SquaredImageButton;
import org.eu.nveo.manonparle.db.Database;
import org.eu.nveo.manonparle.db.DatabaseException;
import org.eu.nveo.manonparle.db.ManonDatabase;

import static org.eu.nveo.manonparle.helper.Preferences.*;

public class DisplaySelection extends BaseActivity {

    private static final String tag = "Main";
    private Picto pictoLeft;
    private Picto pictoRight;
    private MediaPlayer mp;
    private Barycentre barycentre;
    private SquaredImageButton btnLeft;
    private SquaredImageButton btnRight;
    private int previousOrientation = -1;
    private SharedPreferences prefs;

    private final View.OnTouchListener btnHandler = new View.OnTouchListener() {
        @Override
        public boolean onTouch( View btn, MotionEvent event ) {
            Picto picto;
            Log.v( tag, btn.getContentDescription().toString() );
            if( btn.getContentDescription().toString().equals( "left" ) ) {
                Log.v(tag, "I set left picto");
                picto = pictoLeft;
            } else {
                Log.v(tag, "I set right picto");
                picto = pictoRight;
            }
            boolean isPlayed  = playAudio(picto);
            if( isPlayed )
                blinkButton( (ImageButton) btn );
            return true;
        }
    };

    private final View.OnTouchListener barycentreHandler = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            Log.v( tag, MotionEvent.actionToString( event.getAction() ) ) ;
            if( event.getAction() == MotionEvent.ACTION_DOWN ) {
                Log.v( tag, "Reset Barycentre" );
                barycentre = new Barycentre( getApplicationContext() );
            }

            barycentre.updateBarycentre( event );

            if( event.getAction() == MotionEvent.ACTION_UP ) {
                Log.v(tag, "Fire new event" );
                Float[] coords;
                if( Math.abs( prefs.getInt("skew_side", DEFAULT_SKEW_SIDE) ) == 1 ) {
                    coords = barycentre.getSkewed();
                } else {
                    coords = barycentre.get();
                }
                long now = 1;
                MotionEvent newEvent = MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, coords[0], coords[1], event.getMetaState() );
                findViewById(R.id.fullscreen_content).dispatchTouchEvent(newEvent);
                return false;
            }
            return true;
        }
    };
    private RotationComputer r;

    private boolean playAudio( Picto picto){
        if( picto.hasSound() && ! mp.isPlaying() ) {
            Log.v( tag, "Playing the new sound ");
            Uri audioUri = picto.getSoundUri( getBaseContext() );
            Log.v(tag, audioUri.toString() );
            if( mp != null ){
                mp.release();
            }
            mp = MediaPlayer.create( getBaseContext(), audioUri );
            mp.start();
            return true;
        } else {
            Log.v( tag, "Media Player already playing");
            return false;
        }
    }

    private void blinkButton( ImageButton btn ) {
        Log.v(tag, "Setting the animation");
        SharedPreferences prefs = getSharedPreferences( GLOBAL_PREFS, MODE_PRIVATE );
        int color = prefs.getInt("confirm_color", CONFIRM_COLOR );
        // Higher is faster, so we need to negate it
        int speed = ( 1500 - prefs.getInt( "confirm_speed", CONFIRM_SPEED ) ) + CONFIRM_BASE_SPEED ;
        ObjectAnimator anim = ObjectAnimator.ofInt(btn, "backgroundColor", Color.BLACK, color, Color.BLACK, color, Color.BLACK);
        anim.setDuration( speed );
        anim.setEvaluator( new ArgbEvaluator() );
        anim.setRepeatMode( ValueAnimator.RESTART );
        anim.setRepeatCount( 0 );
        anim.start();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_selection);

        prefs = getSharedPreferences( GLOBAL_PREFS, MODE_PRIVATE );

        Intent args = getIntent();
        String picto1 = args.getStringExtra("idLeft");
        String picto2 = args.getStringExtra("idRight");


        ManonDatabase db = null;
        try {
            db = Database.getConnection();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        pictoLeft = db.picto().byId( Long.parseLong(picto1) );
        pictoRight = db.picto().byId( Long.parseLong(picto2) );

        mp = new MediaPlayer();

        btnLeft = findViewById( R.id.btnLeft );
        btnRight = findViewById( R.id.btnRight );

        btnLeft.setImageURI( pictoLeft.getImageUri( getBaseContext() ) );
        btnRight.setImageURI( pictoRight.getImageUri( getBaseContext() ) );

        btnLeft.setOnTouchListener( btnHandler );
        btnRight.setOnTouchListener( btnHandler );
        findViewById( R.id.mth ).setOnTouchListener( barycentreHandler );
    }

    @Override
    protected void onPause(){
        if( mp != null ){
            mp.release();
        }
        mp = null;
        r.stop();
        r = null;
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if( r == null ) {
            r = new RotationComputer( (SensorManager) getSystemService(SENSOR_SERVICE)  ) {
                @Override
                public void onUpdate( float rotation) {
                    if( previousOrientation == Surface.ROTATION_270 ) {
                        if( rotation > 30 && rotation < 150 ) {
                            previousOrientation = Surface.ROTATION_90;
                            btnLeft.setRotation( 0 );
                            btnRight.setRotation( 0 );
                        }
                    } else {
                        if( rotation > -150 && rotation < -30 ) {
                            previousOrientation = Surface.ROTATION_270;
                            btnLeft.setRotation( 180 );
                            btnRight.setRotation( 180 );
                        }
                    }
                }
            };
        }
        if( mp == null ){
            mp = new MediaPlayer();
        }
    }
}
