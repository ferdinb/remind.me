package com.f.ninaber.android;

import java.io.File;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TimePicker;

import com.f.ninaber.android.db.TaskHelper;
import com.f.ninaber.android.model.Task;
import com.f.ninaber.android.model.Type;
import com.f.ninaber.android.util.DateUtil;
import com.f.ninaber.android.util.ImageUtil;
import com.f.ninaber.android.widget.ArialText;

public class AddTaskActivity extends Activity implements OnClickListener, OnCheckedChangeListener {
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
	private LinearLayout repeatMonth;
	private LinearLayout repeatYear;
	private final static int REPEAT_DAY = 0;
	private final static int REPEAT_MONTH = 1;
	private final static int REPEAT_YEAR = 2;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
	            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
	            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
	            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		setFinishOnTouchOutside(false);

		setContentView(R.layout.activity_add_task);
		DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.90);
        getWindow().setLayout(screenWidth, LayoutParams.WRAP_CONTENT); //set below the setContentview
		
        
        repeatLayout = (LinearLayout) this.findViewById(R.id.action_bar_right);
        
		mDate = DateUtil.calendarDay();
		mTime = DateUtil.calendarTime();

		findViewById(R.id.add_task_date_group).setOnClickListener(this);
		findViewById(R.id.add_task_time_group).setOnClickListener(this);
		
		repeatDay = (LinearLayout)findViewById(R.id.action_bar_day);
		repeatDay.setOnClickListener(this);
		
		repeatMonth = (LinearLayout)findViewById(R.id.action_bar_month);
		repeatMonth.setOnClickListener(this);
		
		repeatYear = (LinearLayout)findViewById(R.id.action_bar_year);
		repeatYear.setOnClickListener(this);
		((Switch)findViewById(R.id.add_task_repeat_btn)).setOnCheckedChangeListener(this);

		dateView = (ArialText) findViewById(R.id.add_task_date);
		dateView.setText(mDate);
		timeView = (ArialText) findViewById(R.id.add_task_time);
		timeView.setText(mTime);

		editTitle = (EditText) findViewById(R.id.add_task_title);
		editNotes = (EditText) findViewById(R.id.add_task_notes);

		findViewById(R.id.action_bar_left).setOnClickListener(this);
		findViewById(R.id.action_bar_right).setOnClickListener(this);
		findViewById(R.id.activity_add_task_cancel).setOnClickListener(this);
		findViewById(R.id.activity_add_task_save).setOnClickListener(this);
		findViewById(R.id.activity_add_task_recorder).setOnClickListener(this);
		findViewById(R.id.activity_add_task_photo).setOnClickListener(this);
		findViewById(R.id.activity_add_task_gallery).setOnClickListener(this);
		findViewById(R.id.activity_add_task_maps).setOnClickListener(this);
		findViewById(R.id.add_task_image_remove).setOnClickListener(this);

		addAttachmentGroup = (LinearLayout) findViewById(R.id.add_task_attachments);
		photoGroup = (RelativeLayout) findViewById(R.id.add_task_image_group);
		photoAttachment = (ImageView) findViewById(R.id.add_task_image);
		
		Task task = (Task) getIntent().getSerializableExtra(Constants.TASK);
		if(task != null){
			setDataToView(task);
		}
		setRepeatTime(REPEAT_DAY);
	}
	
	private void setDataToView(Task task){
		dateView.setText(DateUtil.dateTimestamp(task.getTimestamp()));
		timeView.setText(DateUtil.timeTimestamp(task.getTimestamp()));
		editTitle.setText(task.getTitle());
		editNotes.setText(task.getNotes());
		
		String path = task.getPath();
		String type = task.getType();
		if(type.equalsIgnoreCase(Type.PHOTO.toString()) && !TextUtils.isEmpty(path)){
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

			if (!TextUtils.isEmpty(title) && timestamp > 0) {
				Task task = new Task();
				if(!TextUtils.isEmpty(existTID)){
					task.setTID(existTID);
				}else{
					task.setTID(String.valueOf(System.currentTimeMillis() / 1000));
				}
				task.setTitle(title);
				task.setNotes(notes);
				task.setTimestamp(timestamp);
				task.setStatus(Constants.ON_GOING);
				
				if(null != cameraUri){
					task.setPath(cameraUri.toString());;
					task.setType(Type.PHOTO.toString());
				}else{
					task.setType(Type.TEXT.toString());					
				}
				TaskHelper.getInstance().insertAsync(getContentResolver(), task);
				this.finish();
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
			setRepeatTime(REPEAT_DAY);
			break;
		case R.id.action_bar_month:
			setRepeatTime(REPEAT_MONTH);
			break;
		case R.id.action_bar_year:
			setRepeatTime(REPEAT_YEAR);
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
			if(requestCode == GALLERY && null != data){
				Uri uri = data.getData();
				photoAttachment.setImageURI(uri);
				
				doResize(uri);				
			} else if(requestCode == CAMERA && null != cameraUri){
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
		cameraUri = null;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(buttonView.getId() == R.id.add_task_repeat_btn){
			setUpAnimation();
			
			if(repeatLayout.getVisibility() == View.VISIBLE){				
				repeatLayout.setAnimation(fadeOut);				
				repeatLayout.setVisibility(View.GONE);
			}else{
				repeatLayout.setVisibility(View.VISIBLE);
				repeatLayout.setAnimation(fadeIn);				
			}
		}
	}
	
	private void setUpAnimation(){
		if(fadeIn == null){
			fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
		}
		
		if(fadeOut == null){
			fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
		}		
	}
	
	private void setRepeatTime(int time){
		repeatDay.setSelected(false);
		repeatMonth.setSelected(false);
		repeatYear.setSelected(false);
		
		switch (time) {
		case REPEAT_MONTH:
			repeatMonth.setSelected(true);
			break;
			
		case REPEAT_YEAR:
			repeatYear.setSelected(true);
			break;
			
		default:
			repeatDay.setSelected(true);
			break;
		}

	}
}