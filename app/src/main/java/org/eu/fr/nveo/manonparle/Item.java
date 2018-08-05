package org.eu.fr.nveo.manonparle;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class Item {

    @Getter @Setter
    String name;

    private TextToSpeech tts;
    private MediaPlayer mp;
    private String tag = "Item";
    private Resources res;
    private Context ctx;

    public int getImageId(){
        int id =  res.getIdentifier( name, "mipmap", ctx.getPackageName() ) ;
        Log.v( tag , Integer.toString( id ) );
        return id;
    }

    private int getSoundId(){
        int id = res.getIdentifier( name, "raw", ctx.getPackageName() ) ;
        Log.v( tag , Integer.toString( id ) );
        return id;
    }

    private boolean hasSound(){
        int id = getSoundId();
        if ( id != 0 ) {
            Log.v(tag, "Sound exists");
            return true;
        } else {
            Log.v(tag, "no sound ");
            return false;
        }
    }

    public boolean playAudio(){
        if( hasSound() ) {
            if( ! mp.isPlaying() ) {
                mp = MediaPlayer.create(ctx, getSoundId());
                mp.start();
                return true;
            } else {
                return false;
            }
        } else if( ! tts.isSpeaking() ) {
            String uid = UUID.randomUUID().toString();
            CharSequence text = name.subSequence( 0, name.length() - 1 );
            tts.speak( text, TextToSpeech.QUEUE_ADD, null, uid );
            return true;
        } else {
            return false;
        }
    }

    /*
    public void registerMediaPlayer( MediaPlayer mp ) {
        this.mp = mp;
    }

    public void registerTTS( TextToSpeech tts ) {
        this.tts = tts;
    }

    public Item( String name ){
        this(  name, null, null );
    }
    */

    public Item( String name, Context ctx, MediaPlayer mp, TextToSpeech tts ){
        this.mp = mp;
        this.tts = tts;
        this.name = name;
        this.ctx = ctx;
        this.res = ctx.getResources();
    }

}
