package org.eu.nveo.manonparle.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import org.eu.nveo.manonparle.model.Picto;
import org.eu.nveo.manonparle.model.RPictoGroup;

@Dao
public interface RPictoGroupDao {
    @Insert
    long insert(RPictoGroup rPictoGroup);

    @Query("DELETE from RPictoGroup")
    void deleteAll();

    @Query("SELECT count(*) FROM RPictoGroup WHERE groupId = :id ")
    int countByGroupId( long id);

    @Query("SELECT * FROM RPictoGroup WHERE groupId = :id ")
    RPictoGroup[] byGroupId(long id );

    @Query("SELECT count(*) FROM RPictoGroup WHERE pictoId = :id ")
    int countByPictoId(long id);

    @Query("SELECT * FROM RPictoGroup WHERE pictoId = :id ")
    RPictoGroup[] byPictoId(long id );

    @Query("SELECT Picto.* FROM Picto, RPictoGroup WHERE Picto.id = RPictoGroup.pictoId AND RPictoGroup.groupId = :id ORDER BY Picto.id LIMIT 1")
    Picto firstGroupPicto(long id );

    @Delete
    void delete( RPictoGroup rPictoGroup);

    @Query("SELECT * FROM RPictoGroup WHERE groupId = :groupId and pictoId = :pictoId ")
    RPictoGroup byGroupIdPictoId(long groupId, long pictoId );
}
