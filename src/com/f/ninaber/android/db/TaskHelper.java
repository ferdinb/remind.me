package com.f.ninaber.android.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.f.ninaber.android.model.Task;

public class TaskHelper {
	private static Uri URI = TableTask.CONTENT_URI;
	private static TaskHelper mInstance = null;

	public static TaskHelper getInstance() {
		if (mInstance == null) {
			mInstance = new TaskHelper();
		}
		return mInstance;
	}

	private boolean insert(ContentResolver resolver, Task task) {
		ContentValues values = new ContentValues();
		values.put(TableTask.Column.TID, task.getTid());
		values.put(TableTask.Column.TITLE, task.getTitle());
		values.put(TableTask.Column.NOTES, task.getNotes());
		values.put(TableTask.Column.TIMESTAMP, task.getTimestamp());
		values.put(TableTask.Column.TYPE, task.getType());
		values.put(TableTask.Column.REPEAT, task.getRepeat());
		values.put(TableTask.Column.PATH, task.getPath());

		if (isExist(resolver, task.getTid())) {
			return resolver.update(URI, values, TableTask.Column.TID + " = '" + task.getTid(), null) > 0;
		}
		return resolver.insert(URI, values) != null;
	}

	public boolean isExist(ContentResolver resolver, String tid) {
		Task task = queryByTID(resolver, tid);
		if (task != null && !TextUtils.isEmpty(task.getTid())) {
			return true;
		}
		return false;
	}

	public Task queryByTID(ContentResolver resolver, String TID) {
		Cursor cursor = resolver.query(URI, null, TableTask.Column.TID + " = '" + TID + "'", null, null);
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
		return resolver.delete(URI, TableTask.Column.TID + " = '" + TID + "'", null);
	}

	public int deleteAll(ContentResolver resolver) {
		return resolver.delete(URI, null, null);
	}

	public Cursor getCursorData(ContentResolver resolver) {
		Cursor cursor = resolver.query(URI, null, null, null, TableTask.Column.TIMESTAMP + " DESC");
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			return cursor;
		}
		return null;
	}

	public List<Task> queryAll(ContentResolver resolver) {
		Cursor cursor = getCursorData(resolver);
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

	public Task cursorToTask(Cursor cursor) {
		Task task = new Task();
		task.setTid(cursor.getString(cursor.getColumnIndex(TableTask.Column.TID)));
		task.setTitle(cursor.getString(cursor.getColumnIndex(TableTask.Column.TITLE)));
		task.setNotes(cursor.getString(cursor.getColumnIndex(TableTask.Column.NOTES)));
		task.setTimestamp(cursor.getLong(cursor.getColumnIndex(TableTask.Column.TIMESTAMP)));
		task.setRepeat(cursor.getString(cursor.getColumnIndex(TableTask.Column.REPEAT)));
		task.setType(cursor.getString(cursor.getColumnIndex(TableTask.Column.TYPE)));
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
