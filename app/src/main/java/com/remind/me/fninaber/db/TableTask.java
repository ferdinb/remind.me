package com.remind.me.fninaber.db;

import android.net.Uri;

public class TableTask {
	public static final String TABLE_NAME = "task";
	public static final Uri CONTENT_URI = DataProvider.CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

	public static class Column {
		public static final String _ID = "_id";
		public static final String TID = "tid";
		public static final String TITLE = "title";
		public static final String NOTES = "notes";
		public static final String TYPE = "type";
		public static final String TIMESTAMP = "timestamp";
		public static final String SNOOZE = "snooze";
		public static final String REPEAT = "repeat";
		public static final String STATUS = "status";
		public static final String PATH = "path";
	}
}
