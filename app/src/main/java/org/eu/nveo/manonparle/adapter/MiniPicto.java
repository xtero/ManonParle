package org.eu.nveo.manonparle.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.eu.nveo.manonparle.model.Picto;
import org.eu.nveo.manonparle.view.MiniPictoView;
import org.eu.nveo.manonparle.db.Database;
import org.eu.nveo.manonparle.db.DatabaseException;

public class MiniPicto extends BaseAdapter implements Filterable  {
    private Context mContext;
    private long mGroupId;
    private Picto[] pictos;
    private int basedPadding = 2;

    public MiniPicto(Context ctx, long groupId ){
        mContext = ctx;
        mGroupId = groupId;
        try {
            pictos = Database.getConnection().picto().byGroupId( mGroupId );
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getCount() {
        return pictos.length ;
    }

    @Override
    public Object getItem(int position) {
        return pictos[ position % pictos.length ];
    }

    @Override
    public long getItemId(int position) {
        return pictos[ position % pictos.length ].getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        GridView grid = (GridView) parent;

        MiniPictoView img = new MiniPictoView(mContext);
        Picto picto = pictos[position % pictos.length];
        img.setPicto(picto);
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
                Picto[] i = new Picto[0];
                try {
                    i = Database.getConnection().picto().byGroupIdLike( mGroupId, constraint.toString() );
                } catch (DatabaseException e) {
                    e.printStackTrace();
                }
                r.count = i.length;
                r.values = i;
                return r;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results){
                pictos = (Picto[]) results.values;
            }
        };
    }
}
