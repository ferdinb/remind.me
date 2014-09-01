package com.f.ninaber.android;

import android.app.Activity;
import android.os.Bundle;

import com.f.ninaber.android.util.DateUtil;
import com.f.ninaber.android.widget.ArialText;

public class AddTaskActivity extends Activity {
	private String mDate;
	private ArialText dateView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_task);
		mDate = DateUtil.calendarDay();
		
		
		dateView = (ArialText) findViewById(R.id.add_task_date);
		dateView.setText(mDate);
	}
}
