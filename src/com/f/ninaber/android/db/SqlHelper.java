package com.f.ninaber.android.db;

import java.util.logging.Logger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlHelper extends SQLiteOpenHelper {
	private final String TAG = this.getClass().getSimpleName();

	public static final String DATABASE_NAME = "fninaber.db";
	private static final int DATABASE_VERSION = 1;

	public SqlHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion != newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TableTask.TABLE_NAME);
			createTable(db);
		}
	}

	private void createTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TableTask.TABLE_NAME + " ( "
				+ TableTask.Column._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ TableTask.Column.TID + " TEXT NULL, "
				+ TableTask.Column.TITLE + " TEXT NOT NULL, "
				+ TableTask.Column.NOTES + " TEXT NULL, "
				+ TableTask.Column.TIMESTAMP + " INT NOT NULL, "
				+ TableTask.Column.TYPE + " TEXT NOT NULL, "
				+ TableTask.Column.REPEAT + " TEXT NULL, "
				+ TableTask.Column.PATH + " TEXT NULL "
				+ " ); ");
	}
}
