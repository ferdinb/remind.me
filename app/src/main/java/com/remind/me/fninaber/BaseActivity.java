package com.remind.me.fninaber;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by f.ninaber on 26/10/2015.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String timeFormat = prefs.getString(this.getResources().getString(R.string.setting_time_key), "true");
        Constants.is24HoursFormat = timeFormat.equalsIgnoreCase("true");
    }
}
