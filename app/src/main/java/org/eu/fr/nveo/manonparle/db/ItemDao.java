package org.eu.fr.nveo.manonparle.db;

import android.arch.persistence.room.*;
import org.eu.fr.nveo.manonparle.model.Item;

@Dao
public interface ItemDao {
    @Insert
    long insertItem(Item item);

    @Query("SELECT * FROM Item WHERE name = :name ")
    Item itemByName(String name);

    @Delete
    void deleteItem(Item item);

    @Update
    void updateItem(Item item);
}
