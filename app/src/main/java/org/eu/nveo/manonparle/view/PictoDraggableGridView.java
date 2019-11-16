package org.eu.nveo.manonparle.view;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.*;

public class PictoDraggableGridView extends PictoGridView implements GestureDetector.OnGestureListener {

    private String tag = "PictoDraggableGridView";

    private GestureDetectorCompat mGest;

    private static Handler handler = new Handler();

    public PictoDraggableGridView(Context context) {
        super(context);
        initializeViews(context);
    }

    public PictoDraggableGridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public PictoDraggableGridView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    @Override
    protected void initializeViews (Context context) {
        super.initializeViews( context );
        mGest = new GestureDetectorCompat( mContext, this, handler );
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        String uri = mPicto.getImageUri().toString();
        ClipData.Item curi =  new ClipData.Item( uri.subSequence(0, uri.length() ) );
        ClipData.Item cid = new ClipData.Item( Long.toString( mPicto.getId()) );
        String[] mimes = new String[2];
        mimes[0] = "text/plain";
        mimes[1] = "text/plain";
        ClipDescription desc = new ClipDescription("pictoUri", mimes );
        ClipData data = new ClipData( desc , curi );
        data.addItem( cid );
        this.startDrag( data, new DragShadowBuilder(this), this, 0 );
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event ){
        return !mGest.onTouchEvent(event);
    }

}
