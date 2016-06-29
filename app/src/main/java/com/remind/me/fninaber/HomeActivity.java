package com.remind.me.fninaber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.remind.me.fninaber.fragment.HomeFragment;
import com.remind.me.fninaber.util.TaskManager;

public class HomeActivity extends BaseActivity implements OnClickListener {
    private static final int REQUEST_CODE = 88;
    public static final int RESUlT_CLOSE = 77;
    public static final int RESUlT_REFRESH = 66;
    private Toolbar toolbar;
    private FloatingActionButton btnAddTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupActionBar();

        btnAddTask = (FloatingActionButton) findViewById(R.id.add_task_floating_btn);
        btnAddTask.setOnClickListener(this);

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
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, new HomeFragment()).commit();
    }

    private void setupActionBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        toolbar.setSubtitle("asdjasdladshalkjdhdlkahjkdahsdakjdhasdjka");
//        toolbar.setTitle("opopopopopopopopopopopop");
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
