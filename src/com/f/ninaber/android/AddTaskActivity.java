package com.f.ninaber.android;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import com.f.ninaber.android.db.TaskHelper;
import com.f.ninaber.android.model.Task;
import com.f.ninaber.android.model.Type;
import com.f.ninaber.android.util.DateUtil;
import com.f.ninaber.android.widget.ArialText;

public class AddTaskActivity extends Activity implements OnClickListener {
	private String mDate;
	private String mTime;
	private ArialText dateView;
	private ArialText timeView;
	private LinearLayout dateLayout;
	private LinearLayout timeLayout;
	private EditText editTitle;
	private EditText editNotes;

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
		
		Button cancel = (Button) findViewById(R.id.activity_add_task_cancel);
		cancel.setOnClickListener(this);
		
		Button save = (Button) findViewById(R.id.activity_add_task_save);
		save.setOnClickListener(this);
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
			
			if(!TextUtils.isEmpty(title) && timestamp > 0){
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
}
