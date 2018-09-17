package org.eu.nveo.manonparle.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import org.eu.nveo.manonparle.model.Group;

@Dao
public interface GroupDao {
    @Insert
    long insert(Group group);

    @Query("SELECT * FROM `group` WHERE name = :name limit 1")
    Group byName( String name );

    @Query("DELETE FROM `group`")
    void deleteAll();

    @Query("SELECT * FROM `group` WHERE id = :id ")
    Group byId( long id );

    @Query("SELECT * FROM `group`")
    Group[] all();
}
