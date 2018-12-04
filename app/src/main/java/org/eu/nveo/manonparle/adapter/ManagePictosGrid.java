package org.eu.nveo.manonparle.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import org.eu.nveo.manonparle.FormPicto;
import org.eu.nveo.manonparle.model.Picto;
import org.eu.nveo.manonparle.view.PictoGridView;

public class ManagePictosGrid extends PictoFilterableGridAdapter {

    private String tag = "Adapter: PictoFilterableGridAdapter";

    public ManagePictosGrid(Context ctx ) {
        super(ctx, ALL_GROUP );

        Picto add = getStaticPicto( PICTO_ADD );
        insertPicto( 0 , add );

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView( position, convertView, parent );
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictoGridView picto = (PictoGridView) v;
                long id = picto.getPictoId();
                Log.v(tag, Long.toString(id) );
                Intent i = new Intent( mContext, FormPicto.class );
                i.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                i.putExtra( "pictoId", id );
                mContext.startActivity( i );
            }
        });
        return view;
    }
}
