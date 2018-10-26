package org.eu.nveo.manonparle;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import org.eu.nveo.manonparle.Activity.BaseActivity;
import org.eu.nveo.manonparle.adapter.GroupAdapter;

public class MenuGroup extends BaseActivity {

    private String tag = "MenuGroup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(tag, "Creating activity MenuGroup");
        setContentView(R.layout.activity_menu_group);

        ImageView settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( MenuGroup.this, Settings.class );
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity( i );
            }
        });

        ImageView newItem = findViewById( R.id.new_item);
        if( ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
            newItem.setVisibility( View.INVISIBLE );
        } else {
            newItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent( MenuGroup.this, NewItemShot.class );
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
        list.setAdapter(new GroupAdapter( getApplicationContext() ) );

    }


}
