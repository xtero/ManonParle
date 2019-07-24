package org.eu.nveo.manonparle.view;

import android.content.Context;
import android.util.AttributeSet;

public class SquaredImageButtonView extends android.support.v7.widget.AppCompatImageButton {

    public SquaredImageButtonView(Context context) {
        super(context);
    }

    public SquaredImageButtonView(Context context, AttributeSet attrs ) {
        super(context, attrs);
    }

    public SquaredImageButtonView(Context context, AttributeSet attrs, int defStyle ) {
        super(context, attrs, defStyle );
    }

    @Override
    protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }

    @Override
    public boolean performClick(){
        return super.performClick();
    }
}
