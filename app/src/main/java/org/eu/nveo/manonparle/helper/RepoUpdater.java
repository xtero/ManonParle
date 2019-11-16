package org.eu.nveo.manonparle.helper;

import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;
import org.eu.nveo.manonparle.db.Database;
import org.eu.nveo.manonparle.db.DatabaseException;
import org.eu.nveo.manonparle.model.Repo;

import java.io.IOException;
import java.util.ArrayList;

public class RepoUpdater extends AsyncTask<Repo, Integer, ArrayList<ArrayList<MetaPackage>>> {
    static  final String tag = "RepoUpdater";
    @Override
    protected ArrayList<ArrayList<MetaPackage>> doInBackground(Repo... repos) {
        ArrayList<ArrayList<MetaPackage>> res = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        for (int i = 0; i < repos.length; i++ ) {
            Repo repo = repos[i];
            Log.d(tag, "Updating repo "+ repo.getName() );
            String url = repo.getIndexUrl();
            Log.d( tag, url );
            Request req = new Request.Builder()
                    .url( url )
                    .build();
            try {
                Log.d(tag, "Downloading index" );
                Response resp = client.newCall( req ).execute();
                String meta = resp.body().string();
                Log.d(tag, "Saving index");
                FileUtils.writeStringToFile( repo.getIndexFile()  , meta, "utf8");
                repo.setLastRefreshStatus(Repo.STATUS_OK);
                repo.setLastRefresh( System.currentTimeMillis() );
                Database.getConnection().repo().update( repo );
                Log.d(tag, "Saving repo object" );
                ArrayList<MetaPackage> packs = repo.metaPacksFromJson();
                res.add( packs );
//                publishProgress( (int) ( ( i / (float) repos.length) * 100 ) );
            } catch (IOException | DatabaseException e) {
                e.printStackTrace();
                return null;
            }
        }
        return res;
    }
}
