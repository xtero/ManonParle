package org.eu.fr.nveo.manonparle;

import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;

public class Barycentre {
    private ArrayList<Float> xCoords;
    private ArrayList<Float> yCoords;
    private String tag = "Barycentre";

    Barycentre(){
        Log.v( tag, "Initialize coords ");
        xCoords = new ArrayList<>();
        yCoords = new ArrayList<>();
    }

    Barycentre( float firstX, float firstY ){
        this();
        add( firstX, firstY );
    }

    public int nbCoords(){
        return xCoords.size();
    }

    public int add( float x, float y ) {
        xCoords.add( x );
        yCoords.add( y );
        return nbCoords();
    }

    public void set( int index, float x, float y ) throws Exception {
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
}
