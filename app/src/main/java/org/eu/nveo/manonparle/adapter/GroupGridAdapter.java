package org.eu.nveo.manonparle.adapter;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import org.eu.nveo.manonparle.db.Database;
import org.eu.nveo.manonparle.db.DatabaseException;
import org.eu.nveo.manonparle.model.Group;
import org.eu.nveo.manonparle.view.GroupGrid;

public class GroupGridAdapter extends BaseAdapter {

    private String tag = "GroupGridAdapter";

    private Group[] groups;
    private boolean[] checked;
    private Context ctx;
    private int basedPadding = 2;

    public GroupGridAdapter(Context context ){
        ctx = context;
        try {
            groups = Database.getConnection().group().all();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        checked = new boolean[groups.length];
        for (int i = 0; i < checked.length; i++) {
            checked[i] = false;
        }
    }

    @Override
    public int getCount() {
        return groups.length;
    }

    @Override
    public Object getItem(int position) {
        return groups[position];
    }

    @Override
    public long getItemId(int position) {
        return groups[position].getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GridView grid = (GridView) parent;
        GroupGrid groupGrid = new GroupGrid( ctx );
        Group g = groups[position];
        groupGrid.setGroup( g );
        groupGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupGrid group = (GroupGrid)  v;
                checked[position] = group.toggleChecked();

            }
        });
        //checked[position] = groupGrid.isChecked();
        groupGrid.setChecked( checked[position]);


        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, basedPadding, ctx.getResources().getDisplayMetrics());
        int width = (parent.getWidth() / grid.getNumColumns()) - (2 * padding);
        groupGrid.setLayoutParams(new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));
        groupGrid.setPadding(padding, padding, padding, padding);

        return groupGrid;
    }

    public boolean isChecked( int index ) {
        return checked[ index ];
    }

    public int countChecked(){
        int count = 0;

        for (boolean b : checked) {
            if( b ) {
                count++;
            }
        }

        Log.v( tag, "nombre d'picto checker :" + count);

        return count;
    }
}
