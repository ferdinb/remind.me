package com.f.ninaber.android;

import java.io.File;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.f.ninaber.android.db.TaskHelper;
import com.f.ninaber.android.model.Task;
import com.f.ninaber.android.model.Type;
import com.f.ninaber.android.util.DateUtil;
import com.f.ninaber.android.util.ImageUtil;
import com.f.ninaber.android.util.NotificationsUtil;
import com.f.ninaber.android.util.TaskManager;
import com.f.ninaber.android.widget.ArialText;

public class AddTaskActivity extends Activity implements OnClickListener, OnCheckedChangeListener {
	private static final String TAG = AddTaskActivity.class.getSimpleName();
	private String mDate;
	private String mTime;
	private ArialText dateView;
	private ArialText timeView;
	private EditText editTitle;
	private EditText editNotes;
	private static final int GALLERY = 0;
	private static final int CAMERA = 1;
	private LinearLayout addAttachmentGroup;
	private RelativeLayout photoGroup;
	private ImageView photoAttachment;
	private Uri cameraUri;
	private String existTID;
	private LinearLayout repeatLayout;
	private Animation fadeIn;
	private Animation fadeOut;
	private LinearLayout repeatDay;
	private LinearLayout repeatWeek;
	private LinearLayout repeatMonth;
	private LinearLayout repeatYear;
	private Switch switchRepeat;
	private Button leftButton;
	private Button rightButton;
	private boolean isAlarmTime;
	private TextView title;
	private KeyguardLock keyguardLock;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFinishOnTouchOutside(false);

		setContentView(R.layout.activity_add_task);
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int screenWidth = (int) (metrics.widthPixels * 0.90);
		getWindow().setLayout(screenWidth, LayoutParams.WRAP_CONTENT);

		repeatLayout = (LinearLayout) this.findViewById(R.id.action_bar_right);
		title = (TextView) this.findViewById(R.id.action_bar_tittle);
		
		mDate = DateUtil.calendarDay();
		mTime = DateUtil.calendarTime();

		findViewById(R.id.add_task_date_group).setOnClickListener(this);
		findViewById(R.id.add_task_time_group).setOnClickListener(this);

		repeatDay = (LinearLayout) findViewById(R.id.action_bar_day);
		repeatDay.setOnClickListener(this);
		
		repeatWeek = (LinearLayout) findViewById(R.id.action_bar_week);
		repeatWeek.setOnClickListener(this);

		repeatMonth = (LinearLayout) findViewById(R.id.action_bar_month);
		repeatMonth.setOnClickListener(this);

		repeatYear = (LinearLayout) findViewById(R.id.action_bar_year);
		repeatYear.setOnClickListener(this);
		switchRepeat = ((Switch) findViewById(R.id.add_task_repeat_btn));
		switchRepeat.setOnCheckedChangeListener(this);

		dateView = (ArialText) findViewById(R.id.add_task_date);
		dateView.setText(mDate);
		timeView = (ArialText) findViewById(R.id.add_task_time);
		timeView.setText(mTime);

		editTitle = (EditText) findViewById(R.id.add_task_title);
		editNotes = (EditText) findViewById(R.id.add_task_notes);

		findViewById(R.id.action_bar_left).setOnClickListener(this);
		findViewById(R.id.action_bar_right).setOnClickListener(this);
		findViewById(R.id.activity_add_task_recorder).setOnClickListener(this);
		findViewById(R.id.activity_add_task_photo).setOnClickListener(this);
		findViewById(R.id.activity_add_task_gallery).setOnClickListener(this);
		findViewById(R.id.activity_add_task_maps).setOnClickListener(this);
		findViewById(R.id.add_task_image_remove).setOnClickListener(this);
		leftButton = (Button) findViewById(R.id.activity_add_task_cancel);
		leftButton.setOnClickListener(this);
		rightButton = (Button) findViewById(R.id.activity_add_task_save);
		rightButton.setOnClickListener(this);

		addAttachmentGroup = (LinearLayout) findViewById(R.id.add_task_attachments);
		photoGroup = (RelativeLayout) findViewById(R.id.add_task_image_group);
		photoAttachment = (ImageView) findViewById(R.id.add_task_image);
		setRepeatTime(Constants.REPEAT_DAY);

		Task task = (Task) getIntent().getSerializableExtra(Constants.TASK);
		if (task != null) {
			setDataToView(task);
		}

		boolean isView = getIntent().getBooleanExtra(Constants.VIEW, false);
		isAlarmTime = getIntent().getBooleanExtra(Constants.ALARM, false);
		
