package com.f.ninaber.android.util;

import java.io.IOException;

import com.f.ninaber.android.HomeActivity;
import com.f.ninaber.android.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotificationsUtil {
	private static NotificationsUtil instance;
	private Vibrator vibrator;
	private long[] pattern = { 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000 };
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

	public void vibrate() {
		if (null != vibrator) {
			vibrator.vibrate(pattern, -1);
		}
	}

	public void stopVibrate() {
		if (null != vibrator) {
			vibrator.cancel();
		}
	}

	public void playRingtone(Context context) {
		if (null != mediaPlayer && !mediaPlayer.isPlaying()) {
			Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
			final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
				try {
					mediaPlayer.setDataSource(context, alert);
					mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
					mediaPlayer.setLooping(true);
					mediaPlayer.start();
				} catch (Exception e) {
					Log.e("f.ninaber", "Failed to play : " + e);
				}
			}
		}
	}

	public void stopRingtone() {
		if (null != mediaPlayer && mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
	}

	public void showNotifBar(Context context, String day, String title, String notes, boolean isSound) {
		Intent intent = new Intent(context, HomeActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
		Notification noti = new NotificationCompat.Builder(context).setContentTitle(title).setContentText(notes).setSmallIcon(R.drawable.ic_icon).setContentIntent(pIntent).build();
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		if (isSound) {
			noti.defaults |= Notification.DEFAULT_SOUND;
		}
		noti.defaults |= Notification.DEFAULT_LIGHTS;
		noti.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(0, noti);
	}
}
