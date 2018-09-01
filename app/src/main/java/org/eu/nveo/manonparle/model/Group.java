package org.eu.nveo.manonparle.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(indices = {@Index("name")} )
public class Group {
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

    public long getItemImage() {
        return itemImage;
    }

    public void setItemImage(long itemImage) {
        this.itemImage = itemImage;
    }
}
