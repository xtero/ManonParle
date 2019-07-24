package org.eu.nveo.manonparle.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(
        indices = {
                @Index("rpictogroupId"),
                @Index("pictoId"),
                @Index("groupId"),
                @Index("packageId")
        }
)
public class PackageRPictoGroup {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long packageId;
    private long rpictogroupId;
    private long pictoId;
    private long groupId;

    public PackageRPictoGroup(){

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

    public long getRpictogroupId() {
        return rpictogroupId;
    }

    public void setRpictogroupId(long rpictogroupId) {
        this.rpictogroupId = rpictogroupId;
    }

    public long getPictoId() {
        return pictoId;
    }

    public void setPictoId(long pictoId) {
        this.pictoId = pictoId;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }
}
