package org.eu.nveo.manonparle.Helper;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import org.apache.commons.io.IOUtils;
import org.eu.nveo.manonparle.db.Database;
import org.eu.nveo.manonparle.db.DatabaseException;
import org.eu.nveo.manonparle.db.ItemDao;
import org.eu.nveo.manonparle.db.ItemDatabase;
import org.eu.nveo.manonparle.model.Group;
import org.eu.nveo.manonparle.model.Item;
import org.eu.nveo.manonparle.model.RItemGroup;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public class AssetImporter implements OnInitListener  {

    private static String tag = "AssetImporter";
    private HashMap<String,Long> itemHash;
    private HashMap<String,Long> groupHash;
    private HashMap<String,Long> uidHash;
    private File dataFolder;
    private TextToSpeech tts;
    private OnImportComplete listener;
    private Context ctx;
    private File importFolder;

    private Handler ttsHandler = new Handler();

    private Runnable onComplete = new Runnable() {
        @Override
        public void run() {
            if( uidHash.size() == 0 ){
                tts.shutdown();
                listener.onComplete();
            } else {
                ttsHandler.postAtTime( onComplete, 100 );
            }
        }
    };

    public AssetImporter(Context context ){
        ctx = context;
        dataFolder = context.getDir(Item.STORAGE_PATH, Context.MODE_PRIVATE);
        itemHash = new HashMap<>();
        groupHash = new HashMap<>();
        uidHash = new HashMap<>();

    }

    private long importItem( JSONObject item, File folder ) throws JSONException {
        String name = item.getString("label");
        Log.v( tag, "Creating item "+name);
        String imageName = item.getString( "image" );
        String audioName = item.getString( "audio" );
        ItemDatabase db = null;
        try {
            db = Database.getConnection();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        ItemDao dao = db.itemDao();
        Item el = new Item();
        el.setName( name );
        el.setHasSound(  true );
        long id = dao.insertItem( el );

        // import image
        FileInputStream image = null;
        try {
            image = new FileInputStream( new File( folder ,  "medias/" + imageName ) );
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Log.v(tag, "Copy image");
            FileOutputStream fos = new FileOutputStream( new File( dataFolder , Long.toString(id)+".png" ) );
            FileUtils.copyFile( image, fos );
        } catch (IOException e) {
            e.printStackTrace();
        }

        // import audio - synthesized if not provided
        if( audioName != "null" ) {
            FileInputStream audio = null;
            try {
                audio = new FileInputStream( new File( folder,  "medias/" + audioName ) );
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Log.v(tag, "Copy audio");
                FileOutputStream fos = new FileOutputStream( new File( dataFolder , Long.toString(id)+".mp3" ) );
                FileUtils.copyFile( audio, fos );
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            String uid = UUID.randomUUID().toString();
            Log.v(tag, "Synthesized audio "+name+ " with uid " + uid);
            tts.synthesizeToFile( name.subSequence( 0, name.length() ), null, new File( dataFolder,Long.toString(id)+".mp3" ), uid  );
            uidHash.put(uid, (long) 1);
        }


        return id;
    }

    private long importGroup( JSONObject group ) throws JSONException {
        String name = group.getString("label");
        Log.v( tag, "Creating group "+name);
        ItemDatabase db = null;

        try {
            db = Database.getConnection();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        ItemDao dao = db.itemDao();
        Group el = new Group();
        el.setName( name );
        long id = dao.insertGroup( el );
        return id;
    }

    private void createLink( JSONObject link ) throws JSONException {
        ItemDatabase db = null;
        try {
            db = Database.getConnection();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        ItemDao dao = db.itemDao();

        String groupName = link.getString("group" );
        Log.v( tag, "Creating links for group "+groupName);
        long groupId = groupHash.get( groupName );
        JSONArray items = link.getJSONArray( "items" );
        for( int i = 0; i< items.length(); i++ ){
            String itemName = items.getString( i );
            long itemId = itemHash.get( itemName );
            RItemGroup el = new RItemGroup();
            el.setGroupId( groupId );
            el.setItemId( itemId );
            dao.insertRItemGroup( el );
        }

    }

    public void importFolder( File folder, OnImportComplete complete ) throws IOException, JSONException {
        listener = complete;
        importFolder = folder;
        tts = new TextToSpeech( ctx, this );
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                Log.v(tag, "Starting generating file "+utteranceId );
            }

            @Override
            public void onDone(String utteranceId) {
                Log.v(tag, "Generation complete for "+utteranceId );
                uidHash.remove( utteranceId );
            }

            @Override
            public void onError(String utteranceId ) {
                Log.v(tag, "Generation failed for "+utteranceId );

            }
            @Override
            public void onError(String utteranceId, int errorCode) {
                Log.v(tag, "Generation failed for "+utteranceId+" error: "+errorCode );
                onError(utteranceId);
            }
        });


    }

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
            for (String item : list) {
                Log.v( tag , "Cloning medias/" + item );
                fd = ctx.getAssets().openFd( "medias/" + item );
                fis = fd.createInputStream();
                fos = new FileOutputStream( new File( tmpMediaFolder, item ) );
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
        File dataFolder = context.getDir(Item.STORAGE_PATH, Context.MODE_PRIVATE);
        File[] files = dataFolder.listFiles();
        for (File file : files) {
            file.delete();
        }
        dataFolder.delete();
    }

    private void internalImportFolder() throws IOException, JSONException {
        // Load json file
        Log.v( tag, "Loading definition.json");
        File defPath = new File( importFolder, "definition.json" );
        FileInputStream is = new FileInputStream( defPath );
        String jsonString = IOUtils.toString( is, "UTF-8" );
        JSONObject json = new JSONObject( jsonString );
        // import Items
        Log.v( tag, "Loading items array");
        JSONArray items = json.getJSONArray( "items" );
        for( int i = 0 ; i < items.length(); i++ ){
            JSONObject item = items.getJSONObject( i );
            long id = importItem( item, importFolder );
            String name = item.getString("label");
            itemHash.put(name, id);
        }
        // import Groups
        Log.v( tag, "Loading groups array");
        JSONArray groups = json.getJSONArray( "groups" );
        for( int i = 0 ; i < groups.length(); i++ ){
            JSONObject group = groups.getJSONObject( i );
            long id = importGroup( group );
            String name = group.getString("label");
            groupHash.put( name, id );
        }
        // create RItemGroups
        Log.v( tag, "Loading links array");
        JSONArray links = json.getJSONArray( "links" );
        for( int i = 0 ; i < links.length(); i++ ){
            JSONObject el = links.getJSONObject( i );
            createLink( el );
        }

        FileUtils.listFolderContent( dataFolder );

        // Cleanup to avoid memory useless usage
        itemHash = null;
        groupHash = null;

    }

    @Override
    public void onInit(int status) {
        if( status == TextToSpeech.SUCCESS) {
            Log.v(tag, "Ready to import");
            tts.setLanguage(new Locale("fr"));
            try {
                internalImportFolder();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ttsHandler.post( onComplete );
        }

    }

    public abstract static class OnImportComplete {
        public abstract void onComplete();
    }
}
