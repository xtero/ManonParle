package org.eu.nveo.manonparle.model;

import android.arch.persistence.room.*;
import android.content.Context;
import android.net.Uri;
import org.eu.nveo.manonparle.db.DatabaseException;
import org.eu.nveo.manonparle.db.ItemDatabase;
import org.eu.nveo.manonparle.helper.AssetImporter;

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
    private String audioExt;
    private String imageExt;

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

    public String getAudioExt() {
        return audioExt;
    }

    public void setAudioExt(String audioExt) {
        this.audioExt = audioExt;
    }

    public String getImageExt() {
        return imageExt;
    }

    public void setImageExt(String imageExt) {
        this.imageExt = imageExt;
    }



    public Uri getImageUri( Context ctx ){
        File dir = AssetImporter.getDataFolder( ctx );
        File image = new File( dir, id+"." + imageExt);
        return Uri.fromFile( image );
    }

    public Uri getSoundUri( Context ctx ){
        if( ! hasSound ) {
            return null;
        }
        File dir = AssetImporter.getDataFolder( ctx );
        File image = new File( dir, id+"." + audioExt );
        return Uri.fromFile( image );
    }

    @Ignore
    public boolean hasSound(){
            return hasSound;
    }

    public void delete(){
        ItemDatabase db = null;
        try {
            db = org.eu.nveo.manonparle.db.Database.getConnection();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        RItemGroup[] links = db.ritemgroup().byItemId( getId() );
        for (RItemGroup link : links) {
            db.ritemgroup().delete( link );
        }
        db.item().delete( this );
    }


    public int countGroupIn(){
        ItemDatabase db = null;
        try {
            db = org.eu.nveo.manonparle.db.Database.getConnection();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        return db.ritemgroup().countByItemId( getId() );

    }
}

