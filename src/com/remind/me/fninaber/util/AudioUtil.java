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

import com.remind.me.fninaber.R;

public class AudioUtil {
	private static AudioUtil instance;
	private MediaPlayer mediaPlayer;

	public static AudioUtil getInstance() {
		if (null == instance) {
			instance = new AudioUtil();
		}
		return instance;
	}

	public void playAudio(final Context context, File file, final ImageView button, final SeekBar seekBar, final TextView time) {
		Uri alert = file != null ? Uri.fromFile(file) : null;
		final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) != 0 && null != alert) {
			try {
				if (null == mediaPlayer) {
					mediaPlayer = new MediaPlayer();
				}
				mediaPlayer.setDataSource(context, alert);
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mediaPlayer.setLooping(false);
				mediaPlayer.prepareAsync();
				mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
					@Override
					public void onPrepared(MediaPlayer mp) {
						if (mp != null) {
							time.setText(mp.getDuration());
							button.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.ic_action_playback_stop));
							mp.start();
						}
					}
				});
				
				mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						button.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.ic_action_playback_play));
						mediaPlayer = null;
					}
				});
				
				Runnable runnable = new Runnable() {
					@Override
					public void run() {
						seekBar.setProgress(mediaPlayer.getCurrentPosition());
					}
				};				
				seekBar.postDelayed(runnable, 50);
				
			} catch (Exception e) {
				Log.e("f.ninaber", "Failed to play : " + e);
			}
		}
	}

}
