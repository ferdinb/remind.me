package com.remind.me.fninaber.service;

import com.remind.me.fninaber.util.TaskManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.e("f.ninaber", "boot receiver");
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
    		TaskManager.getInstance(context).startTaskAlarm(context.getContentResolver(), System.currentTimeMillis());
        }
	}

}
