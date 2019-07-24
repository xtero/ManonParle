package org.eu.nveo.manonparle;

import android.app.Application;
import android.content.Context;

public class ManonParle extends Application {
    private static Application app;

    public static Application getApplication(){
        return app;
    }

    public static Context getContext(){
        return getApplication().getApplicationContext();
    }

    @Override
    public void onCreate(){
        app = this;
        super.onCreate();
    }
}
