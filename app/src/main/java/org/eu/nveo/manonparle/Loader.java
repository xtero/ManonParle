package org.eu.nveo.manonparle;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import org.eu.nveo.manonparle.Activity.FullscreenActivity;
import org.eu.nveo.manonparle.db.Database;
import org.eu.nveo.manonparle.helper.FileUtils;
import org.eu.nveo.manonparle.helper.Folders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static org.eu.nveo.manonparle.helper.ImageUtils.setGlowEffect;
import static org.eu.nveo.manonparle.helper.Preferences.GLOBAL_PREFS;

public class Loader extends FullscreenActivity {
    private static String tag = "Loader";

    private Handler deffered = new Handler();

    private static final int PERM = 1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loader);

        Database.initConnection( getBaseContext() );

        SharedPreferences prefs = getApplicationContext().getSharedPreferences( GLOBAL_PREFS, MODE_PRIVATE);

        if( prefs.getBoolean("first_run", true) ){

            checkPerm();

            View import_popup = getLayoutInflater().inflate(R.layout.popup_load_import, null );
            PopupWindow popup = new PopupWindow( import_popup, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT );

            TextView ok = import_popup.findViewById(R.id.ok);
            ok.setOnClickListener(v -> {
                popup.dismiss();
                String pack = "base.zip";
                // Copy Zip asset file to app storage
                File appPathBase = new File( Folders.getPackFolder(), pack );
                try {
                    FileInputStream fis = getAssets().openFd( pack ).createInputStream();
                    FileOutputStream fos = new FileOutputStream( appPathBase );
                    FileUtils.copyFile(fis,fos);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // run import on the zip File
                Intent next = new Intent( Loader.this, ImportPackage.class );
                next.putExtra("pack_name", pack );
                next.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity( next );
            });
            setGlowEffect( ok, getResources().getColor(R.color.glowConfirm) );

            TextView cancel = import_popup.findViewById(R.id.cancel);
            cancel.setOnClickListener(v -> {

                popup.dismiss();
                Intent next = new Intent( Loader.this, SelectGroup.class );
                next.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity( next );
            });
            setGlowEffect( cancel, getResources().getColor(R.color.glowInvalid) );

            popup.setOnDismissListener(() -> {
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean("first_run", false);
                edit.commit();
            });

            deffered.postDelayed(() -> {
                View root = findViewById( R.id.root );
                popup.showAtLocation( root , Gravity.CENTER,0,0);
            }, 100);
        } else {
            Intent next = new Intent( Loader.this, SelectGroup.class );
            next.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity( next );
        }

    }

    private void checkPerm() {
        ArrayList<String> permissions = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
            permissions.add(  Manifest.permission.WRITE_EXTERNAL_STORAGE );
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ){
            permissions.add( Manifest.permission.CAMERA );
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ){
            permissions.add( Manifest.permission.RECORD_AUDIO );
        }
        if( permissions.size() > 0) {
            String[] array = permissions.toArray( new String[permissions.size()] );
            ActivityCompat.requestPermissions(this, array, PERM);
        }
    }

}
