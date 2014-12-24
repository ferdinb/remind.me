package com.f.ninaber.android;

import com.f.ninaber.android.adapter.TaskAdapter;
import com.f.ninaber.android.db.TableTask;
import com.f.ninaber.android.db.TaskHelper;
import com.f.ninaber.android.model.Task;
import com.f.ninaber.android.util.DateUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class DetailGridActivity extends Activity implements OnItemClickListener, OnItemLongClickListener, LoaderManager.LoaderCallbacks<Cursor> {
	private String mOrder = TableTask.Column.TIMESTAMP + " ASC";
	private static final String KEY_SELECTION = "selection";
	private static final int CURSOR_LOADER_TASK = 104;
	private TaskAdapter mAdapter;
	private ListView mListView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
	            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
	            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
	            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		setFinishOnTouchOutside(true);

		setContentView(R.layout.activity_detail_grid);
		DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.90);
        getWindow().setLayout(screenWidth, LayoutParams.WRAP_CONTENT);

		String day = getIntent().getStringExtra(HistoryFragment.KEY_DAY);
		if(TextUtils.isEmpty(day)){
			this.finish();
		}

		Log.e("f.ninaber", "day : " + day);
		long timestamp = DateUtil.timestampDay(day);
		long oneDayAfter = timestamp + (24 * 60 * 60 * 1000); // one day
		
		Bundle args = new Bundle();
		String selection = TableTask.Column.TIMESTAMP + " > " + "\"" + timestamp + "\"" + " AND " + TableTask.Column.TIMESTAMP + " < " + "\"" + oneDayAfter + "\"";
		args.putString(KEY_SELECTION, selection);
		
		getLoaderManager().initLoader(CURSOR_LOADER_TASK, args, this);		
		mListView = (ListView) this.findViewById(R.id.list_detail_grid);
		mAdapter = new TaskAdapter(this, null, false);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (R.id.list_detail_grid == parent.getId() && null != mAdapter) {
			Task task = TaskHelper.getInstance().cursorToTask((Cursor) mAdapter.getItem(position));
			Intent i = new Intent(DetailGridActivity.this, AddTaskActivity.class);
			i.putExtra(Constants.TASK, task);
			i.putExtra(Constants.VIEW, true);
			startActivity(i);
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		if (null != cursor && null != mAdapter) {
			mAdapter.changeCursor(cursor);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		if (null != mAdapter) {
			mAdapter.swapCursor(null);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle bundle) {
		String mSelection = null;
		if (null != bundle) {
			mSelection = bundle.getString(KEY_SELECTION);
		}
		
		Log.e("f.ninaber", "mselection : " + mSelection);
		return new CursorLoader(this, TableTask.CONTENT_URI, null, mSelection, null, mOrder);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (R.id.list_detail_grid == parent.getId() && null != mAdapter) {
			final Task task = TaskHelper.getInstance().cursorToTask((Cursor) mAdapter.getItem(position));
			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int position) {
					switch (position) {
					case Constants.MENU_EDIT: {
						Task task = TaskHelper.getInstance().cursorToTask((Cursor) mAdapter.getItem(position));
						Intent i = new Intent(DetailGridActivity.this, AddTaskActivity.class);
						i.putExtra(Constants.TASK, task);
						startActivity(i);
						break;
					}
					case Constants.MENU_DELETE: {
						TaskHelper.getInstance().deleteByTID(getContentResolver(), task.getTID());
						break;
					}
					}
					dialog.dismiss();
				}
			};
			AlertDialog dialog = createDialog(task.getTID(), listener);
			dialog.show();
		}
		return true;
	}
	
	private AlertDialog createDialog(String TID, DialogInterface.OnClickListener listener) {
		String[] menuItems = { getResources().getString(R.string.menu_edit), getResources().getString(R.string.menu_delete) };
		AlertDialog.Builder dialog = new AlertDialog.Builder(DetailGridActivity.this);
		dialog.setTitle("Select").setCancelable(true).setItems(menuItems, listener);
		return dialog.create();
	}
}