package com.f.ninaber.android.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.f.ninaber.android.Constants;
import com.f.ninaber.android.model.Calendar;
import com.f.ninaber.android.model.Task;
import com.f.ninaber.android.util.DateUtil;

public class TaskHelper {
	private static Uri URI = TableTask.CONTENT_URI;
	private static TaskHelper mInstance = null;

	public static TaskHelper getInstance() {
		if (mInstance == null) {
			mInstance = new TaskHelper();
		}
		return mInstance;
	}

	public boolean insert(ContentResolver resolver, Task task) {
		ContentValues values = new ContentValues();
		values.put(TableTask.Column.TID, task.getTID());
		values.put(TableTask.Column.TITLE, task.getTitle());
		values.put(TableTask.Column.NOTES, task.getNotes());
		values.put(TableTask.Column.TIMESTAMP, task.getTimestamp());
		values.put(TableTask.Column.SNOOZE, task.getTimestamp());
		values.put(TableTask.Column.TYPE, task.getType());
		values.put(TableTask.Column.REPEAT, task.getRepeat());
		values.put(TableTask.Column.STATUS, task.getStatus());
		values.put(TableTask.Column.PATH, task.getPath());

		if (isExist(resolver, task.getTID())) {
			return resolver.update(URI, values, TableTask.Column.TID + " = \"" + task.getTID() + "\"", null) > 0;
		}
		return resolver.insert(URI, values) != null;
	}

	public boolean insertStatus(ContentResolver resolver, String TID, int status) {
		ContentValues values = new ContentValues();
		values.put(TableTask.Column.STATUS, status);

		if (isExist(resolver, TID)) {
			return resolver.update(URI, values, TableTask.Column.TID + " = \"" + TID + "\"", null) > 0;
		}
		return resolver.insert(URI, values) != null;
	}

	public boolean isExist(ContentResolver resolver, String tid) {
		Task task = queryByTID(resolver, tid);
		if (task != null && !TextUtils.isEmpty(task.getTID())) {
			return true;
		}
		return false;
	}

	public Task queryByTID(ContentResolver resolver, String TID) {
		Cursor cursor = resolver.query(URI, null, TableTask.Column.TID + " = \"" + TID + "\"", null, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			Task task = cursorToTask(cursor);
			cursor.close();
			return task;
		}
		return null;
	}

	public int getCursorCount(ContentResolver resolver) {
		Cursor cursor = resolver.query(URI, null, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			int result = cursor.getCount();
			cursor.close();
			return result;
		}
		return 0;
	}

	public int update(ContentResolver resolver, ContentValues values, String where, String[] selectionArgs) {
		return resolver.update(URI, values, where, selectionArgs);
	}

	public int deleteByTID(ContentResolver resolver, String TID) {
		return resolver.delete(URI, TableTask.Column.TID + " = \"" + TID + "\"", null);
	}

	public int deleteAll(ContentResolver resolver) {
		return resolver.delete(URI, null, null);
	}

