package com.f.ninaber.android;

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
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.f.ninaber.android.db.TaskHelper;
import com.f.ninaber.android.model.Task;
import com.f.ninaber.android.model.Type;
import com.f.ninaber.android.util.DateUtil;
import com.f.ninaber.android.util.NotificationsUtil;
import com.f.ninaber.android.util.RoundedTransform;
import com.f.ninaber.android.util.TaskManager;
import com.f.ninaber.android.widget.ArialText;
import com.squareup.picasso.Picasso;

public class AlarmActivity extends Activity implements OnClickListener {
	private static final String TAG = AddTaskActivity.class.getSimpleName();
	private boolean isAlarmTime;
	private KeyguardLock keyguardLock;
	private View repeatDay;
	private View repeatWeek;
	private View repeatMonth;
	private View repeatYear;
	private Switch switchRepeat;
	private ImageView photoAttachment;
	private ArialText dateView;
	private ArialText timeView;
	private ArialText editTitle;
	private ArialText editNotes;
	private LinearLayout repeatLayout;
	private Task task;

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
		switchRepeat = ((Switch) findViewById(R.id.alarm_repeat_btn));
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
		dateView.setText(DateUtil.dateTimestamp(task.getTimestamp()));
		timeView.setText(DateUtil.timeTimestamp(task.getTimestamp()));
		editTitle.setText(task.getTitle());
		editNotes.setText(task.getNotes());

		String path = task.getPath();
		String type = task.getType();
		int repeat = task.getRepeat();
		if (repeat >= 0 && null != repeatLayout) {
			switchRepeat.setChecked(true);
			switchRepeat.setSelected(false);
			switchRepeat.setClickable(false);
			repeatLayout.setVisibility(View.VISIBLE);
			setRepeatTime(repeat);
		}

		Log.e("f.ninaber", "Path : " + path);
		Log.e("f.ninaber", "Type : " + type);

		if (TextUtils.isEmpty(task.getNotes())) {
			editNotes.setVisibility(View.GONE);
		}

		if (type.equalsIgnoreCase(Type.PHOTO.toString()) && !TextUtils.isEmpty(path)) {
			Uri uri = Uri.parse(path);
			int corner = (int) getResources().getDimension(R.dimen.padding_size_5dp);
			Picasso.with(AlarmActivity.this).load(uri).skipMemoryCache().transform(new RoundedTransform(corner, (int) getResources().getDimension(R.dimen.padding_size_3dp))).into(photoAttachment);
		}
	}

	public void wakeDevice() {
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		WakeLock wakeLock = powerManager.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), TAG);
		wakeLock.acquire(1000);
		KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		keyguardLock = keyguardManager.newKeyguardLock(TAG);
		keyguardLock.disableKeyguard();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		NotificationsUtil.getInstance(this).stopVibrate();
		NotificationsUtil.getInstance(this).stopRingtone();
		keyguardLock.reenableKeyguard();

		// Start - Update alarm
		TaskManager.getInstance(this).startTaskAlarm(this.getContentResolver(), System.currentTimeMillis());
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		NotificationsUtil.getInstance(this).stopVibrate();
		NotificationsUtil.getInstance(this).stopRingtone();
		return super.onTouchEvent(event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.alarm_dismiss:
			if (isAlarmTime && null != task.getTID()) {
				TaskHelper.getInstance().setSnoozeToDefault(getContentResolver(), task.getTID());
			}
			this.finish();
			break;

		case R.id.alarm_snooze:
			task.setSnooze(task.getTimestamp() + snoozeVal());
			Log.e("f.ninaber", "task.getTimestamp() : " + DateUtil.timeTimestamp(task.getTimestamp()));
			Log.e("f.ninaber", "task.getSnooze() : " + DateUtil.timeTimestamp(task.getSnooze()));
			TaskHelper.getInstance().insert(getContentResolver(), task);
			this.finish();
			break;
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