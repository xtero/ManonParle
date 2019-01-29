package org.eu.nveo.manonparle.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Repo {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private String url;
    private Integer lastRefresh;

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
        this.url = url;
    }

    public Integer getLastRefresh() {
        return lastRefresh;
    }

    public void setLastRefresh(int lastRefresh) {
        this.lastRefresh = lastRefresh;
    }
}
