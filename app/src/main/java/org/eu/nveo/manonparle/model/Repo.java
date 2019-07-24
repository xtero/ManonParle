package org.eu.nveo.manonparle.model;

import android.app.DownloadManager;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.database.Cursor;
import android.net.Uri;
import org.eu.nveo.manonparle.ManonParle;
import org.eu.nveo.manonparle.helper.Folders;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;

@Entity
public class Repo {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private String url;
    private Integer lastRefresh;
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

    public String getIndexUrl() {
        return url+"/index.json";
    }

    public Integer getLastRefresh() {
        return lastRefresh;
    }

    public void setLastRefresh(int lastRefresh) {
        this.lastRefresh = lastRefresh;
    }

    public Integer getLastRefreshStatus() {
        return lastRefreshStatus;
    }

    public void setLastRefreshStatus(Integer lastRefreshStatus) {
        this.lastRefreshStatus = lastRefreshStatus;
    }

    public void update() {
        DownloadManager mgr = (DownloadManager) ManonParle.getContext().getSystemService(DOWNLOAD_SERVICE);
        Uri indexUrl = Uri.parse( getIndexUrl() );
        Uri indexStorage = Uri.fromFile( new File( Folders.getIndexesFolder(),  name +".json") );
        DownloadManager.Request req = new DownloadManager.Request( indexUrl );
        req.setAllowedNetworkTypes( DownloadManager.Request.NETWORK_MOBILE );
        req.setAllowedNetworkTypes( DownloadManager.Request.NETWORK_WIFI );
        req.setNotificationVisibility( DownloadManager.Request.VISIBILITY_HIDDEN );
        req.setDestinationUri( indexStorage );
        long id = mgr.enqueue( req );
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById( id );
        Cursor res = mgr.query( query );
    }
}
