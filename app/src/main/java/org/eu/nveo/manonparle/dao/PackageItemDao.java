package org.eu.nveo.manonparle.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import org.eu.nveo.manonparle.model.PackageItem;

@Dao
public interface PackageItemDao {
    @Insert
    long insertPackageItem(PackageItem packageItem );
}
