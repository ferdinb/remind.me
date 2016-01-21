package com.remind.me.fninaber.widget;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.remind.me.fninaber.Constants;
import com.remind.me.fninaber.R;

/**
 * Created by f.ninaber on 26/10/2015.
 */
public class BaseFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String timeFormat = prefs.getString(this.getResources().getString(R.string.setting_time_key), "true");
        Constants.is24HoursFormat = timeFormat.equalsIgnoreCase("true");
    }
}
