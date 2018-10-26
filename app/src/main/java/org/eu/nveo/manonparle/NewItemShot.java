package org.eu.nveo.manonparle;

import android.content.Intent;
import android.media.*;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import org.eu.nveo.manonparle.Activity.BaseActivity;
import org.eu.nveo.manonparle.adapter.GroupGridAdapter;
import org.eu.nveo.manonparle.db.Database;
import org.eu.nveo.manonparle.db.DatabaseException;
import org.eu.nveo.manonparle.db.ItemDatabase;
import org.eu.nveo.manonparle.helper.AssetImporter;
import org.eu.nveo.manonparle.helper.FileUtils;
import org.eu.nveo.manonparle.model.Group;
import org.eu.nveo.manonparle.model.Item;
import org.eu.nveo.manonparle.model.RItemGroup;

import java.io.*;
import java.util.Locale;
import java.util.UUID;

public class NewItemShot extends BaseActivity implements TextToSpeech.OnInitListener {

    private String tag = "NewItemShot";

    public final int ITEM_COMPLETE = 0;
    public final int ITEM_NOT_NAMED = 1;
    public final int ITEM_NO_GROUP = 2;
    public final int ITEM_NO_SOUND = 3;
    public final int ITEM_NO_IMAGE = 4;


    private final int TAKE_A_SHOT = 1;
    private final int CROP_THE_SHOT = 2;

    private Uri photoUri;
    private File photoFile;
    private MediaRecorder itemRec;
    private ImageView recBtn;
    private ImageView recPlay;
    private File output;
    private Switch synth;
    private EditText label;
    private GridView grid;
    private GroupGridAdapter adapter;
    private TextToSpeech tts;

    private int maxDuration = 10; // in seconds

    private boolean isRecording = false;
    private boolean recorded = false;
    private boolean photoTaken = false;


    private Handler recordTimeout = new Handler();

    private Runnable timeoutAct = new Runnable() {
        @Override
        public void run() {
            stopRecording();
        }
    };

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

            Switch local = (Switch) buttonView;

            if( isChecked ) {
                buttonView.setText( local.getTextOn() );
            } else {
                buttonView.setText( local.getTextOff() );
            }

            recBtn.setImageResource( R.drawable.ic_record_disabled );
            recBtn.setOnClickListener( null );
            recPlay.setImageResource( R.drawable.ic_play_disabled );
            recPlay.setOnClickListener( null );

