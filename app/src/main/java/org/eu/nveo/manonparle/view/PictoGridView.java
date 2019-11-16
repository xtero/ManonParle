package org.eu.nveo.manonparle.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.eu.nveo.manonparle.R;
import org.eu.nveo.manonparle.model.Picto;

public class PictoGridView extends LinearLayout {

    private ImageView mImage;
    private TextView mLabel;
    protected Picto mPicto;
    protected Context mContext;

    public PictoGridView(Context context) {
        super(context);
        initializeViews(context);
    }

    public PictoGridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public PictoGridView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    protected void initializeViews (Context context) {
        mContext = context;
        this.setOrientation( LinearLayout.VERTICAL );
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_picto_grid_view, this);
    }

    public long getPictoId(){
        return mPicto.getId();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mImage = this.findViewById( R.id.minipicto_image);
        mLabel = this.findViewById( R.id.minipicto_text);
    }

    public void setPicto(Picto picto){
        mPicto = picto;

        mImage = this.findViewById( R.id.minipicto_image);
        mImage.setImageURI( mPicto.getImageUri() );

        mLabel = this.findViewById( R.id.minipicto_text);
        mLabel.setText( mPicto.getName() );
        mLabel.setGravity(Gravity.CENTER);
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params ){
        super.setLayoutParams( params );
        int width = params.width;
        mImage.setLayoutParams( new LayoutParams(width, width) );
    }

    public void setScaleType( ImageView.ScaleType scaleType ){
        mImage = this.findViewById( R.id.minipicto_image);
        mImage.setScaleType( scaleType );
    }
    public void setTextColor( int textColor ) {
        mLabel = this.findViewById(R.id.minipicto_text);
        mLabel.setTextColor( textColor );
    }


}
