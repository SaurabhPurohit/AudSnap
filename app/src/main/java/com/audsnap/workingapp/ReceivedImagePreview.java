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
    private String[] imageUrl,audioUrl;
    private int j=0;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_image_preview);
        imageView=(ImageView) findViewById(R.id.recivedImagePreview);
        progressBar=(ProgressBar) findViewById(R.id.receivedImageProgress);

        Bundle bundle = getIntent().getExtras();
        String image = bundle.getString("image");
        String audio = bundle.getString("audio");

        if(image.contains(","))
        {
            imageUrl = image.split(",");
        }
        else {
            imageUrl=new String[1];
            imageUrl[0]=image;
        }
        if(audio.contains(","))
        {
            audioUrl=audio.split(",");
        }
        else {
            audioUrl=new String[1];
            audioUrl[0]=audio;
        }
        loadImage(0);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageUrl.length==j+1)
                {
                    finish();
                }
                else {
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                    j++;
                    loadImage(j);
                }
            }
        });
    }

    private void loadImage(final int index)
    {
        progressBar.setVisibility(View.VISIBLE);
        Picasso.with(this).load(imageUrl[index]).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(getBaseContext(), Uri.parse(audioUrl[index]));
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
