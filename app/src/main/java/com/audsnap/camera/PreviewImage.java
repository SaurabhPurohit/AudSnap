package com.audsnap.camera;

import android.app.ProgressDialog;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.audsnap.R;
import com.audsnap.adapter.SearchFriendAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class PreviewImage extends AppCompatActivity implements View.OnClickListener {

    private MediaRecorder myAudioRecorder;
    private ImageButton startRecord,stopRecord,viewImage;
    private String time;
    private String path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/AudSnap/";
    private MediaPlayer mediaPlayer,videoMediaPlayer;
    private boolean isAudioRecording,isSoundPlaying;
    private int height,width,index;
    private Uri videoUri;
    private ImageView imageView;
    private TextureView videoView;
    private StorageReference mStorageRef;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mSenderDb = firebaseDatabase.getReference("/AudSnap/chat/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/");
    private DatabaseReference mReceiverDb = firebaseDatabase.getReference("/AudSnap/chat/"+ SearchFriendAdapter.receiverId+"/");
    Uri uri;
    File audioFile;

    private Surface s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_image);

        mStorageRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl("gs://project-527458073545831192.appspot.com");

       uri = getIntent().getParcelableExtra("URIOFIMAGE");
        Log.d("IMAGEURI",uri.toString());
       // videoUri = getIntent().getParcelableExtra("URIOFVIDEO");
        time = getIntent().getStringExtra("TIMEFORMAT");
        height=getIntent().getIntExtra("height",0);
        width=getIntent().getIntExtra("width",0);
        index=getIntent().getIntExtra("index",0);

        if(uri!=null)
        {
            imageView = (ImageView) findViewById(R.id.imageView);
            videoView = (TextureView) findViewById(R.id.videoView);
            videoView.setSurfaceTextureListener(surfaceTextureListener);
            //videoView.setSurfaceTextureListener(this);
            imageView.setImageURI(uri);
            //imageView.setOnTouchListener(new ZoomInZoomOut());
            startRecord = (ImageButton) findViewById(R.id.button_record_audio);
            startRecord.setOnClickListener(this);
            stopRecord = (ImageButton) findViewById(R.id.button_stop_audio);
            stopRecord.setOnClickListener(this);
            myAudioRecorder = new MediaRecorder();
            viewImage = (ImageButton) findViewById(R.id.button_view_image);
            viewImage.setOnClickListener(this);
        }

    }

    private void setUpMediaPlayer(Surface surface) {

        videoMediaPlayer=new MediaPlayer();
        try {
            videoMediaPlayer.setDataSource(PreviewImage.this,videoUri);
            videoMediaPlayer.setSurface(surface);
            videoMediaPlayer.prepare();
            videoMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        videoMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                videoMediaPlayer.release();
                videoMediaPlayer=null;
                videoView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.button_record_audio:
                try {
                    startRecord.setVisibility(View.GONE);
                    stopRecord.setVisibility(View.VISIBLE);
                    myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                    myAudioRecorder.setOutputFile(path+"/"+time+".3gp");
                    myAudioRecorder.prepare();
                    isAudioRecording=true;
                    myAudioRecorder.start();
                    audioFile = new File(path+"/"+time+".3gp");
                }catch (Exception e)
                {
                    Log.e("PreviewImage",e.getLocalizedMessage(),e);
                }
                break;
            case R.id.button_stop_audio:

                if(myAudioRecorder!=null)
                {
                    myAudioRecorder.stop();
                    isAudioRecording=false;
                    myAudioRecorder.release();
                    myAudioRecorder=null;
                    stopRecord.setVisibility(View.GONE);
                    startRecord.setVisibility(View.GONE);
                    viewImage.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.button_view_image:
                stopRecord.setVisibility(View.GONE);
                startRecord.setVisibility(View.GONE);
                viewImage.setVisibility(View.GONE);
                mediaPlayer=new MediaPlayer();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        viewImage.setVisibility(View.VISIBLE);
                        mediaPlayer.stop();
                        isSoundPlaying=false;
                        mediaPlayer.release();
                        mediaPlayer=null;
                    }
                });
                playSound();

                break;
        }
    }

    private void uploadFile(){
        if(uri!=null){
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            StorageReference ImageRef = mStorageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"/images")
                    .child(uri.getLastPathSegment());

            Uri audioUri = Uri.fromFile(audioFile);

            StorageReference audioRef = mStorageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"audio")
                    .child(uri.getLastPathSegment());

            Log.d("AUDIO",audioUri.toString());

