package org.eu.nveo.manonparle.Helper;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    private static String tag = "FileUtils";

    public static void copyFile(FileInputStream from, FileOutputStream to ) throws IOException {
        byte[] buffer = new byte[1024];
        while( from.read( buffer ) > 0 ) {
            to.write( buffer );
        }
    }

    public static void listFolderContent( File folder ){
        File[] list = folder.listFiles();
        for (File file : list) {
            Log.v( tag, file.getAbsolutePath() );
            if( file.isDirectory() ){
                listFolderContent( file );
            }
        }
    }

    public static void cleanFolder( File folder ){
        File[] list = folder.listFiles();
        for (File file : list) {
            if( file.isDirectory() ){
                cleanFolder( file );
            }
            Log.v( tag, "Deleting " + file.getAbsolutePath() );
            file.delete();
        }
    }
}
