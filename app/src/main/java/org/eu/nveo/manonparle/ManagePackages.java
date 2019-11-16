package org.eu.nveo.manonparle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import org.eu.nveo.manonparle.adapter.PackageListAdapter;
import org.eu.nveo.manonparle.db.Database;
import org.eu.nveo.manonparle.db.DatabaseException;
import org.eu.nveo.manonparle.model.Repo;

public class ManagePackages extends AppCompatActivity {

    private final String tag = "ManagePackages";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_packages);
        try {
            Repo[] repos = Database.getConnection().repo().all();
            for ( Repo repo : repos ) {
                Log.d(tag, "Updating repo "+ repo.getName() );
                repo.update();
                Log.d(tag, "Repo "+ repo.getName() + " updated");
            }
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();

        ListView list = findViewById(R.id.packages);
        list.setAdapter( new PackageListAdapter() );
    }
}
