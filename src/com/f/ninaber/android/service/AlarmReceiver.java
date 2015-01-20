package com.f.ninaber.android.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.text.TextUtils;
import android.util.Log;

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
			Log.e("f.ninaber", "AlarmReceiver TID : " + TID);
			if (!TextUtils.isEmpty(TID)) {
				ContentResolver resolver = context.getContentResolver();

				Task task = TaskHelper.getInstance().getTaskByTID(resolver, TID);
				if (null != task) {
					if (task.getRepeat() != -1) {
						setRepeatTime(context, task);
					} else {
						TaskHelper.getInstance().updateStatus(resolver, TID, Constants.EXPIRED);
					}
					sendNotification(context, task);
					TaskManager.getInstance(context).startTaskAlarm(resolver, System.currentTimeMillis());
				}
			}
		}
	}

	private void setRepeatTime(Context context, Task task) {
		int repeatType = task.getRepeat();
		long timestamp = task.getTimestamp();
		if (repeatType == Constants.REPEAT_DAY) {
			timestamp = DateUtil.getDayAhead(timestamp);
		}else if (repeatType == Constants.REPEAT_WEEK) {
			timestamp = DateUtil.getWeekAhead(timestamp);
		}else if (repeatType == Constants.REPEAT_MONTH) {
			timestamp = DateUtil.getMonthAhead(timestamp);
		} else if (repeatType == Constants.REPEAT_YEAR) {
			timestamp = DateUtil.getYearAhead(timestamp);
		}
		TaskHelper.getInstance().updateTimestamp(context.getContentResolver(), task.getTID(), timestamp);
	}

	private void sendNotification(Context context, Task task) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean isShowPopup = prefs.getBoolean(context.getResources().getString(R.string.setting_popup_key), true);
		boolean isVibrate = prefs.getBoolean(context.getResources().getString(R.string.setting_vibrate_key), true);
		boolean isShowNotifBar = prefs.getBoolean(context.getResources().getString(R.string.setting_notification_bar_key), false);
		boolean isSound = prefs.getBoolean(context.getResources().getString(R.string.setting_sound_key), false);

		if (isVibrate) {
			if (isShowPopup && !isShowNotifBar) {
				NotificationsUtil.getInstance(context).vibratePattern();
			} else if (isShowNotifBar && !isShowPopup) {
				NotificationsUtil.getInstance(context).vibrate();
			} else if (isShowPopup && isShowNotifBar) {
				NotificationsUtil.getInstance(context).vibratePattern();
			}
		}

		if (isShowPopup) {
			startActivity(context, task);
		}

		if (isShowNotifBar) {
			NotificationsUtil.getInstance(context).showNotifBar(context, DateUtil.dateTimestamp(task.getTimestamp()), task.getTitle(), task.getNotes(), isSound);
		}

		if (isSound && !isShowNotifBar) {
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
}