//            Log.d("uploadURI",filePath.toString());
            ImageRef.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();
                            @SuppressWarnings("VisibleForTests")
                            Uri uri = taskSnapshot.getDownloadUrl();
                            mSenderDb.child(time+"/image/").setValue(uri);
                            mReceiverDb.child(time+"/image/").setValue(uri);
//                            imageView.setVisibility(View.INVISIBLE);
                            //and displaying a success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();
                            Log.e("onFailure()",exception.toString());
                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            @SuppressWarnings("VisibleForTests")
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });

            audioRef.putFile(audioUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            //progressDialog.dismiss();
//                            imageView.setVisibility(View.INVISIBLE);
                            @SuppressWarnings("VisibleForTests")
                            Uri uri = taskSnapshot.getDownloadUrl();
                            mSenderDb.child(time+"/audio/").setValue(uri).addOnFailureListener(PreviewImage.this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                }
                            });
                            mSenderDb.child(time+"/receiver/").setValue(SearchFriendAdapter.receiverId);
                            mSenderDb.child(time+"/sent/").setValue(true);

                            mReceiverDb.child(time+"/audio/").setValue(uri);
                            mReceiverDb.child(time+"/receiver/").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            mReceiverDb.child(time+"/sent/").setValue(false);

                            //and displaying a success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            //progressDialog.dismiss();
                            Log.e("onFailure()",exception.toString());
                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            @SuppressWarnings("VisibleForTests")
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            //progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });


        }
    }

    private void playSound()
    {
        try {
            Log.d("playSound", String.valueOf(isSoundPlaying));
            isSoundPlaying=true;
            Log.d("playSound", String.valueOf(isSoundPlaying));
            mediaPlayer.setDataSource(path+"/"+time+".3gp");
            mediaPlayer.prepare();
            mediaPlayer.start();
            uploadFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(myAudioRecorder!=null && isAudioRecording)
        {
            myAudioRecorder.stop();
            myAudioRecorder.release();
            myAudioRecorder=null;
        }
        if(mediaPlayer!=null && isSoundPlaying)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(myAudioRecorder!=null && isAudioRecording)
        {
            myAudioRecorder.stop();
            myAudioRecorder.release();
            myAudioRecorder=null;
        }
        Log.d("playSound", String.valueOf(isSoundPlaying));
        if(mediaPlayer!=null && isSoundPlaying)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(myAudioRecorder!=null && isAudioRecording)
        {
            myAudioRecorder.stop();
            myAudioRecorder.release();
            myAudioRecorder=null;
        }
        Log.d("playSound", String.valueOf(isSoundPlaying));
        if(mediaPlayer!=null && isSoundPlaying)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }

    TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

            s=new Surface(surface);

            int rotation = getWindowManager().getDefaultDisplay()
                    .getRotation();
            Matrix matrix = new Matrix();

            switch (rotation){

                case Surface.ROTATION_0:
                   // mCamera.setDisplayOrientation(90);
                    videoView.setLayoutParams(new FrameLayout.LayoutParams(
                            height, width, Gravity.CENTER));
                    if(index==0)
                    {
                        matrix.setScale(-1,1,height/2,0);
                        videoView.setScaleY(-1);
                        videoView.setTransform(matrix);
                    }
                    else {
                        matrix.setScale(-1,1,height/2,0);
                        videoView.setTransform(matrix);
                    }
                    break;

                case Surface.ROTATION_90:
                    videoView.setLayoutParams(new FrameLayout.LayoutParams(
                            height, width, Gravity.CENTER));
                    matrix.setScale(-1,1,width/2,0);
                    videoView.setTransform(matrix);

                    break;

                case Surface.ROTATION_180:
                    videoView.setLayoutParams(new FrameLayout.LayoutParams(
                            height, width, Gravity.CENTER));
                    matrix.setScale(-1,1,height/2,0);
                    videoView.setTransform(matrix);
                    break;

                case Surface.ROTATION_270:
                    videoView.setLayoutParams(new FrameLayout.LayoutParams(
                            height, width, Gravity.CENTER));
                    matrix.setScale(-1,1,width/2,0);
                    videoView.setTransform(matrix);
                    break;


            }
            setUpMediaPlayer(s);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