            if( ! isChecked ){
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

        label = findViewById( R.id.edit_item_label_input );
        label.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if( ! hasFocus ){
                    ensureFullscreen();
                }
            }
        });

        label.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if( actionId == EditorInfo.IME_ACTION_DONE ){
                    ensureFullscreen();
                    return false;
                }
                return true;
            }
        });

        recBtn = findViewById( R.id.edit_item_rec_btn );
        recPlay = findViewById( R.id.edit_item_play_btn );

        synth = findViewById(R.id.edit_item_synth);
        synth.setOnCheckedChangeListener( synthChange );
        synthChange.onCheckedChanged( synth, synth.isChecked() );

        output = new File( AssetImporter.getTmpFolder( this ), "item.mp4" );
        if( output.exists() ){
            output.delete();
        }

        photoFile = new File( getExternalFilesDir(Environment.DIRECTORY_PICTURES), "tmp.jpg");
        if( photoFile.exists() ){
            photoFile.delete();
        }
        photoUri = FileProvider.getUriForFile(this, "org.eu.nveo.manonparle", photoFile );

        ImageView valid = findViewById(R.id.valid_item);
        valid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( checkForm() != ITEM_COMPLETE ) {
                    Toast msg = Toast.makeText( NewItemShot.this, getResources().getText( R.string.edit_item_not_complete ), Toast.LENGTH_SHORT );
                    msg.show();
                } else {
                    if( synth.isChecked() ) {
                        tts = new TextToSpeech( NewItemShot.this, NewItemShot.this );
                        // next of the flow in onInit
                    } else {
                        saveForm();
                    }
                }
            }
        });

        adapter =  new GroupGridAdapter( this );
        grid = findViewById( R.id.edit_item_group_select );
        grid.setNumColumns( 2 );
        grid.setAdapter( adapter );

        ImageView img = findViewById(R.id.imageView4);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takeAShot = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if( takeAShot.resolveActivity( getPackageManager() ) != null ) {
                    takeAShot.putExtra(MediaStore.EXTRA_OUTPUT, photoUri );
                    startActivityForResult( takeAShot, TAKE_A_SHOT );
                }

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( resultCode == RESULT_OK ) {
            if( requestCode == TAKE_A_SHOT ) {
                performCrop();
            } else if( requestCode == CROP_THE_SHOT ) {
                photoTaken = true;
                ImageView img = findViewById(R.id.imageView4);
                img.setImageURI(photoUri);
            }
        }
    }

    private int checkForm(){
        Log.v(tag, "Nom de l'item: '"+label.getText().toString()+"'" );
        if( label.getText().toString().isEmpty()  ) {
            return ITEM_NOT_NAMED;
        } else if( ! synth.isChecked() && ! recorded ) {
            return ITEM_NO_SOUND;
        } else if( adapter.countChecked() == 0  ) {
            return ITEM_NO_GROUP;
        } else if( ! photoTaken ) {
            return ITEM_NO_IMAGE;
        } else {
            return ITEM_COMPLETE;
        }

    }

    private void saveForm(){

        boolean bSynth = synth.isChecked();
        ItemDatabase conn = null;
        try {
            conn = Database.getConnection();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        String imageExt = "jpg";
        String audioExt = "mp4";
        if( bSynth ) {
            audioExt = "wav";
        }

        // Create Item
        Item el = new Item();
        el.setName( label.getText().toString() );
        el.setHasSound( true );
        el.setSoundSynth( bSynth );
        el.setImageExt( imageExt );
        el.setAudioExt( audioExt );
        long itemId = conn.item().insert( el );

        // Create links with groups
        int count = adapter.getCount();
        for( int i = 0; i < count; i++ ){
            Group g = (Group) adapter.getItem(i);
            if( adapter.isChecked( i ) ){
                long groupId = g.getId();
                RItemGroup link = new RItemGroup();
                link.setItemId( itemId );
                link.setGroupId( groupId );
                conn.ritemgroup().insert( link );
            }
        }

        File dataFolder = AssetImporter.getDataFolder( NewItemShot.this );
        // Save image
        try {
            FileInputStream fis = new FileInputStream( photoFile );
            FileOutputStream fos = new FileOutputStream( new File( dataFolder , Long.toString(itemId)+"."+imageExt ) );
            FileUtils.copyFile( fis, fos );
            fis.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        photoFile.delete();

        try {
            FileInputStream fis = new FileInputStream( output );
            FileOutputStream fos = new FileOutputStream( new File( dataFolder , Long.toString(itemId)+"."+audioExt ) );
            FileUtils.copyFile( fis, fos );
            fis.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ret();

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

    private void startRecording(){
        recorded = false;
        itemRec = new MediaRecorder();
        itemRec.setAudioSource(MediaRecorder.AudioSource.CAMCORDER );
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
        recPlay.setImageResource( R.drawable.ic_play );
        recPlay.setOnClickListener( recPlayListener );
    }

    @Override
    public void onInit(int status) {
        if( status == TextToSpeech.SUCCESS ) {
            tts.setLanguage(new Locale("fr"));
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {

                }

                @Override
                public void onDone(String utteranceId) {
                    tts.shutdown();
                    saveForm();
                }

                @Override
                public void onError(String utteranceId) {
                    tts.shutdown();
                    Toast msg = Toast.makeText( NewItemShot.this, "Failed to synthesized audio", Toast.LENGTH_SHORT);
                    msg.show();
                }
            });
            String uuid = UUID.randomUUID().toString();
            tts.synthesizeToFile( label.getText().subSequence(0, label.length() ), null, output,  uuid );
        } else {
            Toast msg = Toast.makeText( NewItemShot.this, "Failed to init TTS module", Toast.LENGTH_SHORT);
            msg.show();
        }
    }
}
