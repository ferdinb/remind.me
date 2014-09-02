package com.f.ninaber.android;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import com.f.ninaber.android.util.DateUtil;
import com.f.ninaber.android.widget.ArialText;

public class AddTaskActivity extends Activity implements OnClickListener{
	private String mDate;
	private String mTime;
	private ArialText dateView;
	private ArialText timeView;
	private LinearLayout dateLayout;
	private LinearLayout timeLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_task_date_group:
			DatePickerDialog datePicker = new DatePickerDialog(this, dateListener, DateUtil.calendarYear(), DateUtil.calendarMonth(), DateUtil.calendarDate());
			DatePicker picker = datePicker.getDatePicker();
			picker.setMinDate(System.currentTimeMillis() - 10000);
			datePicker.show();
			break;
			
		case R.id.add_task_time_group:			
			TimePickerDialog timePicker = new TimePickerDialog(this, timeListener, DateUtil.calendarHour(), DateUtil.calendarMinute(), true);
			timePicker.show();
			break;
		default:
			break;
		}
	}
	
	
	private OnDateSetListener dateListener = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			
		}
	};
	
	private OnTimeSetListener timeListener = new OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			
		}
	};
}
