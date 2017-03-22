package com.audsnap.workingapp;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ExpandedMenuView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.audsnap.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ReceivedImagePreview extends AppCompatActivity {

    private ImageView imageView;
    private ProgressBar progressBar;
    private String imageUrl,audioUrl;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_image_preview);
        imageView=(ImageView) findViewById(R.id.recivedImagePreview);
        progressBar=(ProgressBar) findViewById(R.id.receivedImageProgress);

        Bundle bundle = getIntent().getExtras();
        imageUrl = bundle.getString("image");
        audioUrl = bundle.getString("audio");

        Picasso.with(this).load(imageUrl).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(getBaseContext(), Uri.parse(audioUrl));
                    mediaPlayer.prepare();
                    mediaPlayer.setLooping(true);
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            progressBar.setVisibility(View.GONE);
                            mediaPlayer.start();
                        }
                    });
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }
}
