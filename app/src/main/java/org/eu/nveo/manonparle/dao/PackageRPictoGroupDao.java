package org.eu.nveo.manonparle.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import org.eu.nveo.manonparle.model.PackageRPictoGroup;

@Dao
public interface PackageRPictoGroupDao {
    @Insert
    long insertPackageRPictoGroup(PackageRPictoGroup packageRPictoGroup);
}
