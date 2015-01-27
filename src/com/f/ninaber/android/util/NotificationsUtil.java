package com.f.ninaber.android.util;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.f.ninaber.android.HomeActivity;
import com.f.ninaber.android.R;
import com.f.ninaber.android.model.Task;
import com.f.ninaber.android.model.Type;

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
		mediaPlayer = new MediaPlayer();
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
		stopRingtone();

		if (null != mediaPlayer && !mediaPlayer.isPlaying()) {
			Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
			final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
				try {
					mediaPlayer.setDataSource(context, alert);
					mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
					mediaPlayer.setLooping(true);
					mediaPlayer.prepare();
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

	public void showNotifBar(Context context, Task task, boolean isSound) {
		String day = DateUtil.dateTimestamp(task.getTimestamp());
		String title = task.getTitle();
		String notes = task.getNotes();

		Bitmap bitmap = null;
		if (null != task.getType() && task.getType().equalsIgnoreCase(Type.PHOTO.toString())) {
			try {
				bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(task.getPath()));
			} catch (Exception e) {
				Log.e("f.ninaber", "Notif exception : " + e);
			}
		}

		Intent intent = new Intent(context, HomeActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
		Notification noti = null;

		if (null != bitmap) {
			noti = new NotificationCompat.Builder(context).setContentTitle(title).setContentText(notes).setSmallIcon(R.drawable.ic_launcher)
					.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap)).setContentIntent(pIntent).build();
		} else {
			if (!TextUtils.isEmpty(notes)) {
				noti = new NotificationCompat.Builder(context).setContentTitle(title).setContentText(notes).setSmallIcon(R.drawable.ic_launcher).setContentIntent(pIntent).build();
			} else {
				noti = new NotificationCompat.Builder(context).setContentTitle(day).setContentText(title).setSmallIcon(R.drawable.ic_launcher).setContentIntent(pIntent).build();
			}

		}
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		if (isSound) {
			noti.defaults |= Notification.DEFAULT_SOUND;
		}
		noti.defaults |= Notification.DEFAULT_LIGHTS;
		noti.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(0, noti);
	}
}
