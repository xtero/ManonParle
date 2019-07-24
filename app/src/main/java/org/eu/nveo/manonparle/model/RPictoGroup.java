package org.eu.nveo.manonparle.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity (
        foreignKeys = {
                @ForeignKey( entity = Group.class, parentColumns = "id", childColumns = "groupId" ),
                @ForeignKey( entity = Picto.class, parentColumns = "id", childColumns = "pictoId" )
        },
        indices = {
            @Index("groupId"),
            @Index("pictoId")
        }
)
public class RPictoGroup {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long groupId;
    private long pictoId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public long getPictoId() {
        return pictoId;
    }

    public void setPictoId(long pictoId) {
        this.pictoId = pictoId;
    }

}
