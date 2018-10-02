package org.eu.nveo.manonparle.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import org.eu.nveo.manonparle.model.PackageRItemGroup;

@Dao
public interface PackageRItemGroupDao {
    @Insert
    long insertPackageRItemGroup(PackageRItemGroup packageRItemGroup );
}
