package org.eu.nveo.manonparle.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import org.eu.nveo.manonparle.model.Group;
import org.eu.nveo.manonparle.model.Item;
import org.eu.nveo.manonparle.model.RItemGroup;

@Database( entities = { Item.class, Group.class , RItemGroup.class }, version = 3, exportSchema = true )
public abstract class ItemDatabase extends RoomDatabase {

    public abstract ItemDao item();

    public abstract GroupDao group();

    public abstract RItemGroupDao ritemgroup();
}
