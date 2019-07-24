package org.eu.nveo.manonparle.helper;

import android.content.Context;
import org.eu.nveo.manonparle.ManonParle;

import java.io.File;

public class Folders {
    private static String tmp_path = "tmp";
    private static String data_path = "picto";
    private static String pack_path = "pack";
    private static String indexes_path = "indexes";

    public static File getTmpFolder(){
        return ManonParle.getContext().getDir(tmp_path, Context.MODE_PRIVATE );
    }

    public static File getDataFolder(){
        return ManonParle.getContext().getDir( data_path, Context.MODE_PRIVATE );
    }

    public static File getPackFolder(){
        return ManonParle.getContext().getDir( pack_path, Context.MODE_PRIVATE );
    }

    public static File getIndexesFolder() {
        return ManonParle.getContext().getDir( indexes_path, Context.MODE_PRIVATE );
    }
}
