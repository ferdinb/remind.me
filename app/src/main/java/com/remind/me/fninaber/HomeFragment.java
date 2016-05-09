package com.remind.me.fninaber;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.remind.me.fninaber.adapter.HomeCardAdapter;
import com.remind.me.fninaber.db.TaskHelper;
import com.remind.me.fninaber.model.Task;
import com.remind.me.fninaber.util.DateUtil;
import com.remind.me.fninaber.util.ScreenUtil;
import com.remind.me.fninaber.widget.BaseFragment;
import com.remind.me.fninaber.widget.FninaberText;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

public class HomeFragment extends BaseFragment implements OnClickListener, AppBarLayout.OnOffsetChangedListener {
    private static final float PERCENTAGE_TO_HIDE_TITLE = 0.9f;
    private static final float PERCENTAGE_TO_ALPHA_TASK = 0.9f;
    private View root;
    private Context context;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private HomeCardAdapter mAdapter;
    private List<Task> tasks;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private int mTotalTask;
    private String[] words;
    private int[] drawables = {R.drawable.img_book, R.drawable.img_rainbow, R.drawable.img_coffee, R.drawable.img_sunset};
    private ImageView background;
    private FninaberText numTaskView;
    private String mSelectedWords;

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
        inflater.inflate(R.menu.home_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
        words = getResources().getStringArray(R.array.motivation_word);
        mSelectedWords = words[ScreenUtil.generateRandomNumber()];
    }

    @Override
    public void onResume() {
        super.onResume();
        tasks = TaskHelper.getInstance().getAllAvailableTask(context.getContentResolver(), true);
        mTotalTask = tasks.size();
        if (null != mAdapter) {
            mAdapter.setTasks(tasks);
            mAdapter.notifyDataSetChanged();
        }

        long[] mTaskTime = TaskHelper.getInstance().numTaskAndStartTime(getActivity().getContentResolver());
        if (null != mTaskTime) {
            numTaskView.setText(getResources().getString(R.string.today_you_have, mTaskTime[0], DateUtil.timeTimestamp(mTaskTime[1])));
        } else {
            String mDefault = getResources().getString(R.string.today_you_have, 0, 0);
            String mTaskValue = mDefault.substring(0, mDefault.indexOf('.'));
            numTaskView.setText(mTaskValue);
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

    private void setAppbarBehaviour() {
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "perfectly.ttf");
        background = (ImageView) getActivity().findViewById(R.id.backdrop);
        numTaskView = (FninaberText) getActivity().findViewById(R.id.activity_home_num_task);

        collapsingToolbarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setCollapsedTitleTypeface(tf);
        collapsingToolbarLayout.setExpandedTitleTypeface(tf);
        collapsingToolbarLayout.setTitle(mSelectedWords);

        Picasso.with(context).load(drawables[ScreenUtil.generateRandomNumber()]).fit().skipMemoryCache().centerCrop().into(background);
        AppBarLayout mAppBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appbar);
        mAppBarLayout.addOnOffsetChangedListener(this);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setAppbarBehaviour();
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
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;
        doAnimationHeader(percentage);
    }

    private void doAnimationHeader(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE) {
            collapsingToolbarLayout.setTitle(getResources().getString(R.string.num_task, mTotalTask));
        } else {
            collapsingToolbarLayout.setTitle(mSelectedWords);
        }

        if (percentage >= PERCENTAGE_TO_ALPHA_TASK) {
            numTaskView.setVisibility(View.GONE);
        } else {
            numTaskView.setVisibility(View.VISIBLE);
            numTaskView.setAlpha(1f - percentage);
        }
    }
}