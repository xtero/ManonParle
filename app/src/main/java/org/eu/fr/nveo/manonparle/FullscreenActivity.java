package org.eu.fr.nveo.manonparle;

import android.annotation.SuppressLint;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import org.eu.fr.nveo.manonparle.model.Item;
import org.eu.fr.nveo.manonparle.db.ItemDao;
import org.eu.fr.nveo.manonparle.db.ItemDatabase;

import java.io.*;
import java.util.Locale;
import java.util.UUID;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    private static final String tag = "MyActivity";
    private Item itemLeft;
    private Item itemRight;
    private TextToSpeech tts;
    private MediaPlayer mp;

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

    private final View.OnTouchListener btnHandler = new View.OnTouchListener() {
        @Override
        public boolean onTouch( View btn, MotionEvent event ) {
            btn.performClick();
            Item item;
            Log.v( tag, btn.getContentDescription().toString() );
            if( btn.getContentDescription().toString().equals( "left" ) ) {
                Log.v(tag, "I set left item");
                item = itemLeft;
            } else {
                Log.v(tag, "I set right item");
                item = itemRight;
            }
            return playAudio( item );
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

    public boolean playAudio( Item item ){
        if( item.hasSound() ) {
            if( ! mp.isPlaying() ) {
                mp = MediaPlayer.create( getBaseContext(), item.getSoundUri( getBaseContext() ) );
                mp.start();
                return true;
            } else {
                return false;
            }
        } else if( ! tts.isSpeaking() ) {
            String uid = UUID.randomUUID().toString();
            String name = item.getName();
            CharSequence text = name.subSequence( 0, name.length() - 1 );
            tts.speak( text, TextToSpeech.QUEUE_ADD, null, uid );
            return true;
        } else {
            return false;
        }
    }

    private void copyFile( FileInputStream from, FileOutputStream to ) throws IOException {
        byte[] buffer = new byte[1024];
        int length = 0;
        while( ( length = from.read( buffer ) ) > 0 ) {
            to.write( buffer,0 , length );
        }
    }

    RoomDatabase.Callback dbCreate = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            Log.v(tag, "Starting importing datas");
            AssetFileDescriptor image;
            AssetFileDescriptor audio;


            String [] list = new String[0];
            try {
                list = getAssets().list("items");
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (String item : list) {
                Log.v( tag, "Importing "+item );
                image = null;
                audio = null;
                String [] files = new String[0];
                try {
                    files = getAssets().list( "items/"+item );
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (String file : files) {
                    Log.v( tag, "Cheking file '"+ file +"'");
                    if ( file.matches(".*png$") ) {
                        Log.v( tag, "Loading image");
                        try {
                            image = getAssets().openFd( "items/"+item + "/" + file );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if( file.matches( ".*mp3$") ) {
                        Log.v( tag, "Loading audio");
                        try {
                            audio = getAssets().openFd( "items/" + item + "/" +  file );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                int hasSound = audio == null ? 0 : 1;
                String query  = "INSERT INTO item ( name, hasSound ) VALUES ( '"+item+"' , "+hasSound+" )";
                Log.v( tag, "Running query : " + query );
                db.execSQL( query );
                Cursor res = db.query("SELECT max(id) FROM item");
                res.moveToFirst();
                long id = res.getLong(0);
                Log.v( tag, "Created under id " + Long.toString( id ) );

                String path = "item";
                File dataFolder = getBaseContext().getDir( path, Context.MODE_PRIVATE );
                try {
                    Log.v(tag, "Copy image");
                    FileInputStream fis = image.createInputStream();
                    FileOutputStream fos = new FileOutputStream( new File( dataFolder , Long.toString(id)+".png" ) );
                    copyFile( fis, fos );
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if( hasSound == 1 ) {
                    try {
                        Log.v(tag, "Copy audio");
                        FileInputStream fis = audio.createInputStream();
                        FileOutputStream fos = new FileOutputStream( new File( dataFolder , Long.toString(id)+".mp3" ) );
                        copyFile( fis, fos );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            Log.v(tag, "Import complete" );
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ItemDatabase db = Room.databaseBuilder( this.getBaseContext(), ItemDatabase.class , "manon")
                .allowMainThreadQueries()
                .addCallback( dbCreate )
                .build();
        ItemDao dao = db.itemDao();
        setContentView(R.layout.activity_fullscreen);

        mContentView = findViewById(R.id.fullscreen_content);
        tts = new TextToSpeech( this.getBaseContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                Locale fr = new Locale( "fr" );
                tts.setLanguage( fr );
            }
        });
        mp = new MediaPlayer();

        itemLeft = dao.itemByName("yaourt");
        itemRight = dao.itemByName("doudou");
        ImageButton btnLeft = findViewById( R.id.btnLeft );
        ImageButton btnRight = findViewById( R.id.btnRight );
        btnLeft.setImageURI( itemLeft.getImageUri( getBaseContext() ) );
        btnRight.setImageURI( itemRight.getImageUri( getBaseContext() ) );
        btnLeft.setOnTouchListener( btnHandler );
        btnRight.setOnTouchListener( btnHandler );
        findViewById( R.id.mth ).setOnTouchListener( barycentreHandler );
    }

    @Override
    protected void onDestroy(){
        mp.release();
        tts.shutdown();
        super.onDestroy();
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
