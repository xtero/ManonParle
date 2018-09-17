package org.eu.nveo.manonparle.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity (
        foreignKeys = {
                @ForeignKey( entity = Group.class, parentColumns = "id", childColumns = "groupId" ),
                @ForeignKey( entity = Item.class, parentColumns = "id", childColumns = "itemId" )
        },
        indices = {
                @Index("groupId"),
            @Index("itemId")
        }
)
public class RItemGroup {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long groupId;
    private long itemId;

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

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

}
