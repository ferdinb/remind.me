package com.remind.me.fninaber.util;

import java.io.File;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.remind.me.fninaber.R;

public class AudioUtil {
    private static AudioUtil instance;
    private MediaPlayer mediaPlayer;
    private Context context;
    private TextView time;
    private ImageView button;
    private SeekBar seekBar;
    private File file;

    public static AudioUtil getInstance() {
        if (null == instance) {
            instance = new AudioUtil();
        }
        return instance;
    }

    public void setInstanceNull() {
        instance = null;
    }

    public void setDefaultView(Context context, File file, ImageView button, SeekBar seekBar, TextView time) {
        this.context = context;
        this.file = file;
        this.button = button;
        this.seekBar = seekBar;
        this.time = time;
    }

    public void playAudio() {
        if (context != null && file != null && button != null && seekBar != null && time != null) {
            Uri alert = file != null ? Uri.fromFile(file) : null;
            final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0 && null != alert) {
                try {
                    if (null == mediaPlayer) {
                        mediaPlayer = new MediaPlayer();
                    }
                    mediaPlayer.setDataSource(context, alert);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setLooping(false);
                    seekBar.setProgress(0);

                    final Runnable timeAndSeekRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (mediaPlayer != null) {
                                long duration = mediaPlayer.getDuration();
                                long playing = mediaPlayer.getCurrentPosition();

                                int d = (int) (duration / 1000);
                                int minutes = d / 60;
                                int seconds = d - (minutes * 60);

                                int p = (int) (playing / 1000);
                                int minutesPlaying = p / 60;
                                int secondsPlaying = p - (minutesPlaying * 60);
                                String playingTime = String.format("%02d", minutesPlaying) + ":" + String.format("%02d", secondsPlaying) + " / " + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);

                                time.setText(playingTime);
                                seekBar.setProgress((int) (playing * 100 / duration));
                                seekBar.postDelayed(this, 100);
                            }
                        }
                    };

                    mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            if (mp != null) {

                                int inSeconds = mp.getDuration() / 1000;
                                int minutes = inSeconds / 60;
                                int seconds = inSeconds - (minutes * 60);

                                String defaultTime = "00:00 / " + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
                                time.setText(defaultTime);
                                button.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.ic_action_playback_stop));
                                seekBar.postDelayed(timeAndSeekRunnable, 100);

                                mp.start();
                            }
                        }
                    });

                    mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            if (null != timeAndSeekRunnable) {
                                seekBar.removeCallbacks(timeAndSeekRunnable);
                            }
                            clearMediaPlayer();
                            clearView();
                        }
                    });
                    mediaPlayer.prepareAsync();

                } catch (Exception e) {
                    Log.e("f.ninaber", "Failed to play : " + e);
                    Toast.makeText(context, context.getResources().getString(R.string.ups_something_wrong), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.enable_sounds), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void stopPlaying() {
        if (context != null && file != null && button != null && seekBar != null && time != null && null != mediaPlayer && mediaPlayer.isPlaying()) {
            clearMediaPlayer();
            clearView();
        }
    }

    public boolean isPlaying() {
        if (null != mediaPlayer) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    private void clearMediaPlayer() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    private void clearView() {
        button.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.ic_action_playback_play));
        seekBar.setProgress(0);
        time.setText(context.getResources().getString(R.string.time_00));
    }
}
