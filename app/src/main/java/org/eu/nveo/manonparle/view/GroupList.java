package org.eu.nveo.manonparle.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.eu.nveo.manonparle.EditGroup;
import org.eu.nveo.manonparle.R;
import org.eu.nveo.manonparle.helper.ImageUtils;
import org.eu.nveo.manonparle.model.Group;

public class GroupList extends LinearLayout {
    private Context ctx;
    private TextView mLabel;
    private ImageView mImage;
    private Group mGroup;
    private ImageView mTool;

    public GroupList(Context context) {
        super(context);
        initializeLayout( context );
    }

    public GroupList(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeLayout( context );
    }

    public GroupList(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeLayout( context );
    }

    private void initializeLayout(Context context ){
        ctx = context;

        this.setOrientation( LinearLayout.HORIZONTAL );
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_group_list, this);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mImage = this.findViewById( R.id.group_list_image );
        mLabel = this.findViewById( R.id.group_list_text );
        mTool = this.findViewById( R.id.group_list_tool );
    }

    public void setGroup( Group g ){
        mGroup = g;

        mImage = this.findViewById( R.id.group_list_image );
        mLabel = this.findViewById( R.id.group_list_text );
        mTool = this.findViewById( R.id.group_list_tool );

        this.setContentDescription( Long.toString( g.getId() ) );

        mImage.setImageURI( g.getImageUri(ctx) );

        String label = g.getName() + " ( "+g.getNbItem()+" images )" ;
        mLabel.setText( label );
        mLabel.setTextSize(24);
        mLabel.setTextColor(Color.WHITE );

        int resId = R.drawable.ic_edit;
        mTool.setImageURI( ImageUtils.resImageUri(resId, ctx) );
        mTool.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, EditGroup.class );
                i.putExtra("groupId", Long.toString( mGroup.getId() ) );
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(i);
            }
        });
    }

    public TextView getText(){
        return this.findViewById( R.id.group_list_text );
    }

    public ImageView getImage(){
        return this.findViewById( R.id.group_list_image );
    }
}
