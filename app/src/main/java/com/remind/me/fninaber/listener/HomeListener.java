package com.remind.me.fninaber.listener;

import android.graphics.Typeface;

import com.remind.me.fninaber.model.Task;

import java.util.List;

/**
 * Created by Windy on 29/06/2016.
 */
public interface HomeListener {
    void onTaskViewChange(String text, int visibility, float alpha);

    void onAdapterDataChange(List<Task> tasks);

    void onCollapingToolbarAnimation(Typeface typeface);

    void onCollapingToolbarTitle(String title);

    void onBackgroundChange(int id);
}
