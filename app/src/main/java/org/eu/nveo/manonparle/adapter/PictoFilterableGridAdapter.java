package org.eu.nveo.manonparle.adapter;

import android.content.Context;
import android.widget.*;
import org.eu.nveo.manonparle.db.Database;
import org.eu.nveo.manonparle.db.DatabaseException;
import org.eu.nveo.manonparle.model.Picto;

import java.util.ArrayList;
import java.util.Arrays;

public class PictoFilterableGridAdapter extends PictoGridAdapter implements Filterable {

    public PictoFilterableGridAdapter(Context ctx, long groupId) {
        super(ctx, groupId);
        forceViewRefresh = true;
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults r = new FilterResults();
                Picto[] i = new Picto[0];
                try {
                    if( mGroupId == ALL_GROUP ) {
                        i = Database.getConnection().picto().allLike( constraint.toString() );
                    } else {
                        i = Database.getConnection().picto().byGroupIdLike( mGroupId, constraint.toString() );
                    }
                } catch (DatabaseException e) {
                    e.printStackTrace();
                }
                r.count = i.length;
                r.values = new ArrayList<>(Arrays.asList(i) );
                return r;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results){
                pictos = (ArrayList<Picto>) results.values;
            }
        };
    }
}
