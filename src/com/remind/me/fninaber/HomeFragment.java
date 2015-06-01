package com.remind.me.fninaber;

import java.util.List;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ListView;

import com.remind.me.fninaber.adapter.CalendarAdapter;
import com.remind.me.fninaber.adapter.TaskAdapter;
import com.remind.me.fninaber.db.TableTask;
import com.remind.me.fninaber.db.TaskHelper;
import com.remind.me.fninaber.model.Calendar;
import com.remind.me.fninaber.model.Task;
import com.remind.me.fninaber.util.DateUtil;

public class HomeFragment extends Fragment implements OnClickListener, LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener,
		OnItemLongClickListener {
	private static final String TAG = HomeFragment.class.getSimpleName();
	private static final int CURSOR_LOADER_TASK = 100;
	private ListView mListView;
	private GridView mGridView;
	private TaskAdapter mAdapter;
	private String mSelection;
	private String[] mArgs = { String.valueOf(DateUtil.getBeginningOfday())};
//	private String[] mArgs = { String.valueOf(System.currentTimeMillis()) };
	private String mOrder;
	private static final String ASC = " ASC";
	private static final String DESC = " DESC";
	private boolean isDescending;
	private View root;
	private FragmentActivity activity;
	private CalendarAdapter mCalendarAdapter;
	private int sizeCalendar;

	private boolean isGridView;
	
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
	public void onPrepareOptionsMenu(Menu menu) {
		MenuItem item = menu.findItem(R.id.action_style);
		if(isGridView){
			item.setIcon(R.drawable.ic_action_view_list);
		}else{
			item.setIcon(R.drawable.ic_action_view_grid);
		}		
		super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.home_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);
		activity.getActionBar().setTitle(activity.getResources().getString(R.string.home));

		mSelection = TableTask.Column.TIMESTAMP + " > ?";
		mOrder = TableTask.Column.TIMESTAMP + ASC;
		mAdapter = new TaskAdapter(activity, null, false);
		activity.getLoaderManager().initLoader(CURSOR_LOADER_TASK, null, this);
	}

	@Override
	public void onResume() {
		super.onResume();		
		List<Calendar> val = TaskHelper.getInstance().getAvailableTimestamp(activity.getContentResolver(), isDescending);		
		int count = TaskHelper.getInstance().getCursorCount(activity.getContentResolver());				
		if (sizeCalendar != count) {
			if (null != mCalendarAdapter) {
				mCalendarAdapter.resetData(val);
			}
		}
		sizeCalendar = count;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.fragment_home, container, false);

		mListView = (ListView) root.findViewById(R.id.list_task);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
		mGridView = (GridView) root.findViewById(R.id.grid_task);
		
		mListView.setEmptyView(root.findViewById(R.id.empty_view));
		mGridView.setEmptyView(root.findViewById(R.id.empty_view));
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
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(activity, TableTask.CONTENT_URI, null, mSelection, mArgs, mOrder);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (R.id.list_task == parent.getId() && null != mAdapter) {
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
		if (R.id.list_task == parent.getId() && null != mAdapter) {
			Task task = TaskHelper.getInstance().cursorToTask((Cursor) mAdapter.getItem(position));
			Intent i = new Intent(activity, AddTaskActivity.class);
			i.putExtra(Constants.TASK, task);
			i.putExtra(Constants.VIEW, true);
			startActivity(i);
		} else if (R.id.grid_task == parent.getId() && null != mCalendarAdapter) {
			Intent i = new Intent(activity, DetailGridActivity.class);
			String day = ((Calendar) mCalendarAdapter.getItem(position)).getDay();
			String dateMonthYear = ((Calendar) mCalendarAdapter.getItem(position)).getDateMonthYear();
			i.putExtra(HistoryFragment.KEY_DAY, day + ", " + dateMonthYear);
			startActivity(i);
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
		case R.id.action_add: {
			Intent intent = new Intent(activity, AddTaskActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.action_sort: {
			isDescending = !isDescending;
			if (isDescending) {
				mOrder = TableTask.Column.TIMESTAMP + DESC;
				mAdapter.notifyDataSetChanged();
				mListView.invalidate();

			} else {
				mOrder = TableTask.Column.TIMESTAMP + ASC;
				mAdapter.notifyDataSetChanged();
				mListView.invalidate();
			}
			activity.getLoaderManager().restartLoader(CURSOR_LOADER_TASK, null, this);

			if (mGridView.getVisibility() == View.VISIBLE) {
				if (null != mCalendarAdapter) {
					List<Calendar> val = TaskHelper.getInstance().getAvailableTimestamp(activity.getContentResolver(), isDescending);
					mCalendarAdapter.resetData(val);
				}
			}

			break;
		}
		case R.id.action_style:
			if (mListView.getVisibility() == View.VISIBLE) {
				if (null == mCalendarAdapter) {
					List<Calendar> value = TaskHelper.getInstance().getAvailableTimestamp(activity.getContentResolver(), isDescending);
					mCalendarAdapter = new CalendarAdapter(value, activity);
					mGridView.setAdapter(mCalendarAdapter);
					mGridView.setOnItemClickListener(this);
					
					sizeCalendar = TaskHelper.getInstance().getCursorCount(activity.getContentResolver());
				}
				mListView.setVisibility(View.GONE);
				mGridView.setVisibility(View.VISIBLE);
				isGridView = true;
			} else {
				mListView.setVisibility(View.VISIBLE);
				mGridView.setVisibility(View.GONE);
				isGridView = false;
			}
			activity.invalidateOptionsMenu();
			break;
			
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
	
}