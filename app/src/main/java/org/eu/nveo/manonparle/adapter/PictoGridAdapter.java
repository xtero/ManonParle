package org.eu.nveo.manonparle.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import org.eu.nveo.manonparle.db.Database;
import org.eu.nveo.manonparle.db.DatabaseException;
import org.eu.nveo.manonparle.model.Picto;
import org.eu.nveo.manonparle.view.PictoGridView;

import java.util.ArrayList;
import java.util.Arrays;

public class PictoGridAdapter extends BaseAdapter {

    protected Context mContext;
    protected long mGroupId;
    protected ArrayList<Picto> pictos;
    protected int basedPadding = 2;
    protected boolean forceViewRefresh = false;

    public static final int ALL_GROUP = -1 ;

    public static final int PICTO_ADD = -1;

    private static String getPictoLabel( int pictoType ) {
        String output = "Unknown";
        switch ( pictoType) {
            case PICTO_ADD:
                output = "Ajouter un picto";
                break;
        }
        return output;
    }

    public static Picto getStaticPicto( int pictoType ) {
        String msg = getPictoLabel( pictoType );
        return new Picto( pictoType, msg, false );
    }

    public PictoGridAdapter(Context ctx, long groupId ){
        mContext = ctx;
        mGroupId = groupId;
        Picto[] tmp = null;
        try {
            if( groupId == ALL_GROUP ) {
                tmp = Database.getConnection().picto().all();
            } else {
                tmp = Database.getConnection().picto().byGroupId(mGroupId);
            }
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        pictos = new ArrayList<>( Arrays.asList(tmp) );
    }

    @Override
    public int getCount() {
        return pictos.size() ;
    }

    @Override
    public Object getItem(int position) {
        return pictos.get( position );
    }

    @Override
    public long getItemId(int position) {
        return pictos.get( position ).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        GridView grid = (GridView) parent;

        PictoGridView img;
        if( convertView != null && ! forceViewRefresh ) {
            img = (PictoGridView) convertView;
        } else {
            img = new PictoGridView(mContext);
        }

        Picto picto = pictos.get( position );
        img.setPicto(picto);
        img.setScaleType(ImageView.ScaleType.FIT_CENTER);
        img.setTextColor(Color.WHITE);

        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, basedPadding, mContext.getResources().getDisplayMetrics());
        int width = (parent.getWidth() / grid.getNumColumns()) - (2 * padding);
        img.setLayoutParams(new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));
        img.setPadding(padding, padding, padding, padding);

        return img;
    }

    public void insertPicto( int index, Picto added ) {
        pictos.add( index, added );
    }
}
