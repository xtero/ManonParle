package org.eu.nveo.manonparle.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class LinearIntercept extends LinearLayout {
    private final String tag = "LinearIntercept";

    public LinearIntercept(Context ctx, AttributeSet attr ){
        super( ctx, attr );
    }
    public LinearIntercept(Context ctx ){
        super( ctx, null );
    }

    @Override
    public boolean onInterceptTouchEvent( MotionEvent event ){

        Log.v( tag, MotionEvent.actionToString( event.getAction() ) );

        // Fire new event
        if ( event.getAction() == MotionEvent.ACTION_DOWN && event.getDownTime() == 1 ) {
            Log.v( tag, "Simulated event");
            return false;
        }

        return true;
    }
}
