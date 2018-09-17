package org.eu.nveo.manonparle.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import org.eu.nveo.manonparle.helper.Fullscreen;

public class BaseActivity extends AppCompatActivity {
    protected Fullscreen fs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fs = new Fullscreen(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fs.ensureFullscreen();
    }
}
