package com.remind.me.fninaber;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.text.TextUtils;

public class SettingFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
	public static final int DELETE_NEVER = 0;
	public static final int DELETE_DAY = 1;
	public static final int DELETE_MONTH = 2;
	public static final int DELETE_YEAR = 3;
	public static final int REQUEST_CODE = 99;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().getActionBar().setTitle(getActivity().getResources().getString(R.string.setting));

		addPreferencesFromResource(R.xml.preferences_setting);

		Preference preference = findPreference(getResources().getString(R.string.setting_pin_key));
		SharedPreferences pref = preference.getSharedPreferences();
		if (!TextUtils.isEmpty(pref.getString(getResources().getString(R.string.setting_pin_key), null))) {
			preference.setTitle(getResources().getString(R.string.pin_on));
		} else {
			preference.setTitle(getResources().getString(R.string.pin_off));
		}

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

			if (val > 0) {
				deletePref.setSummary(getResources().getString(R.string.setting_storage_desc, s[val]));
			} else {
				deletePref.setSummary(R.string.setting_storage_never_delete);
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			Preference preference = findPreference(getResources().getString(R.string.setting_pin_key));
			preference.setTitle(getResources().getString(R.string.pin_on));

		} else if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_CANCELED) {
			Preference preference = findPreference(getResources().getString(R.string.setting_pin_key));
			preference.setTitle(getResources().getString(R.string.pin_off));
		}
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		if (preference.getKey().equals(getResources().getString(R.string.setting_pin_key))) {
			Intent i = new Intent(getActivity(), PINActivity.class);
			startActivityForResult(i, REQUEST_CODE);
			return false;
		} else {
			return super.onPreferenceTreeClick(preferenceScreen, preference);
		}
	}
}