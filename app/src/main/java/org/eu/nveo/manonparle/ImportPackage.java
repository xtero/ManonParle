package org.eu.nveo.manonparle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.eu.nveo.manonparle.Activity.BaseActivity;
import org.eu.nveo.manonparle.db.Database;
import org.eu.nveo.manonparle.db.DatabaseException;
import org.eu.nveo.manonparle.db.ItemDatabase;
import org.eu.nveo.manonparle.helper.AssetImporter;
import org.eu.nveo.manonparle.helper.Definition;
import org.eu.nveo.manonparle.helper.FileUtils;
import org.eu.nveo.manonparle.model.*;
import org.eu.nveo.manonparle.model.Package;

import org.json.*;

import java.io.*;
import java.util.*;

import static org.eu.nveo.manonparle.helper.AssetImporter.*;
import static org.eu.nveo.manonparle.helper.ImageUtils.setGlowEffect;

public class ImportPackage extends BaseActivity implements TextToSpeech.OnInitListener  {

    private boolean importInProgress = false;
    private boolean hasRun = false;
    private boolean ttsEnabled = false;
    private int itemImported = 0;
    private int groupImported = 0;
    private int linksImported = 0;
    private Definition def;
    private TextToSpeech tts;
    private String tag = "ImportPackage";
    private ItemDatabase db;
    private File dataFolder;
    private File tmpFolder;
    private HashMap<String,Long> itemHash;
    private HashMap<String,Long> groupHash;
    private HashMap<String,Long> rItemgroupHash;
    private HashMap<String,Long> uidHash;
    private List<Long> toDelete;
    private Handler importHandler = new Handler();
    private Handler completeHandler = new Handler();
    private Runnable runImport = new Runnable() {
        @Override
        public void run() {
            try {
                hasRun = true;
                importPack();
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
    };

    private void displayReport(){
        View import_report = getLayoutInflater().inflate( R.layout.popup_import_report, null );
        PopupWindow popup = new PopupWindow( import_report, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT );

        TextView ok = import_report.findViewById( R.id.ok );
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                completeHandler.post( returnTo );
            }
        });
        setGlowEffect( ok, getResources().getColor(R.color.glowConfirm));

        TextView items = import_report.findViewById(R.id.imported_item);
        String itemReport = getResources().getString(R.string.report_item);
        itemReport = String.format( itemReport, itemImported, def.countItem() );
        items.setText(itemReport);

        TextView groups = import_report.findViewById(R.id.imported_group);
        String groupReport = getResources().getString(R.string.report_group);
        groupReport = String.format( groupReport, groupImported, def.countGroup() );
        groups.setText( groupReport );

        TextView links = import_report.findViewById(R.id.imported_links);
        String linksReport = getResources().getString(R.string.report_links);
        linksReport = String.format( linksReport, linksImported, def.countRItemGroup() );
        links.setText( linksReport );

