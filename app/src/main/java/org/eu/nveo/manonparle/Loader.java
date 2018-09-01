package org.eu.nveo.manonparle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import org.eu.nveo.manonparle.Helper.AssetImporter;
import org.eu.nveo.manonparle.Helper.FileUtils;
import org.eu.nveo.manonparle.Helper.Fullscreen;
import org.eu.nveo.manonparle.db.Database;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;

public class Loader extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private Fullscreen fs;
    private static String tag = "Loader";
    private final Handler importHandler = new Handler();
    private AssetImporter importer;

    private Runnable importBaseAsset = new Runnable() {
        @Override
        public void run() {
            String tmpPath = "tmp";
            File tmpFolder = getBaseContext().getDir( tmpPath, Context.MODE_PRIVATE);
            AssetImporter.cloneBaseAssetTo( getBaseContext(), tmpFolder );

            importer = new AssetImporter(getBaseContext(), getActivity() );
        }
    };

    private Loader getActivity(){
        return this;
    }

    private void doDataCleanup(){
        Database.empty();
        AssetImporter.cleanDataFolder( getBaseContext() );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loader);

        fs = new Fullscreen( this );

        Database.initConnection( getBaseContext() );

        doDataCleanup();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        fs.ensureFullscreen( 100 );

        importHandler.postDelayed( importBaseAsset, 100 );

    }

    @Override
    protected void onResume() {
        super.onResume();
        fs.ensureFullscreen( 100 );
    }

    @Override
    public void onInit(int status) {
        if( status == TextToSpeech.SUCCESS) {
            Log.v(tag, "Ready to import");
            String tmpPath = "tmp";
            File tmpFolder = getBaseContext().getDir(tmpPath, Context.MODE_PRIVATE);

            try {
                importer.importFolder(tmpFolder);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            FileUtils.cleanFolder(tmpFolder);

            Intent i = new Intent(Loader.this, Select.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
    }
}
