package org.eu.nveo.manonparle.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.eu.nveo.manonparle.View.MiniItemView;
import org.eu.nveo.manonparle.db.Database;
import org.eu.nveo.manonparle.db.DatabaseException;
import org.eu.nveo.manonparle.model.Item;

public class MiniItem extends BaseAdapter implements Filterable  {
    private Context mContext;
    private long mGroupId;
    private Item[] items;
    private int basedPadding = 2;

    public MiniItem ( Context ctx, long groupId ){
        mContext = ctx;
        mGroupId = groupId;
        try {
            items = Database.getConnection().itemDao().itemsByGroupId( mGroupId );
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getCount() {
        return items.length ;
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

        MiniItemView img = new MiniItemView(mContext);
        Item item = items[position % items.length];
        img.setItem(item);
        img.setScaleType(ImageView.ScaleType.FIT_CENTER);
        img.setTextColor(Color.WHITE);

        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, basedPadding, mContext.getResources().getDisplayMetrics());
        int width = (parent.getWidth() / grid.getNumColumns()) - (2 * padding);
        img.setLayoutParams(new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));
        img.setPadding(padding, padding, padding, padding);

        return img;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults r = new FilterResults();
                Item[] i = new Item[0];
                try {
                    i = Database.getConnection().itemDao().itemsByGroupIdLike( mGroupId, constraint.toString() );
                } catch (DatabaseException e) {
                    e.printStackTrace();
                }
                r.count = i.length;
                r.values = i;
                return r;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results){
                items = (Item[]) results.values;
            }
        };
    }
}
