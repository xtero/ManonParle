package org.eu.nveo.manonparle.adapter;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import org.eu.nveo.manonparle.ManonParle;
import org.eu.nveo.manonparle.db.Database;
import org.eu.nveo.manonparle.db.DatabaseException;
import org.eu.nveo.manonparle.model.Group;
import org.eu.nveo.manonparle.view.GroupGridView;

public class GroupGridAdapter extends BaseAdapter {

    private String tag = "GroupGridAdapter";

    private Group[] groups;
    private GroupGridView[] groupGirds;
    private boolean[] checked;
    private int basedPadding = 2;

    public GroupGridAdapter( ){
        try {
            groups = Database.getConnection().group().all();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        checked = new boolean[groups.length];
        for (int i = 0; i < checked.length; i++) {
            checked[i] = false;
        }
        groupGirds = new GroupGridView[groups.length];
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
        Context ctx = ManonParle.getContext();
        GridView grid = (GridView) parent;
        if( convertView == null ) {
            groupGirds[position] = new GroupGridView( ctx );
        }
        GroupGridView groupGridView = groupGirds[position];
        Group g = groups[position];
        groupGridView.setGroup( g );
        groupGridView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupGridView group = (GroupGridView)  v;
                checked[position] = group.toggleChecked();

            }
        });
        groupGridView.setChecked( checked[position] );


        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, basedPadding, ctx.getResources().getDisplayMetrics());
        int width = (parent.getWidth() / grid.getNumColumns()) - (2 * padding);
        groupGridView.setLayoutParams(new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));
        groupGridView.setPadding(padding, padding, padding, padding);

        return groupGridView;
    }

    public boolean isChecked( int index ) {
        return groupGirds[ index ].isChecked();
    }

    public boolean toogleChecked( int index ) {
        return groupGirds[index].toggleChecked();
    }

    public void setChecked( int index, boolean value ) {
        checked[ index ] = value;
        // FIXME avoid this crappy way to sneak around not initialized view
        if( groupGirds[index] != null ) {
            groupGirds[index].setChecked(value);
        }
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
