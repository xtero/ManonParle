package org.eu.nveo.manonparle;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.ImageButton;
import org.eu.nveo.manonparle.Helper.Barycentre;
import org.eu.nveo.manonparle.Helper.Fullscreen;
import org.eu.nveo.manonparle.Helper.RotationComputer;
import org.eu.nveo.manonparle.View.SquaredImageButton;
import org.eu.nveo.manonparle.db.Database;
import org.eu.nveo.manonparle.db.DatabaseException;
import org.eu.nveo.manonparle.model.Item;
import org.eu.nveo.manonparle.db.ItemDao;
import org.eu.nveo.manonparle.db.ItemDatabase;

public class SelectItem extends AppCompatActivity {

    private static final String tag = "Main";
    private Item itemLeft;
    private Item itemRight;
    private MediaPlayer mp;
    private Fullscreen fs;
    private Barycentre barycentre;
    private SquaredImageButton btnLeft;
    private SquaredImageButton btnRight;
    private String item1 = "yaourt";
    private String item2 = "doudou";
    private int previousOrientation = -1;
    private ObjectAnimator anim;

    private final View.OnTouchListener btnHandler = new View.OnTouchListener() {
        @Override
        public boolean onTouch( View btn, MotionEvent event ) {
            Item item;
            Log.v( tag, btn.getContentDescription().toString() );
            if( btn.getContentDescription().toString().equals( "left" ) ) {
                Log.v(tag, "I set left item");
                item = itemLeft;
            } else {
                Log.v(tag, "I set right item");
                item = itemRight;
            }
            boolean isPlayed  = playAudio( item );
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
                barycentre = new Barycentre();
            }

            barycentre.updateBarycentre( event );

            if( event.getAction() == MotionEvent.ACTION_UP ) {
                Log.v(tag, "Fire new event" );
                MotionEvent.PointerCoords coords = barycentre.get();
                float x = coords.getAxisValue(MotionEvent.AXIS_X);
                float y = coords.getAxisValue(MotionEvent.AXIS_Y);
                long now = 1;
                MotionEvent newEvent = MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, x, y, event.getMetaState() );
                findViewById(R.id.fullscreen_content).dispatchTouchEvent(newEvent);
                return false;
            }
            return true;
        }
    };
    private RotationComputer r;

    private boolean playAudio( Item item ){
        if( item.hasSound() && ! mp.isPlaying() ) {
            Log.v( tag, "Playing the new sound ");
            Uri audioUri = item.getSoundUri( getBaseContext() );
            Log.v(tag, audioUri.toString() );
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
        anim = ObjectAnimator.ofInt( btn, "backgroundColor" , Color.BLACK, Color.rgb( 200,200,200), Color.BLACK, Color.rgb( 200,200,200), Color.BLACK  );
        anim.setDuration(1000);
        anim.setEvaluator( new ArgbEvaluator() );
        anim.setRepeatMode( ValueAnimator.RESTART );
        anim.setRepeatCount( 0 );
        anim.start();
    }



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent args = getIntent();
        item1 = args.getStringExtra("idLeft");
        item2 = args.getStringExtra("idRight");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_selectitem);

        ItemDatabase db = null;
        try {
            db = Database.getConnection();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        ItemDao dao = db.itemDao();

        itemLeft  = dao.item( Long.parseLong(item1) );
        itemRight = dao.item( Long.parseLong(item2) );

        fs = new Fullscreen( this );

        mp = new MediaPlayer();

        btnLeft = findViewById( R.id.btnLeft );
        btnRight = findViewById( R.id.btnRight );

        btnLeft.setImageURI( itemLeft.getImageUri( getBaseContext() ) );
        btnRight.setImageURI( itemRight.getImageUri( getBaseContext() ) );

        btnLeft.setOnTouchListener( btnHandler );
        btnRight.setOnTouchListener( btnHandler );
        findViewById( R.id.mth ).setOnTouchListener( barycentreHandler );
    }



    @Override
    protected void onDestroy(){
        mp.release();
        r.stop();
        r = null;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        fs.ensureFullscreen();
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
        fs.ensureFullscreen();
    }
}
