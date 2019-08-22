package org.eu.nveo.manonparle;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import org.eu.nveo.manonparle.adapter.GroupListAdapter;

public class SelectGroup extends AppCompatActivity {

    private String tag = "SelectGroup";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_select_group_actionbar, menu);
        MenuItem picto = menu.findItem( R.id.menu_picto );
        picto.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = new Intent( SelectGroup.this, ManagePictos.class );
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity( i );
                return true;
            }
        });
        MenuItem settings = menu.findItem( R.id.menu_settings );
        settings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = new Intent( SelectGroup.this, Settings.class );
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity( i );
                return true;
            }
        });
        MenuItem packageManager = menu.findItem(R.id.menu_package_manager);
        packageManager.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = new Intent( SelectGroup.this, ManagePackages.class );
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity( i );
                return true;
            }
        });
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(tag, "Creating activity SelectGroup");
        setContentView(R.layout.activity_select_group);
        Toolbar bar = findViewById(R.id.toolbar);
        bar.setTitle( R.string.app_name );
        bar.setTitleTextColor( getResources().getColor( R.color.colorBaseText ) );
        bar.setOverflowIcon( getResources().getDrawable(R.drawable.ic_menu) );
        setSupportActionBar( bar );


        ImageView settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( SelectGroup.this, Settings.class );
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity( i );
            }
        });

        ImageView newPicto = findViewById( R.id.new_picto);
        if( ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
            newPicto.setVisibility( View.INVISIBLE );
        } else {
            newPicto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent( SelectGroup.this, FormPicto.class );
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity( i );
                }
            });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        ListView list = findViewById( R.id.groups );
        list.setAdapter(new GroupListAdapter() );

    }


}
