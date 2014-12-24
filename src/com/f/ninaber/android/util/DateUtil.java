package com.f.ninaber.android.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

public class DateUtil {
	
	public static long timestampDay(String day, String time){
		StringBuilder builder = new StringBuilder();
		builder.append(day);
		builder.append("/");
		builder.append(time);
		
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy/HH:mm", Locale.getDefault());
		try {
			Date date = sdf.parse(builder.toString());
			return date.getTime();
		} catch (ParseException e) {
			Log.e("f.ninaber", "Exception: " + DateUtil.class.getSimpleName() + " | " + e);
		}
		return 0;
	}
	
	public static long timestampDay(String day){		
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault());
		try {
			Date date = sdf.parse(day);
			return date.getTime();
		} catch (ParseException e) {
			Log.e("f.ninaber", "Exception: " + DateUtil.class.getSimpleName() + " | " + e);
		}
		return 0;
	}

	public static String dateTimestamp(long timestamp){
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault());
		Date date = new Date(timestamp);
		return sdf.format(date);
	}
	
	public static String timeTimestamp(long timestamp){
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
		Date time = new Date(timestamp);
		return sdf.format(time);
	}
	
	public static String calendarDay() {
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault());
		Calendar calendar = Calendar.getInstance();
		return sdf.format(calendar.getTime());
	}

	public static String calendarTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
		Calendar calendar = Calendar.getInstance();
		return sdf.format(calendar.getTime());
	}

	public static int calendarDate() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.DATE);
	}

	public static int calendarMonth() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.MONTH);
	}

	public static int calendarYear() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.YEAR);
	}

	public static int calendarHour() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	public static int calendarMinute() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.MINUTE);
	}

	public static int[] dayParseDate(String day) {
		int[] value = new int[3];
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault());
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(sdf.parse(day));
			value[0] = calendar.get(Calendar.DATE);
			value[1] = calendar.get(Calendar.MONTH);
			value[2] = calendar.get(Calendar.YEAR);
		} catch (ParseException e) {
			Log.e("f.ninaber", "Exception: " + DateUtil.class.getSimpleName() + " | " + e);
		}
		return value;
	}

	public static String[] timeParseHour(String time) {
		String[] value = time.split(":");
		return value;
	}

	public static String calendarGivenDay(int day, int month, int year) {
		String sDay = String.valueOf(day);
		String sMonth = String.valueOf(month);

		StringBuilder builder = new StringBuilder();
		if (sDay.length() < 2) {
			builder.append("0" + day);
		} else {
			builder.append(day);
		}

		builder.append("/");
		if (sMonth.length() < 2) {
			builder.append("0" + month);
		} else {
			builder.append(month);
		}

		builder.append("/");
		builder.append(year);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
		try {
			Date date = sdf.parse(builder.toString());
			SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault());
			return dayFormat.format(date).toString();
		} catch (Exception e) {
			Log.e("f.ninaber", "Exception: " + DateUtil.class.getSimpleName() + " | " + e);
		}
		return null;
	}

	public static String calendarGivenTime(int hour, int minute) {
		String sHour = String.valueOf(hour);
		String sMinute = String.valueOf(minute);

		StringBuilder builder = new StringBuilder();
		if (sHour.length() < 2) {
			builder.append("0" + sHour);
		} else {
			builder.append(sHour);
		}
		builder.append(":");

		if (sMinute.length() < 2) {
			builder.append("0" + sMinute);
		} else {
			builder.append(sMinute);
		}
		return builder.toString();
	}
	
	public static String getDate(long timestamp){
		SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy", Locale.getDefault()); 
		return sdf.format(timestamp);
	}
	
	public static long getBeginningOfday(){
		Calendar calendar = Calendar.getInstance(Locale.getDefault());
		Date date = new Date(System.currentTimeMillis());
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);		
		return calendar.getTimeInMillis();
	}
}
