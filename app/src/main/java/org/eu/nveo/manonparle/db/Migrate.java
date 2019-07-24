package org.eu.nveo.manonparle.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

public class Migrate {
    static final Migration MIGRATION_5_6 = new Migration( 5, 6 ) {

        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL( "CREATE TABLE `Repo` ( `id` INTEGER NOT NULL, `name` TEXT, `url` TEXT, `lastRefresh` INTEGER default NULL, PRIMARY KEY(`id`) )" );
        }
    };
}
