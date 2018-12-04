package org.eu.nveo.manonparle.dao;

import android.arch.persistence.room.*;
import org.eu.nveo.manonparle.model.Picto;

@Dao
public interface PictoDao {
    @Insert
    long insert(Picto picto);

    @Query("SELECT * FROM Picto WHERE name = :name ")
    Picto byName(String name);

    @Query("SELECT count(*) FROM Picto")
    int countAll();

    @Query("SELECT * from Picto WHERE id = :id")
    Picto byId(long id );

    @Query("SELECT * from Picto")
    Picto[] all();

    @Query("SELECT i.* FROM Picto as i, RPictoGroup as rig WHERE i.id = rig.pictoId and rig.groupId = :groupId ")
    Picto[] byGroupId(long groupId );

    @Query("SELECT count(*) FROM RPictoGroup WHERE RPictoGroup.groupId = :groupId ")
    int countByGroupId( long groupId );

    @Query("SELECT Picto.* FROM Picto, RPictoGroup AS link WHERE link.groupId = :groupId AND link.pictoId == Picto.id AND Picto.name LIKE :constrain")
    Picto[] byGroupIdLike(long groupId, String constrain );

    @Query("SELECT Picto.* FROM Picto WHERE Picto.name LIKE :constrain")
    Picto[] allLike( String constrain );

    @Query("DELETE FROM Picto")
    void deleteAll();

    @Delete
    void delete(Picto picto);

    @Update
    void update(Picto picto);
}
