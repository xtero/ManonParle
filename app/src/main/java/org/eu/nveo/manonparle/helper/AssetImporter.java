package org.eu.nveo.manonparle.helper;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.util.Log;
import org.json.JSONException;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AssetImporter {

    public static int PACK_SUCCESS = 0;
    public static int PACK_ERROR_NOT_ZIP = 1;
    public static int PACK_ERROR_WRONG_FORMAT = 2;
    public static int PACK_ERROR_INVALID_DEF = 3;
    public static int PACK_ERROR_WRONG_FILE_FORMAT = 4;
    public static String[] PACK_FORMAT_SUPPORTED = {
            ".png",
            ".mp3"
        };

    private static String tag = "AssetImporter";

    @Deprecated
    public static void cloneBaseAssetTo( Context ctx, File tmpFolder ){

        // Import data from asset
        Log.v( tag , "Clone base Asset to "+ tmpFolder.getAbsolutePath() );
        File tmpMediaFolder = new File( tmpFolder, "medias");
        if( ! tmpMediaFolder.exists() ){
            tmpMediaFolder.mkdir();
        }
        try {
            Log.v( tag , "Cloning definition.json" );
            AssetFileDescriptor fd = ctx.getAssets().openFd( "definition.json" );
            FileInputStream fis = fd.createInputStream();
            FileOutputStream fos = new FileOutputStream( new File( tmpFolder, "definition.json" ) );
            FileUtils.copyFile( fis, fos );
            fis.close();
            fos.close();
            String[] list = ctx.getAssets().list("medias");
            for (String picto : list) {
                Log.v( tag , "Cloning medias/" + picto );
                fd = ctx.getAssets().openFd( "medias/" + picto );
                fis = fd.createInputStream();
                fos = new FileOutputStream( new File( tmpMediaFolder, picto ) );
                FileUtils.copyFile( fis, fos );
                fis.close();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v( tag, "Listing folder "+ tmpFolder.getAbsolutePath() );
        FileUtils.listFolderContent( tmpFolder );
    }

    public static void cleanDataFolder( Context context ){
        File dataFolder = Folders.getDataFolder();
        File[] files = dataFolder.listFiles();
        for (File file : files) {
            file.delete();
        }
        dataFolder.delete();
    }

    public static int checkZipIntegrity( File pack ){
        ZipFile file;
        try {
            file = new ZipFile( pack );
        } catch (IOException e) {
            return PACK_ERROR_NOT_ZIP;
        }

        ZipEntry zipDef = null;
        Enumeration<? extends ZipEntry> entries = file.entries();
        while( entries.hasMoreElements() ) {
            ZipEntry entry = entries.nextElement();

            if( entry.isDirectory() ) {
                if( entry.getName() != "medias" ) {
                    return PACK_ERROR_WRONG_FORMAT;
                }
            } else {
                if( entry.getName() == "definition.json" ) {
                    zipDef = entry;
                }
                boolean validFormat = false;
                for( int i = 0; i < PACK_FORMAT_SUPPORTED.length; i++ ){
                    if( entry.getName().endsWith( PACK_FORMAT_SUPPORTED[i] ) ) {
                        validFormat = true;
                    }
                }
                if( ! validFormat ) {
                    return PACK_ERROR_WRONG_FILE_FORMAT;
                }
            }
        }
        // After parsing all entries, there is no definition.json
        if(zipDef == null) {
            return PACK_ERROR_WRONG_FORMAT;
        }

        InputStream defIs;
        try {
            defIs = file.getInputStream( zipDef );
            new Definition( defIs );
            defIs.close();
        } catch (IOException| JSONException e ) {
            return PACK_ERROR_INVALID_DEF;
        }

        return PACK_SUCCESS;
    }

    public static void extractPackToTmp( File pack ){
        ZipFile file;
        File tmpFolder = Folders.getTmpFolder();
        File tmpMediaFolder = new File( tmpFolder, "medias");
        if( ! tmpMediaFolder.exists() ){
            tmpMediaFolder.mkdir();
        }
        try {
            file = new ZipFile( pack );
            Enumeration<? extends ZipEntry> entries = file.entries();
            while( entries.hasMoreElements() ) {
                ZipEntry entry = entries.nextElement();
                if( ! entry.isDirectory() ) {
                    InputStream fis = file.getInputStream(entry);
                    FileOutputStream fos = new FileOutputStream(new File(tmpFolder, entry.getName()));
                    FileUtils.copyFile( fis, fos );
                    fis.close();
                    fos.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
