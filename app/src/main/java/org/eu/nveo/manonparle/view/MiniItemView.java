package org.eu.nveo.manonparle.view;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.eu.nveo.manonparle.R;
import org.eu.nveo.manonparle.model.Item;

public class MiniItemView extends LinearLayout implements GestureDetector.OnGestureListener {

    private String tag = "MiniItemView";

    private ImageView mImage;
    private LinearLayout mLayout;
    private TextView mLabel;
    private Item mItem;
    private Context mContext;
    private GestureDetectorCompat mGest;

    private static Handler handler = new Handler();

    public MiniItemView(Context context) {
        super(context);
        initializeViews(context);
    }

    public MiniItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public MiniItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    private void initializeViews (Context context) {;
        mContext = context;
        this.setOrientation( LinearLayout.VERTICAL );
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_miniitem, this);
        mGest = new GestureDetectorCompat( mContext, this, handler );
    }

    public long getItemId(){
        return mItem.getId();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mImage = this.findViewById( R.id.miniitem_image );
        mLabel = this.findViewById( R.id.miniitem_text );
    }

    public void setItem( Item item ){
        mItem = item;

        mImage = this.findViewById( R.id.miniitem_image );
        mImage.setImageURI( mItem.getImageUri( mContext ) );

        mLabel = this.findViewById( R.id.miniitem_text );
        mLabel.setText( mItem.getName() );
        mLabel.setGravity(Gravity.CENTER);
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params ){
        super.setLayoutParams( params );
        int width = params.width;
        mImage.setLayoutParams( new LayoutParams(width, width) );
    }

    public void setScaleType( ImageView.ScaleType scaleType ){
        mImage = this.findViewById( R.id.miniitem_image );
        mImage.setScaleType( scaleType );
    }
    public void setTextColor( int textColor ) {
        mLabel = this.findViewById(R.id.miniitem_text);
        mLabel.setTextColor( textColor );
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
        String uri = mItem.getImageUri( mContext ).toString();
        ClipData.Item curi =  new ClipData.Item( uri.subSequence(0, uri.length() ) );
        ClipData.Item cid = new ClipData.Item( Long.toString( mItem.getId()) );
        String[] mimes = new String[2];
        mimes[0] = "text/plain";
        mimes[1] = "text/plain";
        ClipDescription desc = new ClipDescription("itemUri", mimes );
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
        if( mGest.onTouchEvent( event ) ){
            return false;
        } else {
            return true;
        }
    }

}
