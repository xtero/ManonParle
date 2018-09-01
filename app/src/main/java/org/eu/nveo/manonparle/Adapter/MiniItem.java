package org.eu.nveo.manonparle.Adapter;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import org.eu.nveo.manonparle.View.MiniItemView;
import org.eu.nveo.manonparle.db.Database;
import org.eu.nveo.manonparle.db.DatabaseException;
import org.eu.nveo.manonparle.db.ItemDao;
import org.eu.nveo.manonparle.db.ItemDatabase;
import org.eu.nveo.manonparle.model.Item;

public class MiniItem extends BaseAdapter {
    ItemDao mDao;
    Context mContext;
    int mGroupId;
    Item[] items;
    int basedPadding = 3;
    int debugFactor = 10;

    public MiniItem ( Context ctx, int groupId ){
        mContext = ctx;
        mGroupId = groupId;
        ItemDatabase db = null;
        try {
            db = Database.getConnection();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        mDao = db.itemDao();
        //TODO: make it dependant of mGroupId
        items = mDao.items();

    }

    @Override
    public int getCount() {
        return items.length * debugFactor;
    }

    @Override
    public Object getItem(int position) {
        return items[ position % items.length ];
    }

    @Override
    public long getItemId(int position) {
        return items[ position % items.length ].getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        GridView grid = (GridView) parent;

        MiniItemView img;
        if( convertView == null ){
            img = new MiniItemView( mContext );
        } else {
            img = (MiniItemView) convertView;
        }

        Item item = items[ position % items.length ];

        int padding = (int) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, basedPadding, mContext.getResources().getDisplayMetrics() );
        int width = ( parent.getWidth() / grid.getNumColumns() ) - ( 2 * padding ) ;

        img.setItem( item );
        img.setLayoutParams( new ViewGroup.LayoutParams( width, width) );
        img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        img.setPadding( padding, padding, padding, padding );
        img.setTextColor(Color.WHITE);

        return img;
    }
}
