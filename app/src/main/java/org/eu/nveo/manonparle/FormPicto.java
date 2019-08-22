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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import org.eu.nveo.manonparle.adapter.GroupGridAdapter;
import org.eu.nveo.manonparle.db.Database;
import org.eu.nveo.manonparle.db.DatabaseException;
import org.eu.nveo.manonparle.db.ManonDatabase;
import org.eu.nveo.manonparle.helper.FileUtils;
import org.eu.nveo.manonparle.helper.Folders;
import org.eu.nveo.manonparle.model.Group;
import org.eu.nveo.manonparle.model.Picto;
import org.eu.nveo.manonparle.model.RPictoGroup;

import java.io.*;
import java.util.Locale;
import java.util.UUID;

public class FormPicto extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private String tag = "FormPicto";

    public final int PICTO_COMPLETE = 0;
    public final int PICTO_NOT_NAMED = 1;
    public final int PICTO_NO_GROUP = 2;
    public final int PICTO_NO_SOUND = 3;
    public final int PICTO_NO_IMAGE = 4;


    private final int TAKE_A_SHOT = 1;
    private final int CROP_THE_SHOT = 2;

    private long pictoId = -666;

    private Uri photoUri;
    private File photoFile;
    private MediaRecorder recorder;
    private ImageView recBtn;
    private ImageView recPlay;
    private File output;
    private Switch synth;
    private TextView synthText;
    private EditText label;
    private GridView grid;
    private GroupGridAdapter adapter;
    private TextToSpeech tts;
    private ImageView pictoImage;

    private int maxDuration = 10; // in seconds

    private boolean isRecording = false;
    private boolean recorded = false;
    private boolean photoTaken = false;

    private String imageExt = "jpg";
    private String audioExt = "mp4";


    private Handler timeoutHandler = new Handler();

    private Runnable timeoutRunnable = new Runnable() {
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
                MediaPlayer mp = MediaPlayer.create( FormPicto.this, Uri.fromFile( output ) );
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
                synthText.setText( local.getTextOn() );
            } else {
                synthText.setText( local.getTextOff() );
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
        setContentView(R.layout.activity_form_picto);

        label = findViewById( R.id.picto_form_label_input);

        recBtn = findViewById( R.id.picto_form_rec_btn);
        recPlay = findViewById( R.id.picto_form_play_btn);

        synthText = findViewById(R.id.picto_form_synth_text);
        synth = findViewById(R.id.picto_form_synth);
        synth.setOnCheckedChangeListener( synthChange );
        synthChange.onCheckedChanged( synth, synth.isChecked() );

        output = new File( Folders.getTmpFolder(), "picto.mp4" );
        if( output.exists() ){
            output.delete();
        }

        photoFile = new File( getExternalFilesDir(Environment.DIRECTORY_PICTURES), "tmp.jpg");
        if( photoFile.exists() ){
            photoFile.delete();
        }
        photoUri = FileProvider.getUriForFile(this, "org.eu.nveo.manonparle", photoFile );

        ImageView valid = findViewById(R.id.picto_form_valid);
        valid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( checkForm() != PICTO_COMPLETE ) {
                    Toast msg = Toast.makeText( FormPicto.this, getResources().getText( R.string.form_picto_not_complete), Toast.LENGTH_SHORT );
                    msg.show();
                } else {
                    if( synth.isChecked() ) {
                        tts = new TextToSpeech( FormPicto.this, FormPicto.this );
                        // next of the flow in onInit
                    } else {
                        saveForm();
                    }
                }
            }
        });

        ImageView cancel = findViewById( R.id.picto_form_cancel );
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        adapter =  new GroupGridAdapter();
        grid = findViewById( R.id.picto_form_group_select);
        grid.setNumColumns( 2 );
        grid.setAdapter( adapter );

        pictoImage = findViewById(R.id.picto_form_image );
        pictoImage.setOnClickListener(new View.OnClickListener() {
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
    protected void onResume() {
        super.onResume();

        Intent args = getIntent();
        if( pictoId == -666 ) {
            pictoId = args.getLongExtra("pictoId", -1);
            if (pictoId >= 0) {
                initForm(pictoId);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( resultCode == RESULT_OK ) {
            if( requestCode == TAKE_A_SHOT ) {
                performCrop();
            } else if( requestCode == CROP_THE_SHOT ) {
                photoTaken = true;
                pictoImage = findViewById(R.id.picto_form_image);
                pictoImage.setImageURI(null);
                pictoImage.setImageURI(photoUri);
            }
        }
    }

    private void initForm( long id ){
        // Picto
        Picto picto = null;
        try {
            picto =  Database.getConnection().picto().byId( id );
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        Uri pictoUri = picto.getImageUri();
        File pictoFile = new File( pictoUri.getPath() );
        try {
            FileUtils.copyFile( new FileInputStream( pictoFile ) , new FileOutputStream( photoFile ) );
        } catch (IOException e) {
            e.printStackTrace();
        }

        photoTaken = true;
        pictoImage.setImageURI( photoUri );
        imageExt = picto.getImageExt();

        label.setText( picto.getName() );

        synth.setChecked( picto.getSoundSynth() );
        if( ! picto.getSoundSynth() ) {
            Uri pictoSoundUri = picto.getSoundUri();
            File pictoSoundFile = new File( pictoSoundUri.getPath() );
            try {
                FileUtils.copyFile( new FileInputStream( pictoSoundFile ), new FileOutputStream( output ) );
            } catch (IOException e) {
                e.printStackTrace();
            }
            audioExt = picto.getAudioExt();
            recorded = true;

        }
        synthChange.onCheckedChanged( synth, picto.getSoundSynth()  );

        int count = adapter.getCount();
        for( int i = 0; i < count; i++ ) {
            Group g = (Group) adapter.getItem( i );
            if( picto.isLinkedTo( g ) ) {
                    adapter.setChecked( i , true);
            }
        }
    }

    private int checkForm(){
        Log.v(tag, "Nom de l'picto: '"+label.getText().toString()+"'" );
        if( label.getText().toString().isEmpty()  ) {
            return PICTO_NOT_NAMED;
        } else if( ! synth.isChecked() && ! recorded ) {
            return PICTO_NO_SOUND;
        } else if( adapter.countChecked() == 0  ) {
            return PICTO_NO_GROUP;
        } else if( ! photoTaken ) {
            return PICTO_NO_IMAGE;
        } else {
            return PICTO_COMPLETE;
        }

    }

    private void saveForm(){

        boolean bSynth = synth.isChecked();
        ManonDatabase conn = null;
        try {
            conn = Database.getConnection();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        if( bSynth ) {
            audioExt = "wav";
        }

        // Create Picto
        Picto el = null;
        if( pictoId >= 0 ){
            el = conn.picto().byId( pictoId );
        }else {
            el = new Picto();
        }
        el.setName( label.getText().toString() );
        el.setHasSound( true );
        el.setSoundSynth( bSynth );
        el.setImageExt( imageExt );
        el.setAudioExt( audioExt );
        if( pictoId >= 0 ) {
            conn.picto().update( el );
        } else {
            pictoId = conn.picto().insert(el);
        }

        // Create links with groups
        int count = adapter.getCount();
        for( int i = 0; i < count; i++ ){
            Group g = (Group) adapter.getItem(i);
            if( adapter.isChecked( i ) ){
                if( ! el.isLinkedTo( g ) ) {
                    long groupId = g.getId();
                    RPictoGroup link = new RPictoGroup();
                    link.setPictoId(pictoId);
                    link.setGroupId(groupId);
                    conn.rpictogroup().insert(link);
                }
            } else {
                if( el.isLinkedTo( g )  ) {
                    RPictoGroup link = conn.rpictogroup().byGroupIdPictoId( g.getId(), el.getId() );
                    conn.rpictogroup().delete( link );
                }
            }
        }

        File dataFolder = Folders.getDataFolder();
        // Save image
        try {
            FileInputStream fis = new FileInputStream( photoFile );
            FileOutputStream fos = new FileOutputStream( new File( dataFolder , Long.toString(pictoId)+"."+imageExt ) );
            FileUtils.copyFile( fis, fos );
            fis.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        photoFile.delete();

        try {
            FileInputStream fis = new FileInputStream( output );
            FileOutputStream fos = new FileOutputStream( new File( dataFolder , Long.toString(pictoId)+"."+audioExt ) );
            FileUtils.copyFile( fis, fos );
            fis.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        finish();

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

    private void startRecording(){
        recorded = false;
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER );
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile( output.getAbsolutePath() );
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isRecording = true;
        recorder.start();
        timeoutHandler.postDelayed(timeoutRunnable, 1000 * maxDuration );
        recPlay.setImageResource(R.drawable.ic_play);
        recPlay.setOnClickListener( null );
        recBtn.setImageResource( R.drawable.ic_recording );
    }

    private void stopRecording(){
        timeoutHandler.removeCallbacks(timeoutRunnable);
        recorded = true;
        isRecording = false;
        recorder.stop();
        recorder.release();
        recorder = null;
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
                    Toast msg = Toast.makeText( FormPicto.this, "Failed to synthesized audio", Toast.LENGTH_SHORT);
                    msg.show();
                }
            });
            String uuid = UUID.randomUUID().toString();
            tts.synthesizeToFile( label.getText().subSequence(0, label.length() ), null, output,  uuid );
        } else {
            Toast msg = Toast.makeText( FormPicto.this, "Failed to init TTS module", Toast.LENGTH_SHORT);
            msg.show();
        }
    }
}
