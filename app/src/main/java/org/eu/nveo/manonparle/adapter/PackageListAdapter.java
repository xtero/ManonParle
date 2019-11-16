package org.eu.nveo.manonparle.adapter;

import android.database.DataSetObserver;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import org.eu.nveo.manonparle.ManonParle;
import org.eu.nveo.manonparle.db.Database;
import org.eu.nveo.manonparle.db.DatabaseException;
import org.eu.nveo.manonparle.helper.MetaPackage;
import org.eu.nveo.manonparle.model.Repo;
import org.eu.nveo.manonparle.view.PackageListView;

import java.util.ArrayList;

public class PackageListAdapter extends BaseAdapter {

    private static String tag = "PackageListAdapter";
    Repo[] repos;
    ArrayList<MetaPackage> packs;

    public PackageListAdapter(){
        packs = new ArrayList<>();
        try {
            repos = Database.getConnection().repo().all();
            for( Repo repo: repos ){
                ArrayList<MetaPackage> repoPacks = repo.metaPacksFromJson();
                packs.addAll( repoPacks );
            }
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getCount() {
        return packs.size();
    }

    @Override
    public Object getItem(int position) {
        return packs.get( position );
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListView list = (ListView) parent;

        MetaPackage pack = packs.get( position );

        Log.d( tag, pack.getName() );

        PackageListView packView;
        if( convertView != null )  {
            packView = (PackageListView) convertView;
        } else {
            packView = new PackageListView(ManonParle.getContext());
        }

        Log.d( tag, "Rendering a new package");
        packView.setPackage( pack );

        return packView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return packs.size() == 0;
    }
}
