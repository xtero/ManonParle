package org.eu.nveo.manonparle.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import org.eu.nveo.manonparle.SelectPictos;
import org.eu.nveo.manonparle.db.Database;
import org.eu.nveo.manonparle.db.DatabaseException;
import org.eu.nveo.manonparle.model.Group;
import org.eu.nveo.manonparle.view.GroupListView;

public class GroupListAdapter extends BaseAdapter {
    private Group[] groups;
    private Context ctx;


    public GroupListAdapter(Context context ){
        ctx = context;
        try {
            groups = Database.getConnection().group().all();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }
    @Override
    public int getCount() {
        return groups.length;
    }

    @Override
    public Object getItem(int position) {
        return groups[ position ];
    }

    @Override
    public long getItemId(int position) {
        return groups[position].getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final GroupListView groupListView = new GroupListView( ctx );
        Group g = groups[position];
        groupListView.setGroup( g );
        groupListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( ctx, SelectPictos.class);
                i.putExtra("groupId", groupListView.getContentDescription() );
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(i);
            }
        });
        return groupListView;
    }
}
