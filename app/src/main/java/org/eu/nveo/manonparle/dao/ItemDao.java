package org.eu.nveo.manonparle.dao;

import android.arch.persistence.room.*;
import org.eu.nveo.manonparle.model.Item;

@Dao
public interface ItemDao {
    @Insert
    long insert(Item item);

    @Query("SELECT * FROM Item WHERE name = :name ")
    Item byName(String name);

    @Query("SELECT count(*) FROM Item")
    int countAll();

    @Query("SELECT * from item WHERE id = :id")
    Item byId( long id );

    @Query("SELECT * from item")
    Item[] all();

    @Query("SELECT i.* FROM item as i, ritemgroup as rig WHERE i.id = rig.itemId and rig.groupId = :groupId ")
    Item[] byGroupId( long groupId );

    @Query("SELECT count(*) FROM ritemgroup WHERE ritemgroup.groupId = :groupId ")
    int countByGroupId( long groupId );

    @Query("SELECT item.* FROM item, RItemGroup AS link WHERE link.groupId = :groupId AND link.itemId == item.id AND item.name LIKE :constrain")
    Item[] byGroupIdLike( long groupId, String constrain );

    @Query("DELETE FROM item")
    void deleteAll();

    @Delete
    void delete(Item item);

    @Update
    void update(Item item);
}
