package com.remind.me.fninaber;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.afollestad.materialdialogs.MaterialDialog;
import com.remind.me.fninaber.adapter.HomeCardAdapter;
import com.remind.me.fninaber.db.TaskHelper;
import com.remind.me.fninaber.model.Task;
import com.remind.me.fninaber.widget.BaseFragment;

import java.util.List;

public class HomeFragment extends BaseFragment implements OnClickListener, OnItemClickListener, OnItemLongClickListener {
    private View root;
    private Context context;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private HomeCardAdapter mAdapter;
    private List<Task> tasks;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        tasks = TaskHelper.getInstance().getAllAvailableTask(context.getContentResolver(), true);
        if (null != mAdapter) {
            mAdapter.setTasks(tasks);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        mRecyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new HomeCardAdapter(tasks, context);
        mRecyclerView.setAdapter(mAdapter);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_task_floating_btn:
                Intent intent = new Intent(context, AddTaskActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//        if (R.id.list_task == parent.getId() && null != mAdapter) {
//            final Task task = TaskHelper.getInstance().cursorToTask((Cursor) mAdapter.getItem(position));
//            MaterialDialog.ListCallback callback = new MaterialDialog.ListCallback() {
//                @Override
//                public void onSelection(MaterialDialog materialDialog, View view, int position, CharSequence charSequence) {
//                    switch (position) {
//                        case Constants.MENU_EDIT: {
//                            Task task = TaskHelper.getInstance().cursorToTask((Cursor) mAdapter.getItem(position));
//                            Intent i = new Intent(context, AddTaskActivity.class);
//                            i.putExtra(Constants.TASK, task);
//                            context.startActivity(i);
//                            break;
//                        }
//                        case Constants.MENU_DELETE: {
//                            if (null != task.getPath()) {
//                                if (task.getType().equalsIgnoreCase(Type.PHOTO.toString())) {
//                                    Uri uri = Uri.parse(task.getPath());
//                                    File f = new File(uri.getPath());
//                                    if (f.exists()) {
//                                        f.delete();
//                                    }
//                                } else if (task.getType().equalsIgnoreCase(Type.AUDIO.toString())) {
//                                    File f = new File(task.getPath());
//                                    if (f.exists()) {
//                                        f.delete();
//                                    }
//                                }
//                            }
//                            TaskHelper.getInstance().deleteByTID(context.getContentResolver(), task.getTID());
//                            break;
//                        }
//                    }
//                    materialDialog.dismiss();
//                }
//            };
//
//
//            MaterialDialog.Builder materialDialog = createMaterialDialogBuilder(callback);
//            materialDialog.show();
//        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        if (R.id.list_task == parent.getId() && null != mAdapter) {
//            Task task = TaskHelper.getInstance().cursorToTask((Cursor) mAdapter.getItem(position));
//            Intent i = new Intent(context, AddTaskActivity.class);
//            i.putExtra(Constants.TASK, task);
//            i.putExtra(Constants.VIEW, true);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(i);
//        } else if (R.id.grid_task == parent.getId() && null != mCalendarAdapter) {
//            Intent i = new Intent(context, DetailGridActivity.class);
//            String day = ((Calendar) mCalendarAdapter.getItem(position)).getDay();
//            String dateMonthYear = ((Calendar) mCalendarAdapter.getItem(position)).getDateMonthYear();
//            i.putExtra(HistoryFragment.KEY_DAY, day + ", " + dateMonthYear);
//            startActivity(i);
//        }
    }


    private MaterialDialog.Builder createMaterialDialogBuilder(MaterialDialog.ListCallback callback) {
        String[] menuItems = {getResources().getString(R.string.menu_edit), getResources().getString(R.string.menu_delete)};
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title("Select Action");
        builder.cancelable(true);
        builder.items(menuItems);
        builder.itemsCallback(callback);
        builder.buttonRippleColor(getResources().getColor(R.color.yellow_900));
        return builder;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_sort: {
//                isDescending = !isDescending;
//                if (isDescending) {
//                    mOrder = TableTask.Column.TIMESTAMP + DESC;
//                    mAdapter.notifyDataSetChanged();
//                    mListView.invalidate();
//
//                } else {
//                    mOrder = TableTask.Column.TIMESTAMP + ASC;
//                    mAdapter.notifyDataSetChanged();
//                    mListView.invalidate();
//                }
//                getLoaderManager().restartLoader(CURSOR_LOADER_TASK, null, this);
//
//                if (mGridView.getVisibility() == View.VISIBLE) {
//                    if (null != mCalendarAdapter) {
//                        List<Calendar> val = TaskHelper.getInstance().getAvailableTimestamp(context.getContentResolver(), isDescending);
//                        mCalendarAdapter.resetData(val);
//                    }
//                }
//
//                break;
//            }
//            case R.id.action_style:
//                if (mListView.getVisibility() == View.VISIBLE) {
//                    if (null == mCalendarAdapter) {
//                        List<Calendar> value = TaskHelper.getInstance().getAvailableTimestamp(context.getContentResolver(), isDescending);
//                        mCalendarAdapter = new CalendarAdapter(value, context);
//                        mGridView.setAdapter(mCalendarAdapter);
//                        mGridView.setOnItemClickListener(this);
//
//                        sizeCalendar = TaskHelper.getInstance().getCursorCount(context.getContentResolver());
//                    }
//                    mListView.setVisibility(View.GONE);
//                    mGridView.setVisibility(View.VISIBLE);
//                    isGridView = true;
//                } else {
//                    mListView.setVisibility(View.VISIBLE);
//                    mGridView.setVisibility(View.GONE);
//                    isGridView = false;
//                }
//                getActivity().invalidateOptionsMenu();
//                break;
//
//            default:
//                break;
//        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}