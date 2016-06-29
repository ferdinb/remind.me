package com.remind.me.fninaber.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.remind.me.fninaber.R;
import com.remind.me.fninaber.adapter.HomeCardAdapter;
import com.remind.me.fninaber.listener.HomeListener;
import com.remind.me.fninaber.model.Task;
import com.remind.me.fninaber.presenter.HomePresenter;
import com.remind.me.fninaber.widget.BaseFragment;
import com.remind.me.fninaber.widget.FninaberText;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HomeFragment extends BaseFragment implements OnClickListener, AppBarLayout.OnOffsetChangedListener, HomeListener {
    private View root;
    private Context context;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private HomeCardAdapter mAdapter;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView background;
    private FninaberText numTaskView;
    private HomePresenter mPresenter;

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
        mPresenter = new HomePresenter(context, this);
        mPresenter.generateRandomWords();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.updateTask();
        mPresenter.doCheckTaskAndTime();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        mRecyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        setUpView();
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setAppbarBehaviour();
    }

    private void setUpView() {
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new HomeCardAdapter(context);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setAppbarBehaviour() {
        background = (ImageView) getActivity().findViewById(R.id.backdrop);
        numTaskView = (FninaberText) getActivity().findViewById(R.id.activity_home_num_task);
        collapsingToolbarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsing_toolbar);
        AppBarLayout mAppBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appbar);
        mAppBarLayout.addOnOffsetChangedListener(this);

        mPresenter.setCollapsingToolbar();
        mPresenter.setCollapsingToolbarTitle();
        mPresenter.generateRandomBackground();
    }

    @Override
    public void onClick(View v) {
        mPresenter.onClick(v.getId());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        mPresenter.doAnimationHeader(appBarLayout.getTotalScrollRange(), verticalOffset);
    }

    @Override
    public void onTaskViewChange(String text, int visibility, float alpha) {
        if (null != numTaskView) {
            numTaskView.setVisibility(visibility);
            numTaskView.setAlpha(alpha);
            if (!TextUtils.isEmpty(text)) {
                numTaskView.setText(text);
            }

        }
    }

    @Override
    public void onAdapterDataChange(List<Task> tasks) {
        if (null != mAdapter) {
            mAdapter.setTasks(tasks);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCollapingToolbarAnimation(Typeface typeface) {
        if (null != collapsingToolbarLayout) {
            collapsingToolbarLayout.setCollapsedTitleTypeface(typeface);
            collapsingToolbarLayout.setExpandedTitleTypeface(typeface);
        }
    }

    @Override
    public void onCollapingToolbarTitle(String title) {
        if (null != collapsingToolbarLayout) {
            collapsingToolbarLayout.setTitle(title);
        }
    }

    @Override
    public void onBackgroundChange(int id) {
        Picasso.with(context).load(id).fit().skipMemoryCache().centerCrop().into(background);
    }


}