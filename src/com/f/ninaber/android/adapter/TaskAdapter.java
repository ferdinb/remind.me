package com.f.ninaber.android.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.f.ninaber.android.R;
import com.f.ninaber.android.db.TaskHelper;
import com.f.ninaber.android.model.Task;
import com.f.ninaber.android.util.DateUtil;
import com.f.ninaber.android.widget.ArialText;

public class TaskAdapter extends CursorAdapter {

	public TaskAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ArialText dateView = (ArialText) view.findViewById(R.id.adapter_task_date);
		ArialText timeView = (ArialText) view.findViewById(R.id.adapter_task_time);
		ArialText titleView = (ArialText) view.findViewById(R.id.adapter_task_title);
		ArialText notesView = (ArialText) view.findViewById(R.id.adapter_task_notes);

		Task task = TaskHelper.getInstance().cursorToTask(cursor);
		long timestamp = task.getTimestamp();
		
		dateView.setText(DateUtil.dateTimestamp(timestamp));
		timeView.setText(DateUtil.timeTimestamp(timestamp));
		titleView.setText(task.getTitle());
		notesView.setText(task.getNotes());
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return LayoutInflater.from(context).inflate(R.layout.adapter_task_item, parent, false);
	}

}
