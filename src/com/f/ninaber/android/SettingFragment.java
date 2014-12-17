package com.f.ninaber.android;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class SettingFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
	public static final int DELETE_NEVER = 0;
	public static final int DELETE_DAY = 1;
	public static final int DELETE_MONTH = 2;
	public static final int DELETE_YEAR = 3;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences_setting);
	}

	@Override
	public void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(getResources().getString(R.string.setting_storage_key))) {
			Preference deletePref = findPreference(key);
			int val = Integer.valueOf(sharedPreferences.getString(key, "1"));
			String[] s = getResources().getStringArray(R.array.setting_storage_entries);
			
			if(val > 0){
				deletePref.setSummary(getResources().getString(R.string.setting_storage_desc, s[val]));								
			}else{
				deletePref.setSummary(R.string.setting_storage_never_delete);
			}
		}
	}
}