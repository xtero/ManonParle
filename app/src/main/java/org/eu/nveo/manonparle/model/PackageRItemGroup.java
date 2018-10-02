package org.eu.nveo.manonparle.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(
        indices = {
                @Index("ritemgroupId"),
                @Index("itemId"),
                @Index("groupId"),
                @Index("packageId")
        }
)
public class PackageRItemGroup {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long packageId;
    private long ritemgroupId;
    private long itemId;
    private long groupId;

    public PackageRItemGroup(){

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

    public long getRitemgroupId() {
        return ritemgroupId;
    }

    public void setRitemgroupId(long ritemgroupId) {
        this.ritemgroupId = ritemgroupId;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }
}
