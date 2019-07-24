package org.eu.nveo.manonparle.model;

import android.arch.persistence.room.*;
import android.util.Log;
import org.eu.nveo.manonparle.R;
import android.content.Context;
import android.net.Uri;
import org.eu.nveo.manonparle.db.Database;
import org.eu.nveo.manonparle.db.DatabaseException;
import org.eu.nveo.manonparle.db.ManonDatabase;
import org.eu.nveo.manonparle.helper.AssetImporter;
import org.eu.nveo.manonparle.helper.ImageUtils;
import java.io.File;

@Entity(indices = {@Index("name")} )
public class Group {
    @Ignore
    private String tag = "Group";

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private long pictoImage;

    @Ignore
    public Group(){

    }

    public Group(long id, String name ){
        this.id = id;
        this.name = name;
        pictoImage = -1;
    }

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

    public long getImagePictoId(){
        if( pictoImage > 0 ) {
            return pictoImage;
        } else if( getNbPicto() > 0 ) {
            Picto i = getFirstPicto();
            Log.v( tag , i.getName() +":"+i.getId() );
            return i.getId();
        }
        return -1;
    }

    public long getPictoImage() {
        return pictoImage;
    }

    public void setPictoImage(long pictoImage) {
        this.pictoImage = pictoImage;
    }

    public int getNbPicto(){
        int nb = 0;
        try {
            nb = Database.getConnection().rpictogroup().countByGroupId( id );
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        return nb;
    }

    public Picto getFirstPicto(){
        Picto i = null;
        try {
            i = Database.getConnection().rpictogroup().firstGroupPicto( id );
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        return i;
    }

    public Uri getImageUri( Context ctx ){
        long imgId = getImagePictoId();
        File dir = AssetImporter.getDataFolder( ctx );
        if( imgId != -1 ) {
            File image = new File(dir, imgId + ".png");
            return Uri.fromFile( image );
        } else {
            int resId = R.mipmap.placeholder;
            return ImageUtils.resImageUri( resId, ctx );
        }
    }

    public boolean isEmpty(){
        ManonDatabase db = null;
        try {
            db = Database.getConnection();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        int nb = db.rpictogroup().countByGroupId( getId() );
        return nb == 0;
    }
}
