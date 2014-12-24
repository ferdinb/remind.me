package com.f.ninaber.android.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.text.TextUtils;

import com.f.ninaber.android.AddTaskActivity;
import com.f.ninaber.android.Constants;
import com.f.ninaber.android.HomeActivity;
import com.f.ninaber.android.R;
import com.f.ninaber.android.db.TaskHelper;
import com.f.ninaber.android.model.Task;
import com.f.ninaber.android.util.DateUtil;
import com.f.ninaber.android.util.NotificationsUtil;
import com.f.ninaber.android.util.TaskManager;

public class AlarmReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent != null) {
			String TID = intent.getStringExtra(Constants.TID);
			if (!TextUtils.isEmpty(TID)) {
				ContentResolver resolver = context.getContentResolver();

				Task task = TaskHelper.getInstance().getTaskByTID(resolver, TID);
				TaskHelper.getInstance().insertStatus(resolver, TID, Constants.EXPIRED);
				TaskManager.getInstance(context).startTaskAlarm(resolver, task.getTimestamp());

				sendNotification(context, task);
			}
		}
	}

	private void sendNotification(Context context, Task task) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		if (prefs.getBoolean(context.getResources().getString(R.string.setting_vibrate_key), true)) {
			NotificationsUtil.getInstance(context).vibratePattern();
		}

		if (prefs.getBoolean(context.getResources().getString(R.string.setting_popup_key), true)) {
			startActivity(context, task);
		}

		if (prefs.getBoolean(context.getResources().getString(R.string.setting_notification_bar_key), false)) {
			showNotifBar(context, DateUtil.dateTimestamp(task.getTimestamp()), task.getTitle(), task.getNotes());
		}

		if (prefs.getBoolean(context.getResources().getString(R.string.setting_sound_key), false)) {
			NotificationsUtil.getInstance(context).playRingtone(context);
		}
	}

	private void startActivity(Context context, Task task) {
		Intent i = new Intent(context, AddTaskActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra(Constants.TASK, task);
		i.putExtra(Constants.VIEW, true);
		i.putExtra(Constants.ALARM, true);
		context.startActivity(i);
	}

	private void showNotifBar(Context context, String day, String title, String notes) {
		Intent intent = new Intent(context, HomeActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

		Notification noti = new NotificationCompat.Builder(context).setContentTitle(day).setStyle(new NotificationCompat.BigTextStyle().bigText(title))
				.setSmallIcon(R.drawable.ic_icon).setContentIntent(pIntent).build();
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// hide the notification after its selected
		noti.flags |= Notification.FLAG_AUTO_CANCEL;

		notificationManager.notify(0, noti);
	}
}
