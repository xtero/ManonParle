package org.eu.fr.nveo.manonparle;

import android.content.ClipData;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import org.eu.fr.nveo.manonparle.Adapter.MiniItem;
import org.eu.fr.nveo.manonparle.Helper.Fullscreen;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Welcome extends AppCompatActivity {
    private Fullscreen fs;
    private ImageView left;
    private ImageView right;

    View.OnDragListener dragHandler = new View.OnDragListener() {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        GridView grid = findViewById( R.id.itemList );
        grid.setAdapter( new MiniItem( getBaseContext(), 0 ));
        grid.setNumColumns( 2 );
        left = findViewById( R.id.imageView);
        right = findViewById( R.id.imageView2);

        left.setOnDragListener( dragHandler );
        right.setOnDragListener( dragHandler );


        fs = new Fullscreen( this );

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        fs.ensureFullscreen( 100 );
    }

    @Override
    protected void onResume() {
        super.onResume();
        fs.ensureFullscreen( 100 );
    }
}
