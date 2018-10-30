package org.eu.nveo.manonparle.helper;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class Definition {
    private JSONObject def;

    public Definition( InputStream is ) throws IOException, JSONException {
        String jsonStr = IOUtils.toString( is, "UTF-8" );
        def = new JSONObject( jsonStr );
    }

    public String getName(){
        String name = null;
        try {
            name = def.getJSONObject("meta").getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return name;
    }

    public String getLabel(){
        String label = null;
        try {
            label = def.getJSONObject("meta").getString("label");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return label;
    }

    public int getVersion(){
        int version = -1;
        try {
            version = def.getJSONObject("meta").getInt("version");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return version;
    }

    public int countPicto(){
        return getPictos().length();
    }

    public int countPictoWithoutSound(){
        int pictoWithoutSound = 0;
        JSONArray pictos = getPictos();
        for( int i = 0; i < pictos.length(); i++ ){
            try {
                JSONObject picto = pictos.getJSONObject( i );
                String audio = picto.getString("audio");
                if( audio.equals("null")){
                    pictoWithoutSound++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return pictoWithoutSound;
    }

    public int countGroup(){
        return getGroups().length();
    }

    public int countRPictoGroup(){
        int count = 0;
        JSONArray groups = getRPictoGroups();
        for( int i = 0; i < groups.length(); i++ ){
            JSONObject group = null;
            try {
                group = groups.getJSONObject( i );
                JSONArray pictos = group.getJSONArray("pictos");
                count += pictos.length();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    public boolean hasPictoWithoutSound(){
        return countPictoWithoutSound() > 0;
    }


    public JSONArray getPictos(){
        JSONArray obj = null;
        try {
             obj = def.getJSONArray("pictos");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public JSONArray getGroups() {
        JSONArray obj = null;
        try {
             obj = def.getJSONArray("groups")               ;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }


    public JSONArray getRPictoGroups() {
        JSONArray obj = null;
        try {
            obj = def.getJSONArray("links")               ;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

}
