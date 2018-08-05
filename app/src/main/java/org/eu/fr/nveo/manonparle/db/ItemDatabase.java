package org.eu.fr.nveo.manonparle.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import org.eu.fr.nveo.manonparle.model.Item;

@Database( entities = { Item.class }, version = 1, exportSchema = false )
public abstract class ItemDatabase extends RoomDatabase {

    public abstract ItemDao itemDao();
}
