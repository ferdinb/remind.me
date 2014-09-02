package com.f.ninaber.android.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

	public static String calendarDay() {
		SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault());
		Calendar calendar = Calendar.getInstance();
		return dayFormat.format(calendar.getTime());
	}

	public static String calendarTime() {
		SimpleDateFormat dayFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
		Calendar calendar = Calendar.getInstance();
		return dayFormat.format(calendar.getTime());
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
}
