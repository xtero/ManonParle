package org.eu.nveo.manonparle.dao;

import android.arch.persistence.room.Insert;
import org.eu.nveo.manonparle.model.Package;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

@Dao
public interface PackageDao {
    @Query("SELECT * from package where name = :name")
    Package packageByName( String name );

    @Query("SELECT * from package where id = :id")
    Package packageById( long id );

    @Insert
    long insertPackage( Package pack );
}
