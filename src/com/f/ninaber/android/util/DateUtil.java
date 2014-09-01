package com.f.ninaber.android.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateUtil {
	
	public static String calendarDay(){
		SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy - HH:mm", Locale.getDefault());
		Calendar calendar = Calendar.getInstance();
		return dayFormat.format(calendar.getTime());
	}
}
