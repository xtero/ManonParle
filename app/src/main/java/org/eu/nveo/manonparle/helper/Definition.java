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

    public int countItem(){
        return getItems().length();
    }

    public int countItemWithoutSound(){
        int itemWithoutSound = 0;
        JSONArray items = getItems();
        for( int i = 0; i < items.length(); i++ ){
            try {
                JSONObject item = items.getJSONObject( i );
                String audio = item.getString("audio");
                if( audio.equals("null")){
                    itemWithoutSound++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return itemWithoutSound;
    }

    public int countGroup(){
        return getGroups().length();
    }

    public int countRItemGroup(){
        int count = 0;
        JSONArray groups = getRItemGroups();
        for( int i = 0; i < groups.length(); i++ ){
            JSONObject group = null;
            try {
                group = groups.getJSONObject( i );
                JSONArray items = group.getJSONArray("items");
                count += items.length();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    public boolean hasItemWithoutSound(){
        return countItemWithoutSound() > 0;
    }


    public JSONArray getItems(){
        JSONArray obj = null;
        try {
             obj = def.getJSONArray("items");
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


    public JSONArray getRItemGroups() {
        JSONArray obj = null;
        try {
            obj = def.getJSONArray("links")               ;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

}
