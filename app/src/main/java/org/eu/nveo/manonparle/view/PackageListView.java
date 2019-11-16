package org.eu.nveo.manonparle.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.eu.nveo.manonparle.R;
import org.eu.nveo.manonparle.helper.MetaPackage;

public class PackageListView extends LinearLayout {
    private String tag = "PackageListView";
    private ImageView img;
    private TextView name;
    private TextView nb_pictos;
    private TextView nb_groups;
    private TextView version;
    private TextView size;
    private TextView repo;

    public PackageListView(Context context) {
        super(context);
        initializeView( context );
    }

    public PackageListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeView( context );
    }

    public PackageListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeView( context );
    }

    public PackageListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initializeView( context );
    }

    private void initializeView(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_package_list, this);
        initializeObjects();
    }

    private void initializeObjects(){
        if( img == null ) {
            img = this.findViewById(R.id.package_image);
        }
        if( name == null ) {
            name = this.findViewById(R.id.name);
        }
        if( nb_pictos == null ) {
            nb_pictos = this.findViewById(R.id.nb_pictos);
        }
        if( nb_groups == null ) {
            nb_groups = this.findViewById(R.id.nb_groups);
        }
        if( version == null ) {
            version = this.findViewById(R.id.version);
        }
        if( size == null ) {
            size = this.findViewById(R.id.size);
        }
        if( repo == null ) {
            repo = this.findViewById(R.id.repo);
        }
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        //initializeObjects();
    }

    public void setPackage(MetaPackage pack){
        //initializeObjects();
        name.setText( pack.getName() );
        String nb_pictos_template = getResources().getString(R.string.manage_packages_nb_pictos);
        nb_pictos.setText( String.format( nb_pictos_template, pack.getNbPictos()) );
        String nb_groups_template = getResources().getString(R.string.manage_packages_nb_pictos);
        nb_groups.setText( String.format( nb_groups_template, pack.getNbGroup()) );
        String version_template = getResources().getString(R.string.manage_packages_version);
        version.setText( String.format( version_template, pack.getVersion() ) );
        String repo_template = getResources().getString(R.string.manage_packages_repo);
        repo.setText( String.format( repo_template, pack.getRepo().getName()));
        String size_template = getResources().getString(R.string.manage_packages_size);
        size.setText( String.format( size_template, pack.getHumanReadableSizeString()) );
        // TODO
        // Missing a way to get the image of the pack
    }
}
