package org.eu.nveo.manonparle.helper;

import android.util.Log;

import java.io.*;

public class FileUtils {

    private static String tag = "FileUtils";

    public static void copyFile( InputStream from, FileOutputStream to ) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while( ( len = from.read( buffer ) ) > 0 ) {
            to.write( buffer, 0, len );
        }
    }

    public static void copyFromInputStream(InputStream from, FileOutputStream to ) throws IOException {
        byte[] buffer = new byte[1024];
        int len=0;
        while( ( len = from.read( buffer ) ) > 0 ) {
            to.write( buffer, 0, len );
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
