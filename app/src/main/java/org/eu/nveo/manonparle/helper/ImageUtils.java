package org.eu.nveo.manonparle.helper;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;

public class ImageUtils {
    public static Uri resImageUri(int resId, Context ctx){
        Resources res = ctx.getResources();

        String pack = res.getResourcePackageName(resId);
        String type = res.getResourceTypeName(resId);
        String scheme = ContentResolver.SCHEME_ANDROID_RESOURCE;
        String name = res.getResourceEntryName(resId);

        Uri path = Uri.parse( scheme +"://"+ pack +"/"+ type +"/"+ name );
        return path;
    }
}
