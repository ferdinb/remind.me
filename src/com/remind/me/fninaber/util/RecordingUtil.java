package com.remind.me.fninaber.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.remind.me.fninaber.Constants;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

public class RecordingUtil {
	private static RecordingUtil instance;
	private MediaRecorder mRecorder;

	public static RecordingUtil getInstance() {
		if (null == instance) {
			instance = new RecordingUtil();
		}
		return instance;
	}

	public String startRecording() {
		stopRecording();

		File f = new File(Environment.getExternalStorageDirectory() + Constants.AUDIO_FOLDER);
		if (!f.exists()) {
			f.mkdirs();
		}

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
		String audioFileName = "AUDIO_" + timeStamp;
		File fileName = new File(f, audioFileName);

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
			return null;
		}
		return fileName.getAbsolutePath();
	}

	public void stopRecording() {
		if (null != mRecorder) {
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
		}
	}
}
