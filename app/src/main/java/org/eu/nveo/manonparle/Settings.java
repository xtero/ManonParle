package org.eu.nveo.manonparle;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import org.eu.nveo.manonparle.Activity.BaseActivity;
import top.defaults.colorpicker.ColorPickerPopup;

import static org.eu.nveo.manonparle.helper.ImageUtils.*;
import static org.eu.nveo.manonparle.helper.Preferences.*;

public class Settings extends BaseActivity {
    private String tag = "Settings";

    private TextView skew_left;
    private TextView skew_neutral;
    private TextView skew_right;
    private EditText skew;
    private LinearLayout confirmColor;
    private TextView confirmInvite;
    private SeekBar confirmSpeed;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    /*
    private void resetGlowEffect( View v ){
        TextView text = (TextView) v;
        text.setShadowLayer( 0, 0,0, getResources().getColor(R.color.baseOverlay) );
        text.setTextColor( getResources().getColor(R.color.colorBaseText) );
    }

    private void setGlowEffect( View v ){
        TextView text = (TextView) v;
        int color = getResources().getColor(R.color.glowSetting) ;
        text.setShadowLayer( 10, 0,0, color);
        text.setTextColor( color );

    }
    */

    private void setSkewEdit( boolean enabled ){
        skew.setEnabled( enabled );
        if( enabled ){
            skew.setBackgroundColor( getResources().getColor(R.color.inputFieldBackground) );
        } else {
            skew.setBackgroundColor(Color.GRAY);
        }
    }

    private View.OnClickListener skew_side_select = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            resetGlowEffect( skew_left, getResources().getColor(R.color.colorBaseText));
            resetGlowEffect( skew_neutral, getResources().getColor(R.color.colorBaseText) );
            resetGlowEffect( skew_right, getResources().getColor(R.color.colorBaseText) );
            setGlowEffect( (TextView) v, getResources().getColor(R.color.glowSetting));
            int skew_side = Integer.parseInt( v.getContentDescription().toString() );
            editor.putInt( "skew_side", skew_side );
            editor.commit();

            // If neutral, no need to enable skew
            setSkewEdit( skew_side != 0 );
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        prefs = getApplicationContext().getSharedPreferences( GLOBAL_PREFS, MODE_PRIVATE);
        editor = prefs.edit();
        skew = findViewById( R.id.skew_factor );

        int skew_side = prefs.getInt("skew_side", DEFAULT_SKEW_SIDE );
        skew_left = findViewById( R.id.skew_left );
        skew_left.setOnClickListener(skew_side_select);
        skew_neutral = findViewById( R.id.skew_neutral );
        skew_neutral.setOnClickListener(skew_side_select);
        skew_right = findViewById( R.id.skew_right );
        skew_right.setOnClickListener(skew_side_select);

        switch ( skew_side ){
            case -1:
                setGlowEffect( skew_left, getResources().getColor(R.color.glowSetting) );
                setSkewEdit( true );
                break;
            case 0:
                setGlowEffect( skew_neutral, getResources().getColor(R.color.glowSetting) );
                setSkewEdit( false );
                break;
            case 1:
                setGlowEffect( skew_right, getResources().getColor(R.color.glowSetting) );
                setSkewEdit( true );
                break;
        }

        skew.setText( Integer.toString( prefs.getInt("skew_factor", DEFAULT_SKEW_FACTOR ) ) );

        skew.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if( s.length() == 0 ){
                    return;
                }
                int skew_factor = Integer.parseInt( s.toString() );
                editor.putInt( "skew_factor", skew_factor );
                editor.commit();
            }
        });
        skew.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if( ! hasFocus ){
                    ensureFullscreen();
                }
            }
        });

        final int color = prefs.getInt("confirm_color", CONFIRM_COLOR );
        confirmInvite = findViewById(R.id.confirm_color_invite);
        confirmInvite.setTextColor( optimalFontColor( color ) );
        confirmColor = findViewById( R.id.confirm_color );
        confirmColor.setBackgroundColor( color );
        confirmColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerPopup.Builder( Settings.this )
                        .initialColor( prefs.getInt("confirm_color", CONFIRM_COLOR ) )
                        .enableAlpha(true)
                        .okTitle((String) getResources().getText(R.string.select))
                        .cancelTitle((String) getResources().getText(R.string.cancel))
                        .showIndicator(true)
                        .showValue(true)
                        .build()
                        .show(v, new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void onColorPicked(int i) {
                                editor.putInt("confirm_color", i);
                                editor.commit();
                                confirmColor.setBackgroundColor( i );
                                confirmInvite.setTextColor( optimalFontColor( i ) );

                            }

                            @Override
                            public void onColor(int i, boolean b) {

                            }
                        });
            }
        });

        int speed = prefs.getInt("confirm_speed", CONFIRM_SPEED );
        confirmSpeed = findViewById( R.id.confirm_speed_value );
        confirmSpeed.setProgress( speed );
        confirmSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int speed = seekBar.getProgress();
                editor.putInt( "confirm_speed", speed );
                editor.commit();
            }
        });
    }

}
