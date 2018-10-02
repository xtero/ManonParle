package org.eu.nveo.manonparle.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import org.eu.nveo.manonparle.dao.*;
import org.eu.nveo.manonparle.model.*;
import org.eu.nveo.manonparle.model.Package;

@Database(
        entities = {
                Item.class,
                Group.class ,
                RItemGroup.class,
                Package.class,
                PackageItem.class,
                PackageGroup.class,
                PackageRItemGroup.class
        },
        version = 4,
        exportSchema = true
)
public abstract class ItemDatabase extends RoomDatabase {

    public abstract ItemDao item();

    public abstract GroupDao group();

    public abstract RItemGroupDao ritemgroup();

    public abstract PackageDao pack();

    public abstract PackageItemDao packitem();

    public abstract PackageGroupDao packgroup();

    public abstract PackageRItemGroupDao packritemgroup();
}
