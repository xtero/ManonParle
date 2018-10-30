package org.eu.nveo.manonparle.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import org.eu.nveo.manonparle.model.PackagePicto;

@Dao
public interface PackagePictoDao {
    @Insert
    long insertPackagePicto(PackagePicto packagePicto);
}
