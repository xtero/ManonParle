package org.eu.nveo.manonparle.view;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.eu.nveo.manonparle.R;
import org.eu.nveo.manonparle.model.Group;

public class GroupGrid extends FrameLayout {
    private ImageView mImage;
    private TextView mLabel;
    private LinearLayout container;
    private Context ctx;
    private boolean checked = false;
    private long groupId;

    public GroupGrid(Context context) {
        super(context);
        initializeLayout( context );
    }

    public GroupGrid(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeLayout( context );
    }

    public GroupGrid(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeLayout( context );
    }

    private void initializeLayout(Context context) {
        ctx = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_group_grid, this);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mImage = this.findViewById( R.id.group_image );
        mLabel = this.findViewById( R.id.group_text );
        container = this.findViewById( R.id.container );

        container.setBackgroundColor( getResources().getColor( R.color.baseOverlay ) );
    }

    public void setGroup( Group g ){
        this.setContentDescription( Long.toString( g.getId() ) );
        this.setBackground(getResources().getDrawable(R.drawable.bg_corner_rounded) );

        mImage = this.findViewById( R.id.group_image );
        mLabel = this.findViewById( R.id.group_text );

        mImage.setImageURI( g.getImageUri( ctx ));

        mLabel.setText( g.getName() );
        mLabel.setTextColor( getResources().getColor(R.color.colorBaseText) );
        mLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        groupId = g.getId();
    }

    public TextView getText(){
        return this.findViewById( R.id.group_list_text );
    }

    public ImageView getImage(){
        return this.findViewById( R.id.group_list_image );
    }

    public long getGroupId(){
        return groupId;
    }

    public void setChecked( boolean value ) {
        boolean previous = checked;
        checked = value;
        int speed = 300;
        int colorFrom;
        int colorTo;
        if (checked) {
            colorTo = getResources().getColor(R.color.glowConfirm);
            colorFrom = Color.BLACK;
        } else {
            colorTo = Color.BLACK;
            colorFrom = getResources().getColor(R.color.glowConfirm);
        }
        if( previous != checked ) {
            AnimatedVectorDrawable animate = new AnimatedVectorDrawable();
            ObjectAnimator anim = ObjectAnimator.ofInt(this, "backgroundColor", colorFrom, colorTo );
            anim.setDuration( speed );
            anim.setEvaluator( new ArgbEvaluator() );
            anim.setRepeatMode( ValueAnimator.RESTART );
            anim.setRepeatCount( 0 );
            anim.start();
        }
    }

    public boolean toggleChecked(){
        setChecked( ! isChecked() );
        return isChecked();
    }

    public boolean isChecked(){
        return checked;
    }
}
