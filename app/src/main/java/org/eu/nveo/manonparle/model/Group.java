package org.eu.nveo.manonparle.model;

import android.arch.persistence.room.*;
import android.content.ContentResolver;
import android.util.Log;
import org.eu.nveo.manonparle.R;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import org.eu.nveo.manonparle.db.Database;
import org.eu.nveo.manonparle.db.DatabaseException;
import org.eu.nveo.manonparle.helper.ImageUtils;

import java.io.File;

import static org.eu.nveo.manonparle.model.Item.STORAGE_PATH;

@Entity(indices = {@Index("name")} )
public class Group {
    @Ignore
    private String tag = "Group";

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private long itemImage;

    @Ignore
    public Group(){

    }

    public Group(long id, String name ){
        this.id = id;
        this.name = name;
        itemImage = -1;
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

    public long getImageItemId(){
        if( itemImage > 0 ) {
            return itemImage;
        } else if( getNbItem() > 0 ) {
            Item i = getFirstItem();
            Log.v( tag , i.getName() +":"+i.getId() );
            return i.getId();
        }
        return -1;
    }

    public long getItemImage() {
        return itemImage;
    }

    public void setItemImage(long itemImage) {
        this.itemImage = itemImage;
    }

    public int getNbItem(){
        int nb = 0;
        try {
            nb = Database.getConnection().ritemgroup().countByGroupId( id );
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        return nb;
    }

    public Item getFirstItem(){
        Item i = null;
        try {
            i = Database.getConnection().ritemgroup().firstGroupItem( id );
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        return i;
    }

    public Uri getImageUri( Context ctx ){
        long imgId = getImageItemId();
        File dir = ctx.getDir( STORAGE_PATH, Context.MODE_PRIVATE );
        if( imgId != -1 ) {
            File image = new File(dir, imgId + ".png");
            return Uri.fromFile( image );
        } else {
            int resId = R.mipmap.placeholder;
            return ImageUtils.resImageUri( resId, ctx );
        }
    }
}