	public Cursor getCursorDataDesc(ContentResolver resolver, String selection) {
		Cursor cursor = resolver.query(URI, null, selection, null, TableTask.Column.TIMESTAMP + " DESC");
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			return cursor;
		}
		return null;
	}
	
	public Cursor getCursorDataAsc(ContentResolver resolver, String selection) {
		Cursor cursor = resolver.query(URI, null, selection, null, TableTask.Column.TIMESTAMP + " ASC");
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			return cursor;
		}
		return null;
	}

	public Task getTaskByTID(ContentResolver resolver, String TID) {
		Task task = null;
		Cursor cursor = resolver.query(URI, null, TableTask.Column.TID + " = \"" + TID + "\"", null, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			task = cursorToTask(cursor);
		}
		return task;
	}

	public Task getFirstTimestamp(ContentResolver resolver, long timestamp) {
		Task task = null;
		Cursor cursor = resolver.query(URI, null, TableTask.Column.TIMESTAMP + " > \"" + timestamp + "\"" + " AND " + TableTask.Column.STATUS
				+ " = \"" + Constants.ON_GOING + "\"", null, TableTask.Column.TIMESTAMP + " ASC");
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			task = cursorToTask(cursor);
		}
		return task;
	}
	
	public Task getFirstSnooze(ContentResolver resolver, long timestamp) {
		Task task = null;
		Cursor cursor = resolver.query(URI, null, TableTask.Column.SNOOZE + " > \"" + timestamp + "\"", null, TableTask.Column.SNOOZE + " ASC");
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			task = cursorToTask(cursor);
		}
		return task;
	}

	public List<Task> queryAll(ContentResolver resolver) {
		Cursor cursor = getCursorDataDesc(resolver, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			List<Task> tasks = new ArrayList<Task>();

			for (int i = 0; i < cursor.getCount(); i++) {
				Task task = cursorToTask(cursor);
				if (task != null) {
					tasks.add(task);
				}
				cursor.moveToNext();
			}
			cursor.close();
			return tasks;
		}
		return null;
	}

	private List<Calendar> getAvailableTimestamp(ContentResolver resolver, Cursor cursor) {
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			List<Calendar> timestamps = new ArrayList<Calendar>();
			List<String> checks = new ArrayList<String>();
			for (int i = 0; i < cursor.getCount(); i++) {
				long timestamp = cursor.getLong(cursor.getColumnIndex(TableTask.Column.TIMESTAMP));
				String day = DateUtil.dateTimestamp(timestamp);
				String monthYear = day.substring(day.indexOf(" "), day.length());
				if (!checks.contains(monthYear)) {
					String d = day.substring(0, day.indexOf(","));
					Calendar calendar = new Calendar();
					calendar.setDay(d);
					calendar.setDateMonthYear(monthYear);
					calendar.setNumTask("1");
					timestamps.add(calendar);
					checks.add(monthYear);					
				}else{
					for(int x = 0; x < timestamps.size(); x ++){
						Calendar cal = timestamps.get(x);
						String comp = cal.getDateMonthYear();
						if(comp.equalsIgnoreCase(monthYear)){
							int numTask = Integer.valueOf(cal.getNumTask());
							cal.setNumTask(String.valueOf(++numTask));
							break;
						}
					}
				}
				cursor.moveToNext();
			}
			cursor.close();
			return timestamps;
		}

		return null;
	}

	public List<Calendar> getAvailableTimestamp(ContentResolver resolver, boolean desc){
		String selection = TableTask.Column.TIMESTAMP + " > " + "\"" + System.currentTimeMillis() + "\"";
		if(desc){
			return getAvailableTimestamp(resolver, getCursorDataDesc(resolver, selection));
		}
		return getAvailableTimestamp(resolver, getCursorDataAsc(resolver, selection));
	}
	
	public Task cursorToTask(Cursor cursor) {
		Task task = new Task();
		task.setTID(cursor.getString(cursor.getColumnIndex(TableTask.Column.TID)));
		task.setTitle(cursor.getString(cursor.getColumnIndex(TableTask.Column.TITLE)));
		task.setNotes(cursor.getString(cursor.getColumnIndex(TableTask.Column.NOTES)));
		task.setTimestamp(cursor.getLong(cursor.getColumnIndex(TableTask.Column.TIMESTAMP)));
		task.setSnooze(cursor.getLong(cursor.getColumnIndex(TableTask.Column.SNOOZE)));
		task.setRepeat(cursor.getInt(cursor.getColumnIndex(TableTask.Column.REPEAT)));
		task.setType(cursor.getString(cursor.getColumnIndex(TableTask.Column.TYPE)));
		task.setStatus(cursor.getInt(cursor.getColumnIndex(TableTask.Column.STATUS)));
		task.setPath(cursor.getString(cursor.getColumnIndex(TableTask.Column.PATH)));
		return task;
	}

	public void insertAsync(ContentResolver resolver, Task task) {
		new InsertTask(resolver).execute(task);
	}

	private class InsertTask extends AsyncTask<Task, Integer, Integer> {
		ContentResolver mResolver = null;

		public InsertTask(ContentResolver resolver) {
			mResolver = resolver;
		}

		@Override
		protected Integer doInBackground(Task... task) {
			insert(mResolver, task[0]);
			return 0;
		}

	}
}
