package com.f.ninaber.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.f.ninaber.android.adapter.CalendarAdapter;
import com.f.ninaber.android.db.TaskHelper;
import com.f.ninaber.android.model.Calendar;

public class CalendarActivity extends Activity implements OnItemClickListener {
	private GridView mGridView;
	private CalendarAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
						| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		setFinishOnTouchOutside(true);

		setContentView(R.layout.activity_calendar);
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int screenWidth = (int) (metrics.widthPixels * 0.90);
		getWindow().setLayout(screenWidth, LayoutParams.WRAP_CONTENT); // set
																		// below
																		// the
																		// setContentview

		mAdapter = new CalendarAdapter(TaskHelper.getInstance().getAvailableTimestamp(getContentResolver(), false), this);
		mGridView = (GridView) this.findViewById(R.id.calendar_gridview);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent data = new Intent();
		Calendar c = (Calendar) mAdapter.getItem(position);
		data.putExtra(SearchFragment.KEY_DAY, c.getDay() + ", " + c.getDateMonthYear());
		setResult(RESULT_OK, data);
		this.finish();
	}
}