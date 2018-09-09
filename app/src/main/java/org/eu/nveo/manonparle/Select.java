package org.eu.nveo.manonparle;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.*;
import org.eu.nveo.manonparle.Adapter.MiniItem;
import org.eu.nveo.manonparle.Helper.Fullscreen;
import org.eu.nveo.manonparle.db.Database;
import org.eu.nveo.manonparle.db.DatabaseException;
import org.eu.nveo.manonparle.model.Group;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Select extends AppCompatActivity {
    private Fullscreen fs;
    private ImageView left;
    private ImageView right;
    private GridView grid;
    private MiniItem miniItem;
    private String searchStr;
    private SearchView search;

    private final View.OnDragListener dragHandler = new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            if( action == DragEvent.ACTION_DROP) {
                ImageView img = (ImageView) v;
                ClipData data = event.getClipData();
                String uri = data.getItemAt(0 ).getText().toString();
                String id = data.getItemAt( 1 ).getText().toString();
                img.setImageURI(Uri.parse( uri ));
                img.setContentDescription( id );
            }
            return true;
        }
    };

    private void setSearchStr( String searchStr ){
        this.searchStr = '%'+searchStr+'%';
    }

    private final Handler searchHandler = new Handler();

    private final Runnable refreshGrid = new Runnable() {
        @Override
        public void run() {

            Filter phil = miniItem.getFilter();
            phil.filter(searchStr, new Filter.FilterListener() {
                @Override
                public void onFilterComplete(int count) {
                    miniItem.notifyDataSetChanged();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select);

        Group group = null;
        try {
            group = Database.getConnection().itemDao().groupByName( "base" );
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        grid = findViewById( R.id.itemList );
        grid.setNumColumns( 2 );

        miniItem = new MiniItem( getBaseContext(), group.getId() );
        grid.setAdapter( miniItem );

        left = findViewById( R.id.imageView);
        right = findViewById( R.id.imageView2);
        left.setOnDragListener( dragHandler );
        right.setOnDragListener( dragHandler );
        left.setContentDescription("");
        right.setContentDescription("");


        fs = new Fullscreen( this );

        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( left.getContentDescription() == "" || right.getContentDescription() == "" ){
                    Toast message = Toast.makeText( getApplicationContext(), "Il faut selectionner 2 images", Toast.LENGTH_SHORT );
                    message.show();
                    return;
                } else {
                    Intent i = new Intent(Select.this, SelectItem.class);
                    String idLeft = (String) left.getContentDescription();
                    String idRight = (String) right.getContentDescription();
                    i.putExtra("idLeft", idLeft);
                    i.putExtra("idRight", idRight);
                    startActivity(i);
                }
            }
        });

        search = findViewById( R.id.searchitems );
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setSearchStr( query );
                searchHandler.removeCallbacks( refreshGrid );
                searchHandler.postDelayed( refreshGrid, 500 );
                search.clearFocus();
                fs.ensureFullscreen();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setSearchStr( newText );
                searchHandler.removeCallbacks( refreshGrid );
                searchHandler.postDelayed( refreshGrid, 500 );
                return false;
            }
        });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        fs.ensureFullscreen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fs.ensureFullscreen();
    }
}