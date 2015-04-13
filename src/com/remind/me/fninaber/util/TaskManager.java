package com.remind.me.fninaber.util;

import java.io.File;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.remind.me.fninaber.Constants;
import com.remind.me.fninaber.R;
import com.remind.me.fninaber.db.TaskHelper;
import com.remind.me.fninaber.model.Task;
import com.remind.me.fninaber.service.AlarmReceiver;
import com.remind.me.fninaber.service.BootReceiver;

public class TaskManager {
	private static final int ALARM_ID = 10;
	private static TaskManager instance;
	private AlarmManager alarmManager;
	private Context context;
	private static final int NEVER = 0;
	private static final int DAY = 1;
	private static final int WEEK = 2;
	private static final int MONTH = 3;

	public static TaskManager getInstance(Context context) {
		if (null == instance) {
			instance = new TaskManager(context.getApplicationContext());
		}
		return instance;
	}

	private TaskManager(Context context) {
		this.context = context;
		if (null == alarmManager) {
			alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		}
	}

	public void setAlarm(String TID, long timestamp) {
		Intent intent = new Intent(context, AlarmReceiver.class);
		intent.putExtra(Constants.TID, TID);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), ALARM_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.set(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent);
	}

	public void cancelAlarm() {
		Intent intent = new Intent(context, AlarmReceiver.class);
		PendingIntent alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), ALARM_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.cancel(alarmIntent);
	}

	public Task getFirstStart(ContentResolver resolver, long timestamp) {
		return TaskHelper.getInstance().getFirstTimestamp(resolver, timestamp);
	}

	public Task getfirstSnooze(ContentResolver resolver, long timestamp) {
		return TaskHelper.getInstance().getFirstSnooze(resolver, timestamp);
	}

	public boolean startTaskAlarm(ContentResolver resolver, long timestamp) {
		Task task = getFirstStart(resolver, timestamp);
		Task snoozeTask = getfirstSnooze(resolver, timestamp);

		if (null == task && null == snoozeTask) {
			disableBootReceiver();
			return false;
		}

		if (null != task && null != snoozeTask) {
			if (task.getTID().equalsIgnoreCase(snoozeTask.getTID())) {
				setAlarm(snoozeTask.getTID(), snoozeTask.getSnooze());
				enableBootReceiver();
				return true;
			}
		}

		long time = -1;
		String taskID = null;

		if (task != null) {
			time = task.getTimestamp();
			taskID = task.getTID();
		}

		if ((time <= 0 && snoozeTask != null && snoozeTask.getSnooze() > 0) || (time > 0 && snoozeTask != null && snoozeTask.getSnooze() > 0 && snoozeTask.getSnooze() < time)) {
			time = snoozeTask.getSnooze();
			taskID = snoozeTask.getTID();
		}

		Log.e("f.ninaber", "startTaskAlarm ==> " + DateUtil.timeTimestamp(time));
		setAlarm(taskID, time);
		enableBootReceiver();
		return true;
	}

	private void enableBootReceiver() {
		ComponentName receiver = new ComponentName(context, BootReceiver.class);
		PackageManager pm = context.getPackageManager();
		pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
	}

	private void disableBootReceiver() {
		ComponentName receiver = new ComponentName(context, BootReceiver.class);
		PackageManager pm = context.getPackageManager();
		pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
	}

	public void validityTask() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String deleteVal = prefs.getString(context.getResources().getString(R.string.setting_storage_key), "0");
		long timestamp = System.currentTimeMillis();
		switch (Integer.valueOf(deleteVal)) {
		case DAY:
			timestamp = DateUtil.getDayBefore(timestamp);
			break;
		case WEEK:
			timestamp = DateUtil.getWeekBefore(timestamp);
			break;
		case MONTH:
			timestamp = DateUtil.getMonthBefore(timestamp);
			break;
		default:
			return;
		}
		List<String> deletedImages = TaskHelper.getInstance().deleteByTime(context.getContentResolver(), String.valueOf(timestamp));
		Log.e("f.ninaber", "DeletedImages : " + deletedImages.size());
		if (deletedImages.size() > 0) {
			deleteExpiredImage(deletedImages);
		}
	}

	public void deleteExpiredImage(final List<String> deletedImages) {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				for (int i = 0; i < deletedImages.size(); i++) {
					String path = deletedImages.get(i);
					Uri uri = Uri.parse(path);
					File file = new File(uri.getPath());
					if (file.exists()) {
						file.delete();
					}
				}
				return null;
			}
		}.execute();
	}
}
