package com.f.ninaber.android.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.f.ninaber.android.Constants;
import com.f.ninaber.android.db.TaskHelper;
import com.f.ninaber.android.model.Task;
import com.f.ninaber.android.service.AlarmReceiver;

public class TaskManager {
	private static final int ALARM_ID = 10;
	private static TaskManager instance;
	private AlarmManager alarmManager;
	private Context context;

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
		Log.e("f.ninaber", "Alarm 1 : " + task + "Alarm 2 : " + snoozeTask);
		if (null == task && null == snoozeTask) {
			return false;
		}
		
		long time = task.getTimestamp();
		if(snoozeTask!= null && snoozeTask.getSnooze() > 0 && snoozeTask.getSnooze() < task.getTimestamp()){
			time = snoozeTask.getSnooze();
		}
		
		Log.e("f.ninaber", "Time : " + time);
		Log.e("f.ninaber", "Hour : " + DateUtil.timeTimestamp(time));
		
		setAlarm(task.getTID(), time);
		return true;
	}
}
