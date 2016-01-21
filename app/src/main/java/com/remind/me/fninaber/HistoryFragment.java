package com.remind.me.fninaber;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.remind.me.fninaber.adapter.TaskAdapter;
import com.remind.me.fninaber.db.TableTask;
import com.remind.me.fninaber.db.TaskHelper;
import com.remind.me.fninaber.model.Task;
import com.remind.me.fninaber.util.DateUtil;
import com.remind.me.fninaber.util.TaskManager;
import com.remind.me.fninaber.widget.BaseFragment;

public class HistoryFragment extends BaseFragment implements OnClickListener, LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener,
		OnItemLongClickListener {
	private static final String TAG = HistoryFragment.class.getSimpleName();
	private static final int CURSOR_LOADER_TASK = 102;
	private ListView mListView;
	private TaskAdapter mAdapter;
	private String mOrder;
	private static final String ASC = " ASC";
	private View root;
	private BaseActivity activity;
	private static final String KEY_SELECTION = "selection";
	public static final String KEY_DAY = "day";

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (BaseActivity) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		this.activity = null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);

		// mSelection = null;
		mOrder = TableTask.Column.TIMESTAMP + ASC;
		mAdapter = new TaskAdapter(activity, null, false);
		activity.getLoaderManager().initLoader(CURSOR_LOADER_TASK, null, this);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		activity.getMenuInflater().inflate(R.menu.search, menu);

		SearchManager manager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
		SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
		search.setSearchableInfo(manager.getSearchableInfo(activity.getComponentName()));
		search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextChange(String query) {
				Bundle args = new Bundle();
				if (!TextUtils.isEmpty(query)) {
					StringBuilder selection = new StringBuilder();
					selection.append(TableTask.Column.TITLE + " LIKE " + "\"%" + query + "%\"");
					selection.append(" OR ");
					selection.append(TableTask.Column.NOTES + " LIKE " + "\"%" + query + "%\"");

					args.putString(KEY_SELECTION, selection.toString());
				}
				activity.getLoaderManager().restartLoader(CURSOR_LOADER_TASK, args, HistoryFragment.this);
				return true;
			}

			@Override
			public boolean onQueryTextSubmit(String query) {
				Bundle args = new Bundle();
				if (!TextUtils.isEmpty(query)) {
					StringBuilder selection = new StringBuilder();
					selection.append(TableTask.Column.TITLE + " LIKE " + "\"%" + query + "%\"");
					selection.append(" OR ");
					selection.append(TableTask.Column.NOTES + " LIKE " + "\"%" + query + "%\"");

					args.putString(KEY_SELECTION, selection.toString());
				}
				activity.getLoaderManager().restartLoader(CURSOR_LOADER_TASK, args, HistoryFragment.this);
				return true;
			}
		});

	}

	@Override
	public void onResume() {
		super.onResume();

		// Start - Update alarm
		TaskManager.getInstance(activity).startTaskAlarm(activity.getContentResolver(), System.currentTimeMillis());
		((Toolbar)activity.findViewById(R.id.toolbar)).setTitle(activity.getResources().getString(R.string.history));
	}

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.fragment_home, container, false);

		mListView = (ListView) root.findViewById(R.id.list_task);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
		return root;
	}

	@Override
	public void onClick(View v) {
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

		return new CursorLoader(activity, TableTask.CONTENT_URI, null, mSelection, null, mOrder);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (null != mAdapter) {
			final Task task = TaskHelper.getInstance().cursorToTask((Cursor) mAdapter.getItem(position));
			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int position) {
					switch (position) {
					case Constants.MENU_EDIT: {
						Task task = TaskHelper.getInstance().cursorToTask((Cursor) mAdapter.getItem(position));
						Intent i = new Intent(activity, AddTaskActivity.class);
						i.putExtra(Constants.TASK, task);
						activity.startActivity(i);
						break;
					}
					case Constants.MENU_DELETE: {
						TaskHelper.getInstance().deleteByTID(activity.getContentResolver(), task.getTID());
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (null != mAdapter) {
			Task task = TaskHelper.getInstance().cursorToTask((Cursor) mAdapter.getItem(position));
			Intent i = new Intent(activity, AddTaskActivity.class);
			i.putExtra(Constants.TASK, task);
			i.putExtra(Constants.VIEW, true);
			activity.startActivity(i);
		}
	}

	private AlertDialog createDialog(String TID, DialogInterface.OnClickListener listener) {
		String[] menuItems = { activity.getResources().getString(R.string.menu_edit), activity.getResources().getString(R.string.menu_delete) };
		AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
		dialog.setTitle("Select").setCancelable(true).setItems(menuItems, listener);
		return dialog.create();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_calendar: {
			if(TaskHelper.getInstance().getCursorCount(activity.getContentResolver()) > 0){
				Intent i = new Intent(activity, CalendarActivity.class);
				startActivityForResult(i, 0);
			}
			break;
		}
		
		default:
			break;
		}
		return true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		getLoaderManager().destroyLoader(CURSOR_LOADER_TASK);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK){
			String day = data.getStringExtra(KEY_DAY);
			long timestamp = DateUtil.timestampDay(day);
			long oneDayAfter = timestamp + (24 * 60 * 60 * 1000); // one day
			
			Bundle args = new Bundle();
			String selection = TableTask.Column.TIMESTAMP + " > " + "\"" + timestamp + "\"" + " AND " + TableTask.Column.TIMESTAMP + " < " + "\"" + oneDayAfter + "\"";
			args.putString(KEY_SELECTION, selection);
			activity.getLoaderManager().restartLoader(CURSOR_LOADER_TASK, args, HistoryFragment.this);
		}		
	}
}