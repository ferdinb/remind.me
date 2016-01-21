package com.remind.me.fninaber;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.remind.me.fninaber.db.TaskHelper;
import com.remind.me.fninaber.model.Task;
import com.remind.me.fninaber.model.Type;
import com.remind.me.fninaber.util.AudioUtil;
import com.remind.me.fninaber.util.DateUtil;
import com.remind.me.fninaber.util.NotificationsUtil;
import com.remind.me.fninaber.util.RoundedTransform;
import com.remind.me.fninaber.util.TaskManager;
import com.remind.me.fninaber.widget.ArialText;
import com.squareup.picasso.Picasso;

import java.io.File;

public class AlarmActivity extends Activity implements OnClickListener {
    private static final String TAG = AddTaskActivity.class.getSimpleName();
    private boolean isAlarmTime;
    private KeyguardLock keyguardLock;
    private View repeatDay;
    private View repeatWeek;
    private View repeatMonth;
    private View repeatYear;
    private SwitchCompat switchRepeat;
    private ImageView photoAttachment;
    private ArialText dateView;
    private ArialText timeView;
    private ArialText editTitle;
    private ArialText editNotes;
    private LinearLayout repeatLayout;
    private Task task;
    private PowerManager.WakeLock wakeLock;
    private File audioPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        setFinishOnTouchOutside(false);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.90);
        getWindow().setLayout(screenWidth, LayoutParams.WRAP_CONTENT);

        repeatLayout = (LinearLayout) this.findViewById(R.id.repeat_group);
        repeatDay = (View) findViewById(R.id.action_bar_day);
        repeatWeek = (View) findViewById(R.id.action_bar_week);
        repeatMonth = (View) findViewById(R.id.action_bar_month);
        repeatYear = (View) findViewById(R.id.action_bar_year);
        switchRepeat = ((SwitchCompat) findViewById(R.id.alarm_repeat_btn));
        dateView = (ArialText) findViewById(R.id.alarm_date);
        timeView = (ArialText) findViewById(R.id.alarm_time);
        editTitle = (ArialText) findViewById(R.id.alarm_title);
        editNotes = (ArialText) findViewById(R.id.alarm_notes);
        task = (Task) getIntent().getSerializableExtra(Constants.TASK);
        photoAttachment = (ImageView) findViewById(R.id.alarm_image);
        findViewById(R.id.alarm_dismiss).setOnClickListener(this);
        findViewById(R.id.alarm_snooze).setOnClickListener(this);

        if (task == null) {
            this.finish();
        }

        setDataToView(task);
        wakeDevice();
    }

    private void setDataToView(Task task) {
        switchRepeat.setSelected(false);
        switchRepeat.setClickable(false);
        switchRepeat.setOnCheckedChangeListener(null);
        switchRepeat.setFocusable(false);
        switchRepeat.setEnabled(false);

        dateView.setText(DateUtil.dateTimestamp(task.getTimestamp()));
        timeView.setText(DateUtil.timeTimestamp(task.getTimestamp()));
        editTitle.setText(task.getTitle());
        editNotes.setText(task.getNotes());

        String path = task.getPath();
        String type = task.getType();
        int repeat = task.getRepeat();
        if (repeat >= 0) {
            switchRepeat.setChecked(true);
            if (null != repeatLayout) {
                repeatLayout.setVisibility(View.VISIBLE);
            }
            setRepeatTime(repeat);
        }

        if (TextUtils.isEmpty(task.getNotes())) {
            editNotes.setVisibility(View.GONE);
        }

        if (type.equalsIgnoreCase(Type.PHOTO.toString()) && !TextUtils.isEmpty(path)) {
            Uri uri = Uri.parse(path);
            int corner = (int) getResources().getDimension(R.dimen.padding_size_5dp);
            Picasso.with(AlarmActivity.this).load(uri).skipMemoryCache().transform(new RoundedTransform(corner, (int) getResources().getDimension(R.dimen.padding_size_3dp))).into(photoAttachment);
        } else if (type.equalsIgnoreCase(Type.AUDIO.toString()) && !TextUtils.isEmpty(path)) {
            findViewById(R.id.activity_add_task_play_audio_group).setVisibility(View.VISIBLE);
            findViewById(R.id.add_task_play_stop).setOnClickListener(this);
            audioPath = new File(task.getPath());
        }
    }

    public void wakeDevice() {
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), TAG);
        wakeLock.acquire();
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        keyguardLock = keyguardManager.newKeyguardLock(TAG);
        keyguardLock.disableKeyguard();

        String[] entries = getResources().getStringArray(R.array.setting_screen_values);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String timeoutValue = prefs.getString(getResources().getString(R.string.setting_screen_timeout_key), entries[0]);

        Log.e("f.ninaber", "timeoutValue : " + timeoutValue + " || entries[1] : " + entries[1]);

        if (timeoutValue.equalsIgnoreCase(entries[1])) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NotificationsUtil.getInstance(this).stopVibrate();
        NotificationsUtil.getInstance(this).stopRingtone();
        keyguardLock.reenableKeyguard();

        if (wakeLock != null) {
            wakeLock.release();
        }


        // Start - Update alarm
        TaskManager.getInstance(this).startTaskAlarm(this.getContentResolver(), System.currentTimeMillis());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean stopWhenTouch = prefs.getBoolean(getResources().getString(R.string.setting_touch_screen_key), false);
        if (stopWhenTouch) {
            NotificationsUtil.getInstance(this).stopVibrate();
            NotificationsUtil.getInstance(this).stopRingtone();
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.alarm_dismiss:
                if (isAlarmTime && null != task.getTID()) {
                    TaskHelper.getInstance().setSnoozeToDefault(getContentResolver(), task.getTID());
                }
                NotificationsUtil.getInstance(AlarmActivity.this).clearNotificaion(AlarmActivity.this, task.getTID());

                this.finish();
                break;

            case R.id.alarm_snooze:
                task.setSnooze(System.currentTimeMillis() + snoozeVal());
                Log.e("f.ninaber", "task.getTimestamp() : " + DateUtil.timeTimestamp(task.getTimestamp()));
                Log.e("f.ninaber", "task.getSnooze() : " + DateUtil.timeTimestamp(task.getSnooze()));
                TaskHelper.getInstance().insert(getContentResolver(), task);
                this.finish();
                break;
            case R.id.add_task_play_stop: {
                ImageView button = ((ImageView) findViewById(R.id.add_task_play_stop));
                TextView time = ((TextView) findViewById(R.id.activity_add_task_play_time));
                SeekBar seekBar = ((SeekBar) findViewById(R.id.add_task_seekbar));

                NotificationsUtil.getInstance(this).stopVibrate();
                NotificationsUtil.getInstance(this).stopRingtone();

                if (AudioUtil.getInstance().isPlaying()) {
                    AudioUtil.getInstance().stopPlaying();
                } else {
                    AudioUtil.getInstance().setDefaultView(this, audioPath, button, seekBar, time);
                    AudioUtil.getInstance().playAudio();
                }
            }
            default:
                break;
        }
    }

    private long snoozeVal() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String snoozeVal = prefs.getString(getResources().getString(R.string.setting_snooze_key), "300000");
        return Long.valueOf(snoozeVal);
    }

    private void setRepeatTime(int time) {
        repeatDay.setSelected(false);
        repeatWeek.setSelected(false);
        repeatMonth.setSelected(false);
        repeatYear.setSelected(false);

        switch (time) {
            case Constants.REPEAT_YEAR:
                repeatYear.setSelected(true);
                break;

            case Constants.REPEAT_MONTH:
                repeatMonth.setSelected(true);
                break;

            case Constants.REPEAT_WEEK:
                repeatWeek.setSelected(true);
                break;

            default:
                repeatDay.setSelected(true);
                break;
        }

    }
}