        View root = findViewById(R.id.root);
        popup.showAtLocation( root, Gravity.CENTER ,0, 0);
    }

    private Runnable returnTo = new Runnable() {
        @Override
        public void run() {
            Intent next = new Intent( ImportPackage.this, MenuGroup.class );
            next.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(next);
        }
    };

    private Runnable checkComplete = new Runnable(){
        @Override
        public void run(){
            updateDisplay();
            if( ! importFinished() ){
                Log.v( tag , "Not Ready yet");
                completeHandler.postDelayed( checkComplete, 1000 );
            } else {
                findViewById(R.id.import_progress).setVisibility(View.GONE);
                if( tts != null ) {
                    tts.shutdown();
                }
                if( hasRun ){
                    if( toDelete.size() > 0 ){
                        for (Long aLong : toDelete) {
                            Item el = db.item().byId( aLong );
                            int nbLinks = el.countGroupIn();
                            itemImported--;
                            linksImported = linksImported - nbLinks;
                            el.delete();
                        }
                    }
                    displayReport();
                } else {
                    completeHandler.post( returnTo );
                }
            }
        }
    };

    private void updateDisplay() {
        ProgressBar item = findViewById(R.id.progress_items);
        item.setMax(def.countItem() );
        item.setProgress(itemImported);

        ProgressBar group = findViewById(R.id.progress_groups);
        group.setMax(def.countGroup() );
        group.setProgress(groupImported);

        ProgressBar link = findViewById(R.id.progress_links);
        link.setMax(def.countRItemGroup() );
        link.setProgress(linksImported);
    }

    private UtteranceProgressListener ttsProgress = new UtteranceProgressListener() {
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
        public void onError(String utteranceId) {
            Log.v(tag, "Generation failed for "+utteranceId );
            long id = uidHash.remove( utteranceId );
            toDelete.add( id );
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_package);

        try {
            db = Database.getConnection();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        dataFolder = AssetImporter.getDataFolder( this );
        tmpFolder = AssetImporter.getTmpFolder( this );
        itemHash = new HashMap<>();
        groupHash = new HashMap<>();
        rItemgroupHash = new HashMap<>();
        uidHash = new HashMap<>();
        toDelete = new ArrayList<>();

        Intent args = getIntent();
        String packName = args.getStringExtra("pack_name" );

        Log.v(tag, packName);

        File pack = new File( AssetImporter.getPackFolder(ImportPackage.this), packName );
        int integrity =  AssetImporter.checkZipIntegrity( pack );
        if( integrity != PACK_SUCCESS ){
            // TODO treat the case when we have a shity zip file
        }

        AssetImporter.extractPackToTmp( pack, ImportPackage.this );

        try {
            def = new Definition( new FileInputStream( new File( tmpFolder, "definition.json" ) ) );
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        Package dbPack = db.pack().packageByName( def.getName() );
        if( dbPack == null ){
            doImport();
        } else {
            if( dbPack.getVersion() > def.getVersion() ){
                doDowngrade();
            } else if ( dbPack.getVersion() < def.getVersion() ) {
                doUpgrade();
            } else {
                completeHandler.post( checkComplete );
            }
        }
    }

    private void doUpgrade() {
        // TODO manage upgrade
    }

    private void doDowngrade() {
        // TODO manage downgrade
    }

    private void doImport() {
        // TODO show tts loading popup
        findViewById(R.id.tts_loader).setVisibility(View.VISIBLE);
        tts = new TextToSpeech( this, this );
        tts.setOnUtteranceProgressListener( ttsProgress );
    }

    @Override
    public void onInit(int status) {
        findViewById(R.id.tts_loader).setVisibility(View.GONE);
        findViewById(R.id.import_progress).setVisibility(View.VISIBLE);
        if( status == TextToSpeech.SUCCESS ){
            ttsEnabled = true;
            tts.setLanguage( new Locale("fr") );
            importHandler.post( runImport );
            completeHandler.postDelayed( checkComplete, 1000 );
        } else {
            ttsEnabled = false;
            if( def.hasItemWithoutSound() ){
                View tts_offline = getLayoutInflater().inflate( R.layout.popup_import_confirm_no_tts, null);
                PopupWindow popup = new PopupWindow( tts_offline, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                TextView cancel = tts_offline.findViewById(R.id.cancel);
                TextView ok = tts_offline.findViewById(R.id.ok);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popup.dismiss();
                        completeHandler.post( returnTo );
                    }
                });
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popup.dismiss();
                        importHandler.post( runImport );
                        completeHandler.postDelayed( checkComplete, 1000 );
                    }
                });
                View root = findViewById(R.id.root);
                popup.showAtLocation( root, Gravity.CENTER ,0, 0);


            } else {
                importHandler.post( runImport );
                completeHandler.postDelayed( checkComplete, 1000 );
            }
            // open a popup to know if continue to import
        }
    }

    private long importItem( JSONObject item ) throws JSONException {
        String name = item.getString("label");
        Log.v( tag, "Creating item "+name);
        String imageName = item.getString( "image" );
        String imageExt = FileUtils.findExt( imageName );
        String audioName = item.getString( "audio" );
        String audioExt = FileUtils.findExt( audioName );

        // if TTS is disabled, and the item doesn't have a sound, skipped it
        if( audioName == null && ! ttsEnabled ) {
            return -1;
        }

        Item el = new Item();
        el.setName( name );
        el.setHasSound(  true );
        el.setSoundSynth( audioName == "null" );
        el.setImageExt( imageExt );
        if( el.getSoundSynth() ) {
            el.setAudioExt( "wav" );
        } else {
            el.setAudioExt(audioExt);
        }

        long id = db.item().insert( el );

        // import image
        try {
            FileInputStream image = new FileInputStream( new File( tmpFolder ,  "medias/" + imageName ) );
            Log.v(tag, "Copy image");
            FileOutputStream fos = new FileOutputStream( new File( dataFolder , Long.toString(id)+"." + imageExt ) );
            FileUtils.copyFile( image, fos );
            image.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // import audio - synthesized if not provided
        if( ! el.getSoundSynth() ) {
            try {
                FileInputStream audio = new FileInputStream( new File( tmpFolder,  "medias/" + audioName ) );
                Log.v(tag, "Copy audio");
                FileOutputStream fos = new FileOutputStream( new File( dataFolder , Long.toString(id)+"."+ audioExt ) );
                FileUtils.copyFile( audio, fos );
                audio.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            String uid = UUID.randomUUID().toString();
            Log.v(tag, "Synthesized audio "+name+ " with uid " + uid);
            uidHash.put(uid, id);
            tts.synthesizeToFile( name.subSequence( 0, name.length() ), null, new File( dataFolder,Long.toString(id)+".wav" ), uid  );
        }

        itemImported++;

        return id;
    }

    private long importGroup( JSONObject group ) throws JSONException {
        String name = group.getString("label");
        Log.v( tag, "Creating group "+name);

        Group el = new Group();
        el.setName( name );
        long id = db.group().insert( el );
        groupImported++;
        return id;
    }

    private void importRItemGroup(JSONObject link ) throws JSONException {

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
            long id = db.ritemgroup().insert( el );
            String itemGroup = itemId + ":" + groupId;
            rItemgroupHash.put( itemGroup, id );
            linksImported++;
        }

    }

    private void createPack( Definition def ){
        Package pack = new Package();
        pack.setLabel( def.getLabel() );
        pack.setName( def.getName() );
        pack.setVersion( def.getVersion() );
        long id = db.pack().insertPackage( pack );

        // create all PackageItem
        Set<String> items = itemHash.keySet();
        for (String item : items) {
            long itemId = itemHash.get( item );
            PackageItem packItem = new PackageItem();
            packItem.setItemId( itemId );
            packItem.setItemName( item );
            packItem.setPackageId( id );
            db.packitem().insertPackageItem( packItem );
        }
        // create all PackageGroup
        Set<String> groups = groupHash.keySet();
        for (String group : groups) {
            long groupId = groupHash.get( group );
            PackageGroup packGroup = new PackageGroup();
            packGroup.setGroupId( groupId );
            packGroup.setGroupName( group );
            packGroup.setPackageId( id );
            db.packgroup().insertPackageGroup( packGroup );
        }

        // create all PackageRItemGroup
        Set<String>itemGroups = rItemgroupHash.keySet();
        for (String itemGroup : itemGroups) {
            long ritemgroupId = rItemgroupHash.get( itemGroup );
            String[] split = itemGroup.split(":");
            long itemId = Long.parseLong( split[0] );
            long groupId = Long.parseLong( split[1] );
            PackageRItemGroup packageRItemGroup = new PackageRItemGroup();
            packageRItemGroup.setRitemgroupId(ritemgroupId);
            packageRItemGroup.setGroupId(groupId);
            packageRItemGroup.setItemId(itemId);
            packageRItemGroup.setPackageId(id);
            db.packritemgroup().insertPackageRItemGroup( packageRItemGroup );
        }

    }

    private void importPack() throws JSONException, IOException {
        // Load json file
        importInProgress = true;
        Log.v( tag, "Loading definition.json");
        Definition def = new Definition( new FileInputStream( new File( tmpFolder, "definition.json" ) ) );

        // import Items
        Log.v( tag, "Loading items array");
        JSONArray items = def.getItems();
        for( int i = 0 ; i < items.length(); i++ ){
            JSONObject item = items.getJSONObject( i );
            long id = importItem( item );
            // If id == -1, the item, has not been created
            if( id != -1 ) {
                String name = item.getString("label");
                itemHash.put(name, id);
            }
            updateDisplay();
        }

        // import Groups
        Log.v( tag, "Loading groups array");
        JSONArray groups = def.getGroups();
        for( int i = 0 ; i < groups.length(); i++ ){
            JSONObject group = groups.getJSONObject( i );
            long id = importGroup( group );
            String name = group.getString("label");
            groupHash.put( name, id );
            updateDisplay();
        }

        // create RItemGroups
        Log.v( tag, "Loading links array");
        JSONArray links = def.getRItemGroups();
        for( int i = 0 ; i < links.length(); i++ ){
            JSONObject el = links.getJSONObject( i );
            importRItemGroup( el );
            updateDisplay();
        }

        createPack( def );

        // Cleanup to avoid memory useless usage
        itemHash = null;
        groupHash = null;

        importInProgress = false;
    }

    private boolean importFinished(){
        Log.v( tag, "Test synthesizing completion");
        if( uidHash.size() > 0 ){
            Log.v(tag, "still some sythesiezing : " + uidHash.size());
            return false;
        }
        if( importInProgress ){
            Log.v(tag, "Import in progress");
            return false;
        }
        Log.v(tag, "Import completed");
        return true;
    }

}
