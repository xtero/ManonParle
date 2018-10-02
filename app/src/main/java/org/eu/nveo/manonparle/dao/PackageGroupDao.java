package org.eu.nveo.manonparle.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import org.eu.nveo.manonparle.model.PackageGroup;

@Dao
public interface PackageGroupDao {
    @Insert
    long insertPackageGroup(PackageGroup packageGroup );
}
