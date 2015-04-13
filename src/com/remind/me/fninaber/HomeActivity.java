package com.remind.me.fninaber;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.remind.me.fninaber.adapter.LeftAdapter;
import com.remind.me.fninaber.model.LeftMenu;
import com.remind.me.fninaber.util.TaskManager;

public class HomeActivity extends FragmentActivity {
	private static final String TAG = HomeActivity.class.getSimpleName();
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBar actionBar;
	private ActionBarDrawerToggle mDrawerToggle;
	private static final int FRAGMENT_HOME = 0;
	private static final int FRAGMENT_HISTORY = 1;
	private static final int FRAGMENT_FEEDBACK = 2;
	private static final int FRAGMENT_SETTING = 3;
	private int currentFragment = FRAGMENT_HOME;
	private static final int REQUEST_CODE = 88;
	public static final int RESUlT_CLOSE = 77;
	public static final int RESUlT_REFRESH = 66;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		setupActionBar();

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerList.setAdapter(new LeftAdapter(setupLeftMenu(), this));

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.desc_home, R.string.desc_menu) {
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		TaskManager.getInstance(this).validityTask();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String pin = prefs.getString(getResources().getString(R.string.setting_pin_key), null);
		if (!TextUtils.isEmpty(pin)) {
			Intent i = new Intent(this, PINActivity.class);
			i.putExtra(Constants.PIN_LOCK_SCREEN, true);
			startActivityForResult(i, REQUEST_CODE);
		} else {
			refreshFragment();
		}
	}

	private void refreshFragment() {
		getFragmentManager().beginTransaction().add(R.id.container, new HomeFragment()).commit();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setupActionBar() {
		if (actionBar == null) {
			actionBar = getActionBar();
		}
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setIcon(getResources().getDrawable(R.drawable.ic_actionbar));
	}

	private List<LeftMenu> setupLeftMenu() {
		List<LeftMenu> list = new ArrayList<LeftMenu>();
		String[] str = getResources().getStringArray(R.array.left_menu);
		int[] ic = { R.drawable.ic_action_home, R.drawable.ic_action_clock, R.drawable.ic_action_gmail, R.drawable.ic_action_settings };
		for (int i = 0; i < str.length; i++) {
			LeftMenu menu = new LeftMenu();
			menu.setTitle(str[i]);
			menu.setResource(ic[i]);
			list.add(menu);
		}
		return list;
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position, long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		if (currentFragment != position) {
			Fragment fragment = null;
			switch (position) {
			case FRAGMENT_HISTORY:
				fragment = new HistoryFragment();
				break;
			case FRAGMENT_FEEDBACK:
				sendEmail();
				return;
			case FRAGMENT_SETTING:
				fragment = new SettingFragment();
				break;
			default:
				fragment = new HomeFragment();
				break;
			}
			currentFragment = position;
			doReplaceFragment(fragment, position);
		}
		closeDrawer(position);
	}

	private void doReplaceFragment(Fragment fragment, int position) {
		getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
	}

	private void closeDrawer(int position) {
		mDrawerList.setItemChecked(position, true);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	private void sendEmail() {
		Intent mEmail = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
		String to = Constants.EMAIL;
		mEmail.putExtra(Intent.EXTRA_EMAIL, new String[] { to });
		mEmail.putExtra(Intent.EXTRA_SUBJECT, Constants.SUBJECT);
		mEmail.setType("message/rfc822");
		startActivity(Intent.createChooser(mEmail, "Choose an email client to send your feedback"));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE && resultCode == RESUlT_CLOSE) {
			finish();
		} else if (requestCode == REQUEST_CODE && resultCode == RESUlT_REFRESH) {
			refreshFragment();
		}

	}

}
