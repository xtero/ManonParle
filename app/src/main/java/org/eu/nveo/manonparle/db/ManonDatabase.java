package org.eu.nveo.manonparle.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import org.eu.nveo.manonparle.dao.*;
import org.eu.nveo.manonparle.model.*;
import org.eu.nveo.manonparle.model.Package;

@Database(
        entities = {
                Picto.class,
                Group.class ,
                RPictoGroup.class,
                Package.class,
                PackagePicto.class,
                PackageGroup.class,
                PackageRPictoGroup.class,
                Repo.class
        },
        version = 6,
        exportSchema = true
)
public abstract class ManonDatabase extends RoomDatabase {

    public abstract PictoDao picto();

    public abstract GroupDao group();

    public abstract RPictoGroupDao rpictogroup();

    public abstract PackageDao pack();

    public abstract PackagePictoDao packpicto();

    public abstract PackageGroupDao packgroup();

    public abstract PackageRPictoGroupDao packrpictogroup();

    public abstract RepoDao repo();
}
