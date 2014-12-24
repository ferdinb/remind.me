package com.f.ninaber.android.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;

public class NotificationsUtil {
	private static NotificationsUtil instance;
	private Vibrator vibrator;
	private long[] pattern = { 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000 };
	private MediaPlayer mediaPlayer;

	public static NotificationsUtil getInstance(Context context) {
		if (null == instance) {
			instance = new NotificationsUtil(context);
		}
		return instance;
	}

	private NotificationsUtil(Context context) {
		vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		mediaPlayer = new MediaPlayer();
		try {
			mediaPlayer.setDataSource(context, notification);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
			mediaPlayer.setLooping(true);
			mediaPlayer.prepare();
		} catch (Exception e) {
			Log.e("f.ninaber", "Exception : " + e);
		}
	}

	public void vibratePattern() {
		if (null != vibrator) {
			vibrator.vibrate(pattern, 0);
		}
	}

	public void stopVibrate() {
		if (null != vibrator) {
			vibrator.cancel();
		}
	}

	public void playRingtone(Context context) {
		if (null != mediaPlayer && !mediaPlayer.isPlaying()) {
			final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0) {
				mediaPlayer.start();
			}
		}
	}

	public void stopRingtone() {
		if (null != mediaPlayer && mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
	}
}
