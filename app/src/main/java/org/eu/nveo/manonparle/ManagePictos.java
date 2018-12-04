package org.eu.nveo.manonparle;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import org.eu.nveo.manonparle.adapter.ManagePictosGrid;
import org.eu.nveo.manonparle.adapter.PictoGridAdapter;
import org.eu.nveo.manonparle.model.Picto;

public class ManagePictos extends AppCompatActivity {

    private String searchStr;
    private ManagePictosGrid gridAdapter;

    private void setSearchStr( String searchStr ){
        this.searchStr = '%'+searchStr+'%';
    }

    private final Handler searchHandler = new Handler();

    private final Runnable refreshGrid = new Runnable() {
        @Override
        public void run() {

            Filter phil = gridAdapter.getFilter();
            phil.filter(searchStr, new Filter.FilterListener() {
                @Override
                public void onFilterComplete(int count) {
                    if( searchStr.equals("%%") ) {
                        Picto add = PictoGridAdapter.getStaticPicto( PictoGridAdapter.PICTO_ADD );
                        gridAdapter.insertPicto( 0, add );
                    }
                    gridAdapter.notifyDataSetChanged();
                }
            });
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_pictos);

        GridView grid = findViewById(R.id.manage_pictos);
        grid.setNumColumns(4);
        gridAdapter = new ManagePictosGrid( ManagePictos.this );
        grid.setAdapter( gridAdapter );

        SearchView search = findViewById( R.id.search_pictos);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setSearchStr( query );
                searchHandler.removeCallbacks( refreshGrid );
                searchHandler.postDelayed( refreshGrid, 500 );
                search.clearFocus();
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
