package com.example.android.musicplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    MediaPlayer md;
    AudioManager am;
    Button play, pause, stop;

    AudioManager.OnAudioFocusChangeListener aflist = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {

                case AudioManager.AUDIOFOCUS_LOSS:
                    md.stop();
                    releaseMediaPlayer();
                    Toast.makeText(getApplicationContext(), "PERM_LOSS", Toast.LENGTH_SHORT).show();
                    break;

                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    // Temporary loss of audio focus - expect to get it back - you can keep your resources around
                    Toast.makeText(getApplicationContext(), "TEMP_LOSS", Toast.LENGTH_SHORT).show();
                    if (md.isPlaying()) {
                        md.pause();
                    }
                    break;

                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    //Reduce the volume for few seconds
                    Toast.makeText(getApplicationContext(), "DUCK", Toast.LENGTH_SHORT).show();
                    md.setVolume(0.5f, 0.5f);
                    break;

                    default:
                        Toast.makeText(getApplicationContext(), "unknown focus!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        play = findViewById(R.id.playsong);
        pause = findViewById(R.id.pausesong);
        stop = findViewById(R.id.stop);

        pause.setVisibility(View.INVISIBLE);


        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        int result = am.requestAudioFocus(aflist, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            md = MediaPlayer.create(this, R.raw.skrillex);
            Toast.makeText(this, "Focus Gained and object created", Toast.LENGTH_SHORT).show();
        }


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                md.start();
                pause.setVisibility(View.VISIBLE);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                md.pause();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (md != null) {
                    md.stop();
                }
                releaseMediaPlayer();
                Toast.makeText(getBaseContext(), "NULL", Toast.LENGTH_SHORT).show();
            }
        });

        md.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(getBaseContext(), "I'm done!", Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (md != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            md.release();

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            md = null;
            am.abandonAudioFocus(aflist);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }
}
