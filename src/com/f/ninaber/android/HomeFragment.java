package com.f.ninaber.android;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

import com.f.ninaber.android.adapter.TaskAdapter;
import com.f.ninaber.android.db.TableTask;
import com.f.ninaber.android.util.DateUtil;

public class HomeFragment extends Fragment implements OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
	private static final int CURSOR_LOADER_TASK = 100;
	private static final String TAG = HomeFragment.class.getSimpleName();
	private ListView mListView;
	private TaskAdapter mAdapter;
	private String mSelection;
	private String[] mArgs = { String.valueOf(DateUtil.getBeginningOfday()) };
	private String mOrder;
	private static final String ASC = " ASC";
	private static final String DESC = " DESC";
	private boolean isDescending;
	private View root;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSelection = TableTask.Column.TIMESTAMP + " > ?";
		mOrder = TableTask.Column.TIMESTAMP + ASC;
		getActivity().getSupportLoaderManager().initLoader(CURSOR_LOADER_TASK, null, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.fragment_home, container, false);
		root.findViewById(R.id.action_bar_add).setOnClickListener(this);
		root.findViewById(R.id.action_bar_sort).setOnClickListener(this);

		mListView = (ListView) root.findViewById(R.id.list_task);
		mAdapter = new TaskAdapter(getActivity(), null, false);
		mListView.setAdapter(mAdapter);

		return root;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.action_bar_add:
			Intent intent = new Intent(getActivity(), AddTaskActivity.class);
			startActivity(intent);
			break;
		case R.id.action_bar_sort: {
			isDescending =! isDescending;
			if (isDescending) {
				mOrder = TableTask.Column.TIMESTAMP + DESC;
				mAdapter.notifyDataSetChanged();
				mListView.invalidate();

			} else {
				mOrder = TableTask.Column.TIMESTAMP + ASC;
				mAdapter.notifyDataSetChanged();
				mListView.invalidate();
			}
			getActivity().getSupportLoaderManager().restartLoader(CURSOR_LOADER_TASK, null, this);			
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		if (cursor != null) {
			mAdapter.changeCursor(cursor);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.swapCursor(null);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(getActivity(), TableTask.CONTENT_URI, null, mSelection, mArgs, mOrder);
	}
}
