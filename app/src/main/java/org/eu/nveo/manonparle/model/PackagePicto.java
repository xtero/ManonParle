package org.eu.nveo.manonparle.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(
        indices = {
                @Index("pictoName"),
                @Index("pictoId"),
                @Index("packageId")
        }
)
public class PackagePicto {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long packageId;
    private long pictoId;
    private String pictoName;

    public PackagePicto(){

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPackageId() {
        return packageId;
    }

    public void setPackageId(long packageId) {
        this.packageId = packageId;
    }

    public long getPictoId() {
        return pictoId;
    }

    public void setPictoId(long pictoId) {
        this.pictoId = pictoId;
    }

    public String getPictoName() {
        return pictoName;
    }

    public void setPictoName(String pictoName) {
        this.pictoName = pictoName;
    }
}
