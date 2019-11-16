package org.eu.nveo.manonparle.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import org.apache.commons.io.FileUtils;
import org.eu.nveo.manonparle.helper.Folders;
import org.eu.nveo.manonparle.helper.MetaPackage;
import org.eu.nveo.manonparle.helper.RepoUpdater;
import org.json.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Entity
public class Repo {

    public static final Integer STATUS_OK = 0;

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private String url;
    private Long lastRefresh;
    private Integer lastRefreshStatus;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        // Ensure there is no trailing /
        url = url.replaceAll("/$", "");
        this.url = url;
    }

    public File getIndexFile(){
        return new File( Folders.getIndexesFolder(), name + ".json" );
    }

    public String getIndexUrl() {
        return url+"/index.json";
    }

    public Long getLastRefresh() {
        return lastRefresh;
    }

    public void setLastRefresh( long lastRefresh) {
        this.lastRefresh = lastRefresh;
    }

    public Integer getLastRefreshStatus() {
        return lastRefreshStatus;
    }

    public void setLastRefreshStatus(Integer lastRefreshStatus) {
        this.lastRefreshStatus = lastRefreshStatus;
    }

    public void update() {
        RepoUpdater update = new RepoUpdater();
        update.execute( this );
        try {
            update.get( 10, TimeUnit.SECONDS );
        } catch (ExecutionException | InterruptedException | TimeoutException e ) {
            e.printStackTrace();
        }
    }

    public ArrayList<MetaPackage> metaPacksFromJson() {
        String str = null;
        ArrayList<MetaPackage> packs = new ArrayList<>();
        try {
            str = FileUtils.readFileToString( this.getIndexFile(), "utf8");
            JSONArray index = new JSONArray( str );
            for( int i = 0; i < index.length(); i++ ){
                JSONObject obj = index.getJSONObject( i );
                MetaPackage pack = new MetaPackage( this , obj );
                packs.add(pack);
            }
            return packs;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
