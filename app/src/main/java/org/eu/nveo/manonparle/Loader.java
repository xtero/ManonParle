package org.eu.nveo.manonparle;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import org.eu.nveo.manonparle.Helper.AssetImporter;
import org.eu.nveo.manonparle.Helper.FileUtils;
import org.eu.nveo.manonparle.Helper.Fullscreen;
import org.eu.nveo.manonparle.db.Database;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;

public class Loader extends AppCompatActivity {
    private Fullscreen fs;
    private static String tag = "Loader";

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

        fs.ensureFullscreen();

        String tmpPath = "tmp";
        final File tmpFolder = getBaseContext().getDir( tmpPath, Context.MODE_PRIVATE );
        AssetImporter.cloneBaseAssetTo( getBaseContext(), tmpFolder );

        final AssetImporter importer = new AssetImporter(getBaseContext());

        try {
            importer.importFolder( tmpFolder, new AssetImporter.OnImportComplete() {


                @Override
                public void onComplete() {
                    FileUtils.cleanFolder( tmpFolder );

                    Log.v(tag, "Starting Select page");
                    Intent i = new Intent(Loader.this, Select.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }

                @Override
                public void onUpdate() {
                    TextView t = findViewById( R.id.notice );
                    int nbSynth = importer.getNbSynthesized();
                    int remainSynth = importer.getRemainingSynthesized();
                    t.setText( "Item audio à synthétiser "+remainSynth+"/"+nbSynth );
                }
            });
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        fs.ensureFullscreen();
    }
}
