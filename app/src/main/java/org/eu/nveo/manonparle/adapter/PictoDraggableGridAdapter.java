package org.eu.nveo.manonparle.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.eu.nveo.manonparle.model.Picto;
import org.eu.nveo.manonparle.view.PictoDraggableGridView;

public class PictoDraggableGridAdapter extends PictoFilterableGridAdapter {

    public PictoDraggableGridAdapter(Context ctx, long groupId ){
        super(ctx, groupId );
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        GridView grid = (GridView) parent;

        PictoDraggableGridView img = new PictoDraggableGridView(mContext);
        Picto picto = pictos.get(position);
        img.setPicto(picto);
        img.setScaleType(ImageView.ScaleType.FIT_CENTER);
        img.setTextColor(Color.WHITE);

        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, basedPadding, mContext.getResources().getDisplayMetrics());
        int width = (parent.getWidth() / grid.getNumColumns()) - (2 * padding);
        img.setLayoutParams(new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));
        img.setPadding(padding, padding, padding, padding);

        return img;
    }

}
