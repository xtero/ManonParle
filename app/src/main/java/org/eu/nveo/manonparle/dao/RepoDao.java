package org.eu.nveo.manonparle.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import org.eu.nveo.manonparle.model.Repo;

@Dao
public interface RepoDao {
    @Query("SELECT * from Repo")
    Repo[] all();

    @Insert
    long insert( Repo r);

    @Update
    void update(Repo r );
}
