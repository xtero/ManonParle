package org.eu.nveo.manonparle;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import org.eu.nveo.manonparle.Activity.BaseActivity;
import org.eu.nveo.manonparle.adapter.GroupAdapter;

public class MenuGroup extends BaseActivity {

    private String tag = "MenuGroup";
    private ImageView settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(tag, "Creating activity MenuGroup");
        setContentView(R.layout.activity_menu_group);

        ListView list = findViewById( R.id.groups );
        list.setAdapter(new GroupAdapter( getApplicationContext() ) );
        settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( MenuGroup.this, Settings.class );
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity( i );
            }
        });
    }

}