		if (isView) {
			editTitle.setFocusable(false);
			editNotes.setFocusable(false);
			findViewById(R.id.add_task_date_group).setEnabled(false);
			findViewById(R.id.add_task_date_group).setOnClickListener(null);
			findViewById(R.id.add_task_time_group).setOnClickListener(null);
			findViewById(R.id.add_task_time_group).setEnabled(false);
			findViewById(R.id.activity_add_task_recorder).setEnabled(false);
			findViewById(R.id.activity_add_task_photo).setEnabled(false);
			findViewById(R.id.activity_add_task_gallery).setEnabled(false);
			findViewById(R.id.activity_add_task_maps).setEnabled(false);
			switchRepeat.setOnCheckedChangeListener(null);
			switchRepeat.setFocusable(false);
			switchRepeat.setEnabled(false);
			switchRepeat.setClickable(false);
			repeatDay.setEnabled(false);
			repeatWeek.setEnabled(false);
			repeatMonth.setEnabled(false);
			repeatYear.setEnabled(false);
			
			if(!isAlarmTime){
				setFinishOnTouchOutside(true);							
				findViewById(R.id.activity_add_button_group).setVisibility(View.GONE);
				findViewById(R.id.activity_view_dummy).setVisibility(View.VISIBLE);
			}
		}
		

		if(isAlarmTime){
			title.setText(getResources().getString(R.string.alarm));
			leftButton.setText(getResources().getString(R.string.dismiss));
			rightButton.setText(getResources().getString(R.string.snooze));
			wakeDevice();
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
	protected void onPause() {
		super.onPause();
	}

	private void setDataToView(Task task) {
		dateView.setText(DateUtil.dateTimestamp(task.getTimestamp()));
		timeView.setText(DateUtil.timeTimestamp(task.getTimestamp()));
		editTitle.setText(task.getTitle());
		editNotes.setText(task.getNotes());

		String path = task.getPath();
		String type = task.getType();
		int repeat = task.getRepeat();
		if (repeat >= 0) {
			switchRepeat.setChecked(true);
			repeatLayout.setVisibility(View.VISIBLE);
			setRepeatTime(repeat);
		}

		if (type.equalsIgnoreCase(Type.PHOTO.toString()) && !TextUtils.isEmpty(path)) {
			Uri uri = Uri.parse(path);
			photoAttachment.setImageURI(uri);

			photoGroup.setVisibility(View.VISIBLE);
			addAttachmentGroup.setVisibility(View.GONE);
		}
		existTID = task.getTID();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_add_task_save:
			String title = editTitle.getText().toString();
			String notes = editNotes.getText().toString();
			String day = dateView.getText().toString();
			String time = timeView.getText().toString();
			long timestamp = DateUtil.timestampDay(day, time);
			boolean isRepeat = switchRepeat.isChecked();

			Log.e("f.ninaber", "Title : " + title + "|| Timestamp : " + timestamp);
			
			if (!TextUtils.isEmpty(title) && timestamp > 0) {
				if(timestamp < System.currentTimeMillis() && DateUtil.getDate(System.currentTimeMillis()).contentEquals(DateUtil.getDate(timestamp))){
					Toast.makeText(this, getResources().getString(R.string.set_time), Toast.LENGTH_SHORT).show();
					return;
				}
				
				Task task = new Task();
				if (!TextUtils.isEmpty(existTID)) {
					task.setTID(existTID);
				} else {
					task.setTID(String.valueOf(System.currentTimeMillis() / 1000));
				}
				task.setTitle(title);
				task.setNotes(notes);
				task.setTimestamp(timestamp);
				task.setStatus(Constants.ON_GOING);

				if (isRepeat) {
					if (repeatDay.isSelected()) {
						task.setRepeat(Constants.REPEAT_DAY);
					}else if (repeatWeek.isSelected()) {
						task.setRepeat(Constants.REPEAT_WEEK);
					}else if (repeatMonth.isSelected()) {
						task.setRepeat(Constants.REPEAT_MONTH);
					} else {
						task.setRepeat(Constants.REPEAT_YEAR);
					}
				} else {
					task.setRepeat(-1);
				}

				if (null != cameraUri) {
					task.setPath(cameraUri.toString());
					task.setType(Type.PHOTO.toString());
				} else {
					task.setType(Type.TEXT.toString());
				}
				
				if(!isAlarmTime){
					task.setSnooze(-1);
					TaskHelper.getInstance().insertAsync(getContentResolver(), task);					
				}else{
					task.setSnooze(System.currentTimeMillis() + snoozeVal());					
					Log.e("f.ninaber", "task.getTimestamp() : " + DateUtil.timeTimestamp(task.getTimestamp()));
					Log.e("f.ninaber", "task.getSnooze() : " + DateUtil.timeTimestamp(task.getSnooze()));
					TaskHelper.getInstance().insert(getContentResolver(), task);		
				}
				this.finish();
			} else {
				Toast.makeText(this, getResources().getString(R.string.add_title), Toast.LENGTH_SHORT).show();
				
			}
			break;

		case R.id.add_task_date_group: {
			int[] val = DateUtil.dayParseDate(mDate);
			DatePickerDialog datePicker = new DatePickerDialog(this, dateListener, val[2], val[1], val[0]);
			DatePicker picker = datePicker.getDatePicker();
			picker.setMinDate(System.currentTimeMillis() - 10000);
			datePicker.show();
			break;
		}
		case R.id.add_task_time_group: {
			String[] val = DateUtil.timeParseHour(mTime);
			TimePickerDialog timePicker = new TimePickerDialog(this, timeListener, Integer.valueOf(val[0]), Integer.valueOf(val[1]), true);
			timePicker.show();
			break;
		}
		case R.id.action_bar_left:
		case R.id.activity_add_task_cancel:
			if(isAlarmTime && null != existTID){
				TaskHelper.getInstance().setSnoozeToDefault(getContentResolver(), existTID);		
			}
			this.finish();
			break;

		case R.id.activity_add_task_photo: {
			takePhoto(CAMERA);
			break;
		}
		

		case R.id.activity_add_task_gallery: {
			takePhoto(GALLERY);
			break;
		}
		case R.id.activity_add_task_recorder: {
			break;
		}

		case R.id.activity_add_task_maps: {
			break;
		}

		case R.id.add_task_image_remove: {
			cameraUri = null;
			photoGroup.setVisibility(View.GONE);
			addAttachmentGroup.setVisibility(View.VISIBLE);
			photoAttachment.setImageBitmap(null);
			break;
		}

		case R.id.action_bar_day:
			setRepeatTime(Constants.REPEAT_DAY);
			break;
		case R.id.action_bar_week:
			setRepeatTime(Constants.REPEAT_WEEK);
			break;
		case R.id.action_bar_month:
			setRepeatTime(Constants.REPEAT_MONTH);
			break;
		case R.id.action_bar_year:
			setRepeatTime(Constants.REPEAT_YEAR);
			break;

		default:
			break;
		}
	}

