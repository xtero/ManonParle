package org.eu.nveo.manonparle;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.*;
import org.eu.nveo.manonparle.Activity.BaseActivity;
import org.eu.nveo.manonparle.adapter.MiniItem;
import org.eu.nveo.manonparle.db.Database;
import org.eu.nveo.manonparle.db.DatabaseException;
import org.eu.nveo.manonparle.model.Group;

public class Select extends BaseActivity {
    private String tag = "Select";
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

        Intent args = getIntent();
        long groupId = Long.parseLong(args.getStringExtra("groupId"));
        Group group = null;
        try {
            group = Database.getConnection().group().byId( groupId );
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

        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( left.getContentDescription() == "" || right.getContentDescription() == "" ){
                    String text = getResources().getString(R.string.please_select);
                    Toast message = Toast.makeText( getApplicationContext(), text, Toast.LENGTH_SHORT );
                    message.show();
                    return;
                } else {
                    Intent i = new Intent(Select.this, DisplaySelection.class);
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
                ensureFullscreen();
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

        search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });
        int searchCloseId = search.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null);
        ImageView mSearchClose = search.findViewById( searchCloseId );
        mSearchClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int searchTextId = search.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
                TextView mSearchText = search.findViewById( searchTextId );
                mSearchText.setText("");
                search.setQuery("", false);
                search.onActionViewCollapsed();
            }
        });
    }
}
