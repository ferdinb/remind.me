package com.f.ninaber.android.service;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.f.ninaber.android.AddTaskActivity;
import com.f.ninaber.android.Constants;
import com.f.ninaber.android.db.TaskHelper;
import com.f.ninaber.android.model.Task;
import com.f.ninaber.android.util.DateUtil;

public class AlarmReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent != null){
			String TID = intent.getStringExtra(Constants.TID);
			if(!TextUtils.isEmpty(TID)){
				ContentResolver resolver = context.getContentResolver();
				
				Task task = TaskHelper.getInstance().getTaskByTID(resolver, TID);
				Log.e("f.ninaber", "Task ID : " + task.getTid() + " || date : " + DateUtil.dateTimestamp(task.getTimestamp()));				
				Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
			    vibrator.vibrate(2000);				
				
				TaskHelper.getInstance().insertStatus(resolver, TID, Constants.EXPIRED);
				
				Intent i = new Intent(context, AddTaskActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
			}
		}
	}

}
