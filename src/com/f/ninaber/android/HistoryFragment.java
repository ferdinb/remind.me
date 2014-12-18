package com.f.ninaber.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.f.ninaber.android.adapter.TaskAdapter;
import com.f.ninaber.android.db.TableTask;
import com.f.ninaber.android.db.TaskHelper;
import com.f.ninaber.android.model.Task;
import com.f.ninaber.android.util.TaskManager;

public class HistoryFragment extends Fragment implements OnClickListener, LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener,
		OnItemLongClickListener {
	private static final String TAG = HistoryFragment.class.getSimpleName();
	private static final int CURSOR_LOADER_TASK = 101;
	private ListView mListView;
	private TaskAdapter mAdapter;
	private String mSelection;
	private String[] mArgs = { String.valueOf(System.currentTimeMillis()) };
	private String mOrder;
	private static final String ASC = " ASC";
	private View root;
	private FragmentActivity activity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (FragmentActivity) activity;
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
		activity.getActionBar().setTitle(activity.getResources().getString(R.string.history));

		mSelection = TableTask.Column.TIMESTAMP + " < ?";
		mOrder = TableTask.Column.TIMESTAMP + ASC;
		mAdapter = new TaskAdapter(activity, null, false);
		activity.getLoaderManager().initLoader(CURSOR_LOADER_TASK, null, this);
	}

	@Override
	public void onResume() {
		super.onResume();

		// Start - Update alarm
		TaskManager.getInstance(activity).startTaskAlarm(activity.getContentResolver(), System.currentTimeMillis());
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
		if(null != mAdapter){
			mAdapter.swapCursor(null);			
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(activity, TableTask.CONTENT_URI, null, mSelection, mArgs, mOrder);
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
	public void onDestroy() {
		super.onDestroy();
		
		getLoaderManager().destroyLoader(CURSOR_LOADER_TASK);
	}
}