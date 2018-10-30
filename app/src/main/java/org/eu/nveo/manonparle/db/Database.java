package org.eu.nveo.manonparle.db;

import android.arch.persistence.room.Room;
import android.content.Context;

public class Database {
    private static String dbname = "manon";
    private static String tag = "DB";
    private static ManonDatabase db;
    private static Context ctx;

    public static void empty(){
        db.rpictogroup().deleteAll();
        db.group().deleteAll();
        db.picto().deleteAll();
    }

    public static void initConnection( Context context ) {
        ctx = context.getApplicationContext();
        db = Room.databaseBuilder( ctx, ManonDatabase.class, dbname  )
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    public static ManonDatabase getConnection() throws DatabaseException {
        if( db == null ) {
            throw new DatabaseException("Database not initialized");
        }
        return db;
    }

}

