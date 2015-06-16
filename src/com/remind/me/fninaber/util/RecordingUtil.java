package com.remind.me.fninaber.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.remind.me.fninaber.Constants;
import com.remind.me.fninaber.R;

public class RecordingUtil {
	private static RecordingUtil instance;
	private MediaRecorder mRecorder;
	private Runnable runnable;
	private Handler recorderHandler;
	private boolean stopAnimation;
	private File fileName;
	private Handler timerHandler;
	private boolean stopTimer;
	private Runnable timmerRunnable;

	public static RecordingUtil getInstance() {
		if (null == instance) {
			instance = new RecordingUtil();
		}
		return instance;
	}

	private boolean startRecording() {
		stopRecording();

		File f = new File(Environment.getExternalStorageDirectory() + Constants.AUDIO_FOLDER);
		if (!f.exists()) {
			f.mkdirs();
		}

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
		String audioFileName = "AUDIO_" + timeStamp;
		fileName = new File(f, audioFileName);

		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		mRecorder.setOutputFile(fileName.getAbsolutePath());
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			mRecorder.prepare();
			mRecorder.start();
		} catch (Exception e) {
			Log.e("f.ninaber", "Exception : " + e);
			fileName = null;
			return false;
		}
		return fileName.getAbsolutePath() != null ? true : false;
	}

	private void stopRecording() {
		if (null != mRecorder) {
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
		}
	}

	public boolean prepareRecording(final Activity act) {
		NotificationsUtil.getInstance(act).vibrateRecord();
		boolean path = startRecording();
		startAnimation(act);
		startTimer((TextView) act.findViewById(R.id.activity_add_task_recording_time));
		return path;
	}

	private void startTimer(final TextView timerView) {
		timerView.setText("00:00");
		timerView.setVisibility(View.VISIBLE);
		timmerRunnable = new Runnable() {
			int seconds = 0;
			int minutes = 0;

			@Override
			public void run() {
				if (!stopTimer) {
					seconds++;
					if (60 == seconds) {
						minutes++;
						seconds = 0;
					}
					timerView.setText(String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
					timerHandler.postDelayed(this, 1000);
				} else {
					stopTimer = false;
					timerView.setVisibility(View.INVISIBLE);

					if (null != timerHandler && null != timmerRunnable) {
						timerHandler.removeCallbacks(timmerRunnable);
						timerHandler = null;
						timmerRunnable = null;
					}
				}
			}
		};
		timerHandler = new Handler();
		timerHandler.postDelayed(timmerRunnable, 1000);
	}

	public void ReleaseRecording(Context context, boolean isCancel) {
		stopAnimation = true;
		stopTimer = true;

		stopRecording();
		NotificationsUtil.getInstance(context).vibrateRecord();
		if (isCancel) {
			deleteAudioFile();
		} else {
			
		}
		fileName = null;
	}

	private void deleteAudioFile() {
		if (null != fileName && fileName.exists()) {
			fileName.delete();
		}
	}

	private void startAnimation(Activity act) {
		stopAnimation = false;
		Animation anim = AnimationUtils.loadAnimation(act, R.anim.zoom_fade);
		final View view = act.findViewById(R.id.activity_add_task_sound_animate);
		view.startAnimation(anim);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (!stopAnimation) {
					view.startAnimation(animation);
				}
			}
		});

	}
}
