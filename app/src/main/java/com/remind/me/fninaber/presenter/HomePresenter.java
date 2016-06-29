package com.remind.me.fninaber.presenter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.View;

import com.remind.me.fninaber.AddTaskActivity;
import com.remind.me.fninaber.R;
import com.remind.me.fninaber.db.TaskHelper;
import com.remind.me.fninaber.listener.HomeListener;
import com.remind.me.fninaber.model.Task;
import com.remind.me.fninaber.util.DateUtil;
import com.remind.me.fninaber.util.ScreenUtil;

import java.util.List;

/**
 * Created by Windy on 29/06/2016.
 */
public class HomePresenter {
    private static final float PERCENTAGE_TO_HIDE_TITLE = 0.9f;
    private static final float PERCENTAGE_TO_ALPHA_TASK = 0.9f;
    private int[] drawables = {R.drawable.img_book, R.drawable.img_rainbow, R.drawable.img_coffee, R.drawable.img_sunset};
    private HomeListener mListener;
    private Context mContext;
    private ContentResolver mResolver;
    private List<Task> mTasks;
    private String mWords;
    private Resources mResources;

    public HomePresenter(Context mContext, HomeListener mListener) {
        this.mContext = mContext;
        this.mResolver = mContext.getContentResolver();
        this.mListener = mListener;
        this.mResources = mContext.getResources();
    }

    public void generateRandomWords() {
        String[] sWordsArray = mResources.getStringArray(R.array.motivation_word);
        mWords = sWordsArray[ScreenUtil.generateRandomNumber()];
    }

    public List<Task> getTasks() {
        mTasks = TaskHelper.getInstance().getAllAvailableTask(mResolver, true);
        return mTasks;
    }

    public int getTaskSize() {
        return null == mTasks ? 0 : mTasks.size();
    }

    private long[] getTodayTaskTimeAndNumber() {
        return TaskHelper.getInstance().numTaskAndStartTime(mResolver);
    }

    public void updateTask() {
        mListener.onAdapterDataChange(getTasks());
    }

    public void doCheckTaskAndTime() {
        long[] mTaskTime = getTodayTaskTimeAndNumber();
        if (null != mTaskTime) {
            mListener.onTaskViewChange(mResources.getString(R.string.today_you_have, mTaskTime[0], DateUtil.timeTimestamp(mTaskTime[1])), View.VISIBLE, 1);
        } else {
            String mDefault = mResources.getString(R.string.today_you_have, 0, 0);
            String mTaskValue = mDefault.substring(0, mDefault.indexOf('.'));
            mListener.onTaskViewChange(mTaskValue, View.VISIBLE, 1);
        }
    }

    public void setCollapsingToolbar() {
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "perfectly.ttf");
        mListener.onCollapingToolbarAnimation(typeface);
    }

    public void setCollapsingToolbarTitle() {
        mListener.onCollapingToolbarTitle(mWords);
    }

    public void generateRandomBackground() {
        mListener.onBackgroundChange(drawables[ScreenUtil.generateRandomNumber()]);
    }

    public void doAnimationHeader(int maxScroll, int verticalOffset) {
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;
        doAnimation(percentage);
    }

    private void doAnimation(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE) {
            mListener.onCollapingToolbarTitle(mResources.getString(R.string.num_task, getTaskSize()));
        } else {
            mListener.onCollapingToolbarTitle(mWords);
        }

        if (percentage >= PERCENTAGE_TO_ALPHA_TASK) {
            mListener.onTaskViewChange(null, View.GONE, 1);
        } else {
            mListener.onTaskViewChange(null, View.VISIBLE, 1f - percentage);
        }
    }

    public void onClick(int id) {
        switch (id) {
            case R.id.add_task_floating_btn:
                goToActivity(AddTaskActivity.class);
                break;
        }
    }

    private void goToActivity(Class<?> classes) {
        Intent intent = new Intent(mContext, classes);
        mContext.startActivity(intent);
    }
}
