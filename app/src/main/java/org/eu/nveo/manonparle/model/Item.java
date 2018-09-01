package org.eu.nveo.manonparle.model;

import android.arch.persistence.room.*;
import android.content.Context;
import android.net.Uri;
import java.io.File;

@Entity(
        indices = {
                @Index("name")
        }
        )
public class Item {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private Boolean hasSound;
    private Boolean soundSynth;

    public static String STORAGE_PATH = "item";


    public Item( long id, String name, Boolean hasSound ){
        this.id = id;
        this.name = name;
        this.hasSound = hasSound;
    }

    @Ignore
    public Item(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getHasSound() {
        return hasSound;
    }

    public void setHasSound(Boolean hasSound) {
        this.hasSound = hasSound;
    }

    public Boolean getSoundSynth() {
        return soundSynth;
    }

    public void setSoundSynth(Boolean soundSynth) {
        this.soundSynth = soundSynth;
    }


    public Uri getImageUri( Context ctx ){
        File dir = ctx.getDir( STORAGE_PATH, Context.MODE_PRIVATE );
        File image = new File( dir, id+".png");
        return Uri.fromFile( image );
    }

    public Uri getSoundUri( Context ctx ){
        if( ! hasSound ) {
            return null;
        }
        File dir = ctx.getDir( STORAGE_PATH, Context.MODE_PRIVATE );
        File image = new File( dir, id+".mp3");
        return Uri.fromFile( image );
    }

    @Ignore
    public boolean hasSound(){
            return hasSound;
    }

    /*
    */
}

