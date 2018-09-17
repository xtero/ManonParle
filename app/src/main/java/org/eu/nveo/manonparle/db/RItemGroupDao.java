package org.eu.nveo.manonparle.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import org.eu.nveo.manonparle.model.Item;
import org.eu.nveo.manonparle.model.RItemGroup;

@Dao
public interface RItemGroupDao {
    @Insert
    long insert(RItemGroup rItemGroup);

    @Query("DELETE from ritemgroup")
    void deleteAll();

    @Query("SELECT count(*) FROM ritemgroup WHERE groupId = :id ")
    int countByGroupId( long id);

    @Query("SELECT item.* FROM item, ritemgroup WHERE item.id = ritemgroup.itemId AND ritemgroup.groupId = :id ORDER BY item.id LIMIT 1")
    Item firstGroupItem(long id );

}
