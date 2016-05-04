package com.remind.me.fninaber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.remind.me.fninaber.util.TaskManager;

public class HomeActivity extends BaseActivity implements OnClickListener {
    private DrawerLayout mDrawerLayout;
    private static final int REQUEST_CODE = 88;
    public static final int RESUlT_CLOSE = 77;
    public static final int RESUlT_REFRESH = 66;
    private Toolbar toolbar;
    private FloatingActionButton btnAddTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupActionBar();
        btnAddTask = (FloatingActionButton) findViewById(R.id.add_task_floating_btn);
        btnAddTask.setOnClickListener(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

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

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();

                        Fragment fragment = null;
                        switch (menuItem.getItemId()) {
                            case R.id.nav_history:
                                fragment = new HistoryFragment();
                                break;
                            case R.id.nav_feedback:
                                sendEmail();
                                break;
                            case R.id.nav_settings:
                                fragment = new SettingFragment();
                                break;
                            default:
                                fragment = new HomeFragment();
                                break;
                        }
                        doReplaceFragment(fragment);
                        return true;
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, new HomeFragment()).commit();
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void doReplaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, fragment).commit();
    }

    private void sendEmail() {
        Intent mEmail = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
        String to = Constants.EMAIL;
        mEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_task_floating_btn:
                Intent intent = new Intent(this, AddTaskActivity.class);
                startActivity(intent);
                break;
        }
    }
}
