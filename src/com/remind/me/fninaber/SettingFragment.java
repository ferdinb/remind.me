package com.remind.me.fninaber;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

public class SettingFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
	public static final int DELETE_NEVER = 0;
	public static final int DELETE_DAY = 1;
	public static final int DELETE_MONTH = 2;
	public static final int DELETE_YEAR = 3;
	public static final int REQUEST_CODE_PIN = 99;
	public static final int REQUEST_CODE_RINGTONE = 88;

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
		} else if (key.equals(getResources().getString(R.string.setting_vibrate_pattern_key))){
			Preference patternPref = findPreference(key);
			int val = Integer.valueOf(sharedPreferences.getString(key, "1000"));
			String[] s = getResources().getStringArray(R.array.setting_pattern_entries);			
			switch (val) {
			case 10000:
				val = 1;
				break;
			case 30000:
				val = 2;
				break;
			case 60000:
				val = 3;
				break;
			case 300000:
				val = 4;
				break;

			default:
				val = 0;
				break;
			}			
			patternPref.setSummary(getResources().getString(R.string.setting_vibrate_pattern_summary, s[val]));
		}
		
		
		
		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_PIN && resultCode == Activity.RESULT_OK) {
			Preference preference = findPreference(getResources().getString(R.string.setting_pin_key));
			preference.setTitle(getResources().getString(R.string.pin_on));

		} else if (requestCode == REQUEST_CODE_PIN && resultCode == Activity.RESULT_CANCELED) {
			Preference preference = findPreference(getResources().getString(R.string.setting_pin_key));
			preference.setTitle(getResources().getString(R.string.pin_off));
		} else if (requestCode == REQUEST_CODE_RINGTONE && resultCode == Activity.RESULT_OK) {
			Preference preference = findPreference(getResources().getString(R.string.setting_sound_alarm_key));
			Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
			
			if (null == uri) {
				uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
			}
			preference.getEditor().putString(getResources().getString(R.string.setting_sound_alarm_key), uri.toString()).commit();
		}

	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		if (preference.getKey().equals(getResources().getString(R.string.setting_pin_key))) {
			Intent i = new Intent(getActivity(), PINActivity.class);
			startActivityForResult(i, REQUEST_CODE_PIN);
			return false;

		} else if (preference.getKey().equals(getResources().getString(R.string.setting_sound_alarm_key))) {
			String uriString = preference.getSharedPreferences().getString(getResources().getString(R.string.setting_sound_alarm_key), null);
			Uri uri = uriString == null ? RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM) : Uri.parse(uriString);

			Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alarm Sound");
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, uri);
			this.startActivityForResult(intent, REQUEST_CODE_RINGTONE);
			return false;
		} else {
			return super.onPreferenceTreeClick(preferenceScreen, preference);
		}
	}
}