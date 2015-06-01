package com.remind.me.fninaber.util;

import java.util.HashMap;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.remind.me.fninaber.HomeActivity;
import com.remind.me.fninaber.R;
import com.remind.me.fninaber.model.Task;
import com.remind.me.fninaber.model.Type;

public class NotificationsUtil {
	private static NotificationsUtil instance;
	private static final int NOTIF_ID_LIMITER = 4;
	private Vibrator vibrator;
	private long[] normalPattern = { 0, 1000, 1000 };
	private static final String normalOffPattern = "1000";
	private static final int positionPattern = 2;
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

	public void vibratePattern(Context context) {
		if (null != vibrator) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			String patternOff = prefs.getString(context.getResources().getString(R.string.setting_vibrate_pattern_key), normalOffPattern);
			normalPattern[positionPattern] = Integer.valueOf(patternOff);
			vibrator.vibrate(normalPattern, 0);
		}
	}

	public void vibrate() {
		if (null != vibrator) {
			vibrator.vibrate(normalPattern, -1);
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
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			String uriString = prefs.getString(context.getResources().getString(R.string.setting_sound_alarm_key), null);
			Uri alert = uriString == null ? RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM) : Uri.parse(uriString);

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

	public Map<String, String> avalaibleRingtoneList(Context context, int type) {
		// RingtoneManager.TYPE_RINGTONE
		RingtoneManager manager = new RingtoneManager(context);
		manager.setType(type);
		Cursor cursor = manager.getCursor();
		Map<String, String> list = new HashMap<String, String>();
		while (cursor.moveToNext()) {
			String notificationTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
			String notificationUri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX);

			list.put(notificationTitle, notificationUri);
		}

		return list;
	}

	public void stopRingtone() {
		if (null != mediaPlayer && mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
	}

	public void prepareNotification(Context context, Task task, boolean isSound, boolean isPopupShow) {
		if (null != task.getType() && task.getType().equalsIgnoreCase(Type.PHOTO.toString())) {
			prepareNotificationBitmap(context, Uri.parse(task.getPath()), task, isSound, isPopupShow);
		} else {
			displayNotification(context, task, null, isSound, isPopupShow);
		}
	}

	public void clearNotificaion(Context context, String TID) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(calculateNotifID(TID));
	}

	private void prepareNotificationBitmap(final Context context, final Uri uri, final Task task, final boolean isSound, final boolean isPopupShow) {
		new AsyncTask<Uri, Void, Bitmap>() {
			@Override
			protected Bitmap doInBackground(Uri... params) {
				Bitmap bitmap = null;
				try {
					bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), params[0]);
				} catch (Exception e) {
					Log.e("f.ninaber", "Bitmap notification e : " + e);
				}
				return bitmap;
			}

			protected void onPostExecute(Bitmap result) {
				displayNotification(context, task, result, isSound, isPopupShow);
			};
		}.execute(uri);
	}

	private void displayNotification(Context context, Task task, Bitmap bitmap, boolean isSound, boolean isPopupShow) {
		String day = DateUtil.dateTimestamp(task.getTimestamp());
		String title = task.getTitle();
		String notes = task.getNotes();

		Intent intent = new Intent(context, HomeActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
		Notification noti = null;

		if (null != bitmap) {
			noti = new NotificationCompat.Builder(context).setContentTitle(title).setContentText(notes).setSmallIcon(R.drawable.ic_launcher)
					.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).setBigContentTitle(title).setSummaryText(notes)).setContentIntent(pIntent).build();
		} else {
			if (!TextUtils.isEmpty(notes)) {
				noti = new NotificationCompat.Builder(context).setContentTitle(title).setContentText(notes).setSmallIcon(R.drawable.ic_launcher).setContentIntent(pIntent).build();
			} else {
				noti = new NotificationCompat.Builder(context).setContentTitle(day).setContentText(title).setSmallIcon(R.drawable.ic_launcher).setContentIntent(pIntent).build();
			}

		}
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		if (isSound && !isPopupShow) {
			noti.defaults |= Notification.DEFAULT_SOUND;
		}
		noti.defaults |= Notification.DEFAULT_LIGHTS;
		noti.flags |= Notification.FLAG_AUTO_CANCEL;

		notificationManager.notify(calculateNotifID(task.getTID()), noti);
	}

	private int calculateNotifID(String TID) {
		return Integer.valueOf(TID.substring(TID.length() > NOTIF_ID_LIMITER ? TID.length() - NOTIF_ID_LIMITER : 0, TID.length()));
	}

}