	private OnDateSetListener dateListener = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			mDate = DateUtil.calendarGivenDay(dayOfMonth, monthOfYear + 1, year);
			if (!TextUtils.isEmpty(mDate)) {
				dateView.setText(mDate);
			}
		}
	};

	private OnTimeSetListener timeListener = new OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mTime = DateUtil.calendarGivenTime(hourOfDay, minute);
			if (!TextUtils.isEmpty(mTime)) {
				timeView.setText(mTime);
			}
		}
	};

	private void takePhoto(int type) {
		switch (type) {
		case GALLERY: {
			Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, GALLERY);
			break;
		}
		case CAMERA:
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if (intent.resolveActivity(getPackageManager()) != null) {
				File photoFile = null;
				try {
					photoFile = ImageUtil.createImageFile();
				} catch (Exception e) {
					Log.e("f.ninaber", "Error : " + e.getMessage());
				}
				if (photoFile != null) {
					cameraUri = Uri.fromFile(photoFile);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
					startActivityForResult(intent, CAMERA);
				}
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == GALLERY && null != data) {
				Uri uri = data.getData();
				photoAttachment.setImageURI(uri);

				doResize(uri);
			} else if (requestCode == CAMERA && null != cameraUri) {
				doResize(cameraUri);
			}
		} else {
			cameraUri = null;
		}
	}

	private void doResize(Uri uri) {
		new AsyncTask<Uri, Void, Uri>() {
			@Override
			protected Uri doInBackground(Uri... params) {
				return ImageUtil.getReziedImageUri(AddTaskActivity.this, params[0]);
			}

			protected void onPostExecute(Uri result) {
				cameraUri = result;
				photoGroup.setVisibility(View.VISIBLE);
				addAttachmentGroup.setVisibility(View.GONE);
				photoAttachment.setImageURI(result);
			};
		}.execute(uri);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(isAlarmTime){
			NotificationsUtil.getInstance(this).stopVibrate();
			NotificationsUtil.getInstance(this).stopRingtone();			
			keyguardLock.reenableKeyguard();
		}
		
		// Start - Update alarm
		TaskManager.getInstance(this).startTaskAlarm(this.getContentResolver(), System.currentTimeMillis());
		cameraUri = null;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.getId() == R.id.add_task_repeat_btn) {
			setUpAnimation();

			if (repeatLayout.getVisibility() == View.VISIBLE) {
				repeatLayout.setAnimation(fadeOut);
				repeatLayout.setVisibility(View.GONE);
			} else {
				repeatLayout.setVisibility(View.VISIBLE);
				repeatLayout.setAnimation(fadeIn);
			}
		}
	}

	private void setUpAnimation() {
		if (fadeIn == null) {
			fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
		}

		if (fadeOut == null) {
			fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
		}
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
	
	private long snoozeVal(){		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String snoozeVal = prefs.getString(getResources().getString(R.string.setting_snooze_key), "300000");
		return Long.valueOf(snoozeVal);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(isAlarmTime){
			NotificationsUtil.getInstance(this).stopVibrate();
			NotificationsUtil.getInstance(this).stopRingtone();
		}
		return super.onTouchEvent(event);
	}

}