package org.eu.fr.nveo.manonparle.View;

import android.content.Context;
import android.util.AttributeSet;

public class SquaredImageButton extends android.support.v7.widget.AppCompatImageButton {

    public SquaredImageButton(Context context) {
        super(context);
    }

    public SquaredImageButton(Context context, AttributeSet attrs ) {
        super(context, attrs);
    }

    public SquaredImageButton(Context context, AttributeSet attrs, int defStyle ) {
        super(context, attrs, defStyle );
    }

    @Override
    protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }
}
