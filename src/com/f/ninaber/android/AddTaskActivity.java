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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import com.f.ninaber.android.db.TaskHelper;
import com.f.ninaber.android.model.Task;
import com.f.ninaber.android.model.Type;
import com.f.ninaber.android.util.DateUtil;
import com.f.ninaber.android.util.ImageUtil;
import com.f.ninaber.android.widget.ArialText;
import com.squareup.picasso.Picasso;

public class AddTaskActivity extends Activity implements OnClickListener {
	private String mDate;
	private String mTime;
	private ArialText dateView;
	private ArialText timeView;
	private LinearLayout dateLayout;
	private LinearLayout timeLayout;
	private EditText editTitle;
	private EditText editNotes;
	private static final int GALLERY = 0;
	private static final int CAMERA = 1;
	private LinearLayout addAttachmentGroup;
	private RelativeLayout photoGroup;
	private ImageView photoAttachment;
	private Uri cameraUri;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFinishOnTouchOutside(false);

		setContentView(R.layout.activity_add_task);
		mDate = DateUtil.calendarDay();
		mTime = DateUtil.calendarTime();

		dateLayout = (LinearLayout) findViewById(R.id.add_task_date_group);
		dateLayout.setOnClickListener(this);

		timeLayout = (LinearLayout) findViewById(R.id.add_task_time_group);
		timeLayout.setOnClickListener(this);

		dateView = (ArialText) findViewById(R.id.add_task_date);
		dateView.setText(mDate);
		timeView = (ArialText) findViewById(R.id.add_task_time);
		timeView.setText(mTime);

		editTitle = (EditText) findViewById(R.id.add_task_title);
		editNotes = (EditText) findViewById(R.id.add_task_notes);

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
				task.setTid(String.valueOf(System.currentTimeMillis() / 1000));
				task.setTitle(title);
				task.setNotes(notes);
				task.setTimestamp(timestamp);
				task.setType(Type.TEXT.toString());
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
		case R.id.activity_add_task_cancel: {
			this.finish();
			break;
		}

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
			photoGroup.setVisibility(View.GONE);
			addAttachmentGroup.setVisibility(View.VISIBLE);
			photoAttachment.setImageBitmap(null);
			break;
		}

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
				doResize(uri);				
			} else if(requestCode == CAMERA && null != cameraUri){
				doResize(cameraUri);
			}

		}
	}

	private void doResize(Uri uri) {
		new AsyncTask<Uri, Void, Uri>() {
			@Override
			protected Uri doInBackground(Uri... params) {
//				return params[0];
				return ImageUtil.getReziedImageUri(AddTaskActivity.this, params[0]);
			}

			protected void onPostExecute(Uri result) {
				photoGroup.setVisibility(View.VISIBLE);
				addAttachmentGroup.setVisibility(View.GONE);
				photoAttachment.setImageURI(result);
				Picasso.with(AddTaskActivity.this).load(result).skipMemoryCache().into(photoAttachment);
			};

		}.execute(uri);
	}
}
