package org.eu.nveo.manonparle.helper;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;

public class ImageUtils {
    public static Uri resImageUri(int resId, Context ctx){
        Resources res = ctx.getResources();

        String pack = res.getResourcePackageName(resId);
        String type = res.getResourceTypeName(resId);
        String scheme = ContentResolver.SCHEME_ANDROID_RESOURCE;
        String name = res.getResourceEntryName(resId);

        return Uri.parse( scheme +"://"+ pack +"/"+ type +"/"+ name );
    }

    public static int optimalFontColor( int color ){
        int red = Color.red( color );
        int green = Color.green( color );
        int blue = Color.blue( color );

        int trigger = ( ( red * 299 ) + ( green * 587 ) + ( blue * 114 ) ) / 1000;

        if( trigger < 125 ) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
    }
}
