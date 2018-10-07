package org.eu.nveo.manonparle;

import android.content.Intent;
import android.media.*;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.*;
import org.eu.nveo.manonparle.Activity.BaseActivity;
import org.eu.nveo.manonparle.helper.AssetImporter;

import java.io.*;

public class NewItemShot extends BaseActivity {

    private String tag = "NewItemShot";

    private final int TAKE_A_SHOT = 1;
    private final int CROP_THE_SHOT = 2;
    private Uri photoUri;
    private File photoFile;
    private MediaRecorder itemRec;
    private TextView rec;
    private ImageView recBtn;
    private ImageView recPlay;
    private File output;
    private int maxDuration = 10; // in seconds

    private boolean isRecording = false;
    private boolean recorded = false;

    private Handler recordTimeout = new Handler();

    private Runnable timeoutAct = new Runnable() {
        @Override
        public void run() {
            stopRecording();
        }
    };

    private void startRecording(){
        recorded = false;
        itemRec = new MediaRecorder();
        itemRec.setAudioSource(MediaRecorder.AudioSource.MIC);
        itemRec.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        itemRec.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        itemRec.setOutputFile( output.getAbsolutePath() );
        try {
            itemRec.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isRecording = true;
        itemRec.start();
        recordTimeout.postDelayed( timeoutAct, 1000 * maxDuration );
        recPlay.setImageResource(R.drawable.ic_play);
        recPlay.setOnClickListener( null );
        recBtn.setImageResource( R.drawable.ic_recording );
    }
    private void stopRecording(){
        recordTimeout.removeCallbacks( timeoutAct );
        recorded = true;
        isRecording = false;
        itemRec.stop();
        itemRec.release();
        itemRec = null;
        recBtn.setImageResource( R.drawable.ic_record );
        recPlay.setImageResource(R.drawable.ic_play);
        recPlay.setOnClickListener( recPlayListener );
    }
    private View.OnClickListener recBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if( ! isRecording ) {
                startRecording();
            } else {
                stopRecording();
            }


        }
    };

    private View.OnClickListener recPlayListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if( output.exists() ){
                MediaPlayer mp = MediaPlayer.create( NewItemShot.this, Uri.fromFile( output ) );
                mp.start();
                recPlay.setImageResource( R.drawable.ic_playing );
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        recPlay.setImageResource( R.drawable.ic_play );
                    }
                });
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener synthChange = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            rec.setTextColor(getResources().getColor(R.color.colorDisabledText));
            recBtn.setImageResource( R.drawable.ic_record_disabled );
            recBtn.setOnClickListener( null );
            recPlay.setImageResource( R.drawable.ic_play_disabled );
            recPlay.setOnClickListener( null );

            if( ! isChecked ){
                rec.setTextColor(getResources().getColor(R.color.colorBaseText));
                recBtn.setImageResource( R.drawable.ic_record );
                recBtn.setOnClickListener( recBtnListener );
                if( recorded ) {
                    recPlay.setImageResource(R.drawable.ic_play);
                    recPlay.setOnClickListener( recPlayListener );
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item_shot);

        rec = findViewById(  R.id.edit_item_record );
        recBtn = findViewById( R.id.edit_item_rec_btn );
        recPlay = findViewById( R.id.edit_item_play_btn );

        Switch synth = findViewById(R.id.edit_item_synth);
        synth.setOnCheckedChangeListener( synthChange );
        synthChange.onCheckedChanged( synth, synth.isChecked() );

        output = new File( AssetImporter.getTmpFolder( this ), "item.mp4" );
        if( output.exists() ){
            output.delete();
        }

        ImageView valid = findViewById(R.id.valid_item);
        valid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast msg = Toast.makeText( NewItemShot.this, "Not yet implementer", Toast.LENGTH_SHORT );
                msg.show();
            }
        });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        photoFile = new File( getExternalFilesDir(Environment.DIRECTORY_PICTURES), "tmp.jpg");
        if( photoFile.exists() ){
            photoFile.delete();
        }
        photoUri = FileProvider.getUriForFile(this, "org.eu.nveo.manonparle", photoFile );
        Intent takeAShot = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if( takeAShot.resolveActivity( getPackageManager() ) != null ) {
            takeAShot.putExtra(MediaStore.EXTRA_OUTPUT, photoUri );
            startActivityForResult( takeAShot, TAKE_A_SHOT );
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( resultCode == RESULT_OK ) {
            if( requestCode == TAKE_A_SHOT ) {
                performCrop();
            } else if( requestCode == CROP_THE_SHOT ) {
                ImageView img = findViewById(R.id.imageView4);
                img.setImageURI(photoUri);
                //Bundle extras = data.getExtras();
                //Bitmap selectedBitmap = extras.getParcelable("data");
                //img.setImageBitmap( selectedBitmap );
            } else  {
                    ret();
            }
        } else {
            ret();
        }
    }

    private void performCrop() {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setData(photoUri);
        cropIntent.putExtra("crop", true);
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("outputX", 300);
        cropIntent.putExtra("outputY", 300);
        cropIntent.putExtra("return-data", true);
        cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(cropIntent, CROP_THE_SHOT);
    }

    private void ret(){
        Intent intent = new Intent( NewItemShot.this, MenuGroup.class );
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity( intent );
    }
}
