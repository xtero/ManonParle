package org.eu.nveo.manonparle.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static org.eu.nveo.manonparle.helper.Preferences.DEFAULT_SKEW_FACTOR;
import static org.eu.nveo.manonparle.helper.Preferences.DEFAULT_SKEW_SIDE;
import static org.eu.nveo.manonparle.helper.Preferences.GLOBAL_PREFS;

public class Barycentre {
    private List<Float[]> coords;
    private String tag = "Barycentre";
    private int pointerMax = 0;
    private SharedPreferences prefs;

    public Barycentre(Context ctx){
        prefs = ctx.getSharedPreferences( GLOBAL_PREFS, MODE_PRIVATE);

        Log.v( tag, "Initialize coords ");
        coords = new ArrayList<>();
    }

    public Float[] get(){

        Float[] barycentre = new Float[2];
        barycentre[0] = (float) 0.0 ;
        barycentre[1] = (float) 0.0 ;

        for (Float[] coord : coords) {
            barycentre[0] += coord[0];
            barycentre[1] += coord[1];
        }

        barycentre[0] = barycentre[0] / coords.size();
        barycentre[1] = barycentre[1] / coords.size();

        Log.v( tag, "Barycentre x:"+barycentre[0]+" y:"+barycentre[1] );
        return barycentre;
    }

    // Sample of computation
    // skew_factor = 10;
    // number of fingers : 3
    // 1 -> 1.10; 10 * ( 1 - 0 / 2 )
    // 2 -> 1.05; 10 * ( 1 - 1 / 2 )
    // 3 -> 1;    10 * ( 1 - 2 / 2 )

    private float getWeight(int indice){
        // skew_factor is an integer expressed in percent
        int skew_factor = prefs.getInt( "skew_factor", DEFAULT_SKEW_FACTOR );
        int nb = coords.size();

        float weight = skew_factor;
        if( nb != 1 ){
            weight = skew_factor * ( 1 - ( indice / ( nb -  1 ) ) );
        }
        return ( 1 + ( weight / 10 ) );

    }

    public Float[] getSkewed(){

        Float[] barycentre = new Float[2];
        barycentre[0] = (float) 0.0 ;
        barycentre[1] = (float) 0.0 ;

        Collections.sort(coords, new Comparator<Float[]>() {
            @Override
            public int compare(Float[] o1, Float[] o2) {
                int skew_side = prefs.getInt("skew_side", DEFAULT_SKEW_SIDE );
                if( o1[0] > o2[0] ){
                    return -skew_side;
                } else {
                    return skew_side ;
                }
            }
        });

        float weightSum = 0;
        int indice = 0;
        for (Float[] coord : coords) {
            float weight = getWeight( indice );
            weightSum += weight;
            barycentre[0] += coord[0] * weight;
            barycentre[1] += coord[1];
            indice++;
        }

        barycentre[0] = barycentre[0] / weightSum;
        barycentre[1] = barycentre[1] / coords.size();

        get();
        Log.v( tag, "Skewed barycentre x:"+barycentre[0]+" y:"+barycentre[1] );
        return barycentre;
    }

    public void updateBarycentre( MotionEvent event ){
        int nbPointer = event.getPointerCount();
        if( nbPointer >= pointerMax ) {
            pointerMax = nbPointer;
            int i = 0;
            int barySize = coords.size();
            while( i < nbPointer ) {
                Float[] array = new Float[2];
                array[0] = event.getX( i );
                array[1] = event.getY( i );
                if( i == barySize ) {
                    coords.add( array );
                    barySize = coords.size();
                } else {
                    coords.set( i , array  );
                }
                i++;
            }
        }
    }
}
