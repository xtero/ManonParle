package org.eu.nveo.manonparle.helper;

import org.apache.commons.io.FileUtils;
import org.eu.nveo.manonparle.model.Repo;
import org.json.JSONException;
import org.json.JSONObject;

public class MetaPackage {
    private int nbGroup;
    private int nbPictos;
    private String name;
    private String md5sum;
    private int packageFormat;
    private String version;
    private int filesize;
    private Repo repo;

    public int getNbGroup() {
        return nbGroup;
    }

    public void setNbGroup(int nbgroup) {
        this.nbGroup = nbgroup;
    }

    public int getNbPictos() {
        return nbPictos;
    }

    public void setNbPictos(int nbPictos) {
        this.nbPictos = nbPictos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMd5sum() {
        return md5sum;
    }

    public void setMd5sum(String md5sum) {
        this.md5sum = md5sum;
    }

    public int getPackageFormat() {
        return packageFormat;
    }

    public void setPackageFormat(int packageFormat) {
        this.packageFormat = packageFormat;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHumanReadableSizeString(){
        return FileUtils.byteCountToDisplaySize( filesize );
    }

    public int getFilesize() {
        return filesize;
    }

    public void setFilesize(int filesize) {
        this.filesize = filesize;
    }

    public Repo getRepo() {
        return repo;
    }

    //public MetaPackage(){}

    public MetaPackage( Repo repo, JSONObject obj) {
        this.repo = repo;
        try {
            md5sum = obj.getString("md5sum");
            name = obj.getString("name");
            packageFormat = obj.getInt("packageFormat");
            nbGroup = obj.getInt("nbgroups");
            nbPictos = obj.getInt("nbpictos");
            version = obj.getString("version");
            filesize = obj.getInt("filesize");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
