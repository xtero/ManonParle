package org.eu.nveo.manonparle.db;

import android.arch.persistence.room.Room;
import android.content.Context;

public class Database {
    private static String dbname = "manon";
    private static String tag = "DB";
    private static ItemDatabase db;
    private static Context ctx;

    public static void empty(){
        db.ritemgroup().deleteAll();
        db.group().deleteAll();
        db.item().deleteAll();
    }

    public static void initConnection( Context context ) {
        ctx = context.getApplicationContext();
        db = Room.databaseBuilder( ctx, ItemDatabase.class, dbname  )
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    public static ItemDatabase getConnection() throws DatabaseException {
        if( db == null ) {
            throw new DatabaseException("Database not initialized");
        }
        return db;
    }

}

