package com.remind.me.fninaber.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class DataProvider extends ContentProvider {
	private static final String AUTHORITY = "com.remind.me.fninaber";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
	private SqlHelper mDatabaseHelper = null;
	private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

	static {
		URI_MATCHER.addURI(AUTHORITY, TableTask.TABLE_NAME, TableUriCode.TABLE_TASK);
	}

	@Override
	public boolean onCreate() {
		mDatabaseHelper = new SqlHelper(getContext());
		return true;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

		long resultId = -1;
		Uri tempUri = null;

		switch (URI_MATCHER.match(uri)) {
		case TableUriCode.TABLE_TASK: {
			resultId = db.insert(TableTask.TABLE_NAME, null, values);
			tempUri = TableTask.CONTENT_URI;
			break;
		}

		default: {
			throw new UnsupportedOperationException("URI: " + uri + " not supported.");
		}
		}

		getContext().getContentResolver().notifyChange(uri, null);

		return tempUri.buildUpon().appendPath(String.valueOf(resultId)).build();
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();

		String table = null;
		table = getTableName(uri);

		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(table);

		Cursor c = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);

		return c;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		String table = null;
		table = getTableName(uri);
		getContext().getContentResolver().notifyChange(uri, null);
		return db.delete(table, selection, selectionArgs);
	}

	@Override
	public String getType(Uri uri) {
		return "";
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		String table = null;
		table = getTableName(uri);
		getContext().getContentResolver().notifyChange(uri, null);
		return db.update(table, values, selection, selectionArgs);
	}

	public String getTableName(Uri uri) {
		StringBuilder tableName = new StringBuilder();
		switch (URI_MATCHER.match(uri)) {
		case TableUriCode.TABLE_TASK:
			tableName.append(TableTask.TABLE_NAME);
			break;
		default:
			throw new UnsupportedOperationException("URI: " + uri + " not supported.");
		}
		return tableName.toString();
	}
}
