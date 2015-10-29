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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.remind.me.fninaber.Constants;
import com.remind.me.fninaber.R;
import com.remind.me.fninaber.db.TaskHelper;
import com.remind.me.fninaber.model.Task;
import com.remind.me.fninaber.model.Type;
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

        List<String> deletedImages = TaskHelper.getInstance().deleteImageByTime(context.getContentResolver(), String.valueOf(timestamp));
        Log.e("f.ninaber", "DeletedImages : " + deletedImages.size());
        if (null != deletedImages && deletedImages.size() > 0) {
            deleteExpiredImage(deletedImages);
        }

        List<String> deletedAudio = TaskHelper.getInstance().deleteAudioByTime(context.getContentResolver(), String.valueOf(timestamp));
        Log.e("f.ninaber", "DeletedAudio : " + deletedAudio.size());
        if (null != deletedAudio && deletedAudio.size() > 0) {
            deleteExpiredAudio(deletedAudio);
        }

        TaskHelper.getInstance().deleteTaskByTime(context.getContentResolver(), String.valueOf(timestamp));
    }

    public void deleteExpiredImage(final List<String> deletedImages) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                for (int i = 0; null != deletedImages && i < deletedImages.size(); i++) {
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

    public void deleteExpiredAudio(final List<String> deletedAudio) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                for (int i = 0; null != deletedAudio && i < deletedAudio.size(); i++) {
                    String path = deletedAudio.get(i);
                    File file = new File(path);
                    if (file.exists()) {
                        file.delete();
                    }
                }
                return null;
            }
        }.execute();
    }


    public Task buildTask(Task existTask, String title, String notes, Uri imageUri, File audioFile, long timestamp, int repeat) {
        Task task = new Task();
        if (null != existTask && !TextUtils.isEmpty(existTask.getTID())) {
            task.setTID(existTask.getTID());
        } else {
            task.setTID(String.valueOf(System.currentTimeMillis() / 1000));
        }
        task.setTitle(title);
        task.setNotes(notes);
        task.setTimestamp(timestamp);
        task.setStatus(Constants.ON_GOING);
        task.setRepeat(repeat);
        task.setSnooze(-1);

        if (null != imageUri || null != audioFile) {
            if (null != imageUri) {
                task.setPath(imageUri.toString());
                task.setType(Type.PHOTO.toString());
            } else if (null != audioFile) {
                task.setPath(audioFile.getAbsolutePath());
                task.setType(Type.AUDIO.toString());
            }
        } else {
            task.setType(Type.TEXT.toString());
        }
        return task;
    }

    public int repeatVar(boolean isRepeat, View day, View week, View month, View year) {
        if (!isRepeat) {
            return -1;
        }

        if (day.isSelected()) {
            return Constants.REPEAT_DAY;
        } else if (week.isSelected()) {
            return Constants.REPEAT_WEEK;
        } else if (month.isSelected()) {
            return Constants.REPEAT_MONTH;
        } else if (year.isSelected()) {
            return Constants.REPEAT_YEAR;
        }
        return -1;
    }

}
