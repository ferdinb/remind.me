package com.f.ninaber.android;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.widget.ListView;

import com.f.ninaber.android.adapter.LeftAdapter;
import com.f.ninaber.android.model.LeftMenu;

public class HomeActivity extends FragmentActivity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupActionBar();		
		setContentView(R.layout.activity_home);		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new HomeFragment()).commit();
		}
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerList.setAdapter(new LeftAdapter(setupLeftMenu(), this));
	}
	
	private void setupActionBar(){
		if(actionBar == null){
			actionBar = getActionBar();						
		}
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
	}
	
	private List<LeftMenu> setupLeftMenu(){
		List<LeftMenu> list = new ArrayList<LeftMenu>();
		String[] str = getResources().getStringArray(R.array.left_menu);
		int[] ic = {R.drawable.btn_add_reminder, R.drawable.btn_add_sort, R.drawable.ic_location};
		for(int i = 0; i < str.length; i++){
			LeftMenu menu = new LeftMenu();
			menu.setTitle(str[i]);
			menu.setResource(ic[i]);
			list.add(menu);
		}	
		return list;
	}
}
