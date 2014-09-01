package com.f.ninaber.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class HomeFragment extends Fragment implements OnClickListener{
	private static final String TAG = HomeFragment.class.getSimpleName();
	private View root;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.fragment_home, container, false);
		root.findViewById(R.id.action_bar_add).setOnClickListener(this);
		
		return root;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.action_bar_add:
			Intent intent = new Intent(getActivity(), AddTaskActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
}
