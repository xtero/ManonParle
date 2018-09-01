package org.eu.nveo.manonparle.Helper;

import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;

public class Barycentre {
    private ArrayList<Float> xCoords;
    private ArrayList<Float> yCoords;
    private String tag = "Barycentre";
    private int pointerMax = 0;

    public Barycentre(){
        Log.v( tag, "Initialize coords ");
        xCoords = new ArrayList<>();
        yCoords = new ArrayList<>();
    }

    private int nbCoords(){
        return xCoords.size();
    }

    private int add( float x, float y ) {
        xCoords.add( x );
        yCoords.add( y );
        return nbCoords();
    }

    private void set( int index, float x, float y ) throws Exception {
        if( index > nbCoords() ) throw new Exception();
        xCoords.set( index, x );
        yCoords.set( index, y );
    }

    public MotionEvent.PointerCoords get(){
        float xSum = 0;
        for (Float xCoord : xCoords) {
            xSum += xCoord;
        }
        float ySum = 0;
        for (Float yCoord : yCoords) {
            ySum += yCoord;
        }
        MotionEvent.PointerCoords coords = new MotionEvent.PointerCoords();
        coords.setAxisValue( MotionEvent.AXIS_X , xSum / nbCoords() );
        coords.setAxisValue( MotionEvent.AXIS_Y , ySum / nbCoords() );
        return coords;
    }

    public void updateBarycentre( MotionEvent event ){
        int nbPointer = event.getPointerCount();
        if( nbPointer >= pointerMax ) {
            pointerMax = nbPointer;
            int i = 0;
            int barySize = nbCoords();
            while( i < nbPointer ) {
                float x = event.getX( i );
                float y = event.getY( i );
                if( i == barySize ) {
                    add( x, y );
                    barySize = nbCoords();
                } else {
                    try {
                        set( i , x, y  );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                i++;
            }
        }
    }
}
