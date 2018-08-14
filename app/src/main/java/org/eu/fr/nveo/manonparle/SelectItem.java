package org.eu.fr.nveo.manonparle;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.ImageButton;
import org.eu.fr.nveo.manonparle.Helper.Barycentre;
import org.eu.fr.nveo.manonparle.Helper.Fullscreen;
import org.eu.fr.nveo.manonparle.Helper.RotationComputer;
import org.eu.fr.nveo.manonparle.View.SquaredImageButton;
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
public class SelectItem extends AppCompatActivity {

    private static final String tag = "Main";
    private Item itemLeft;
    private Item itemRight;
    private TextToSpeech tts;
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
        if( item.hasSound() ) {
            Log.v( tag, "The item " + item.getName() + " has sound");
            if( ! mp.isPlaying() ) {
                Log.v( tag, "Playing the new sound ");
                mp = MediaPlayer.create( getBaseContext(), item.getSoundUri( getBaseContext() ) );
                mp.start();
                return true;
            } else {
                Log.v( tag, "Media Player already playing");
                return false;
            }
        } else if( ! tts.isSpeaking() ) {
            Log.v( tag, "Reading a new text with TTS" );
            String uid = UUID.randomUUID().toString();
            String name = item.getName();
            CharSequence text = name.subSequence( 0, name.length() );
            tts.speak( text, TextToSpeech.QUEUE_FLUSH, null, uid );
            return true;
        } else {
            Log.v( tag, "Status of tts : " + tts.isSpeaking() );
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

        setContentView(R.layout.activity_selectitem);

        ItemDatabase db = Room.databaseBuilder( this.getBaseContext(), ItemDatabase.class , "manon")
                .allowMainThreadQueries()
                .addCallback( dbCreate )
                .build();

        ItemDao dao = db.itemDao();

        itemLeft  = dao.itemByName( item1 );
        itemRight = dao.itemByName( item2 );

        fs = new Fullscreen( this );

        tts = new TextToSpeech( this.getBaseContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if( status == TextToSpeech.SUCCESS ) {
                    Locale fr = new Locale("fr");
                    tts.setLanguage(fr);
                }
            }
        });

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
        tts.shutdown();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        fs.ensureFullscreen( 100 );
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
        fs.ensureFullscreen( 100 );
    }
}
