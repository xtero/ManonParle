package org.eu.nveo.manonparle.db;

import android.arch.persistence.room.*;
import org.eu.nveo.manonparle.model.Group;
import org.eu.nveo.manonparle.model.Item;
import org.eu.nveo.manonparle.model.RItemGroup;

@Dao
public interface ItemDao {
    @Insert
    long insertItem(Item item);

    @Query("SELECT * FROM Item WHERE name = :name ")
    Item itemByName(String name);

    @Query("SELECT count(*) FROM Item")
    int countItems();

    @Query("SELECT * from item WHERE id = :id")
    Item item( long id );

    @Query("SELECT * from item")
    Item[] items();

    @Query("SELECT i.* FROM item as i, ritemgroup as rig WHERE i.id = rig.itemId and rig.groupId = :groupId ")
    Item[] itemsByGroupId( long groupId );

    @Query("SELECT count(*) FROM ritemgroup WHERE ritemgroup.groupId = :groupId ")
    int countItemsByGroupId( long groupId );

    @Query("SELECT item.* FROM item, RItemGroup AS link WHERE link.groupId = :groupId AND link.itemId == item.id AND item.name LIKE :constrain")
    Item[] itemsByGroupIdLike( long groupId, String constrain );

    @Query("DELETE FROM item")
    void deleteAllItems();

    @Delete
    void deleteItem(Item item);

    @Update
    void updateItem(Item item);

    @Insert
    long insertRItemGroup(RItemGroup rItemGroup);

    @Query("DELETE from ritemgroup")
    void deleteAllRItemGroups();

    @Insert
    long insertGroup(Group group);

    @Query("SELECT * FROM `group` WHERE name = :name ")
    Group groupByName( String name );

    @Query("DELETE FROM `group`")
    void deleteAllGroups();
}
