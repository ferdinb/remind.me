package com.remind.me.fninaber.util;

import com.remind.me.fninaber.Constants;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

	public static void write(Context context, String key, String value) {
		SharedPreferences sharedPref = context.getSharedPreferences(Constants.PREF_KEY_MASTER, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String read(Context context, String key) {
		SharedPreferences sharedPref = context.getSharedPreferences(Constants.PREF_KEY_MASTER, Context.MODE_PRIVATE);
		return sharedPref.getString(key, null);
	}
}
