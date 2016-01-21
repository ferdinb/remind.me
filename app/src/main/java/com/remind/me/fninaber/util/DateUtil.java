package com.remind.me.fninaber.util;

import android.text.format.Time;
import android.util.Log;

import com.remind.me.fninaber.BaseActivity;
import com.remind.me.fninaber.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static long timestampDay(String day, String time) {
        StringBuilder builder = new StringBuilder();
        builder.append(day);
        builder.append("/");
        builder.append(time);

        Log.e("f.ninaber", "day time : " + builder.toString());


        SimpleDateFormat sdf;
        if (time.contains(" AM") || time.contains(" PM") || !Constants.is24HoursFormat) {
            Log.e("f.ninaber", "Here AM/PM ");
            sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy/hh:mm a");
        } else {
            Log.e("f.ninaber", "Here 24 ");
            sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy/HH:mm");
        }

        try {
            Date date = sdf.parse(builder.toString());
            return date.getTime();
        } catch (ParseException e) {
            Log.e("f.ninaber", "Exception: " + DateUtil.class.getSimpleName() + " | " + e);
        }
        return 0;
    }

    public static long timestampDay(String day) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        try {
            Date date = sdf.parse(day);
            return date.getTime();
        } catch (ParseException e) {
            Log.e("f.ninaber", "Exception: " + DateUtil.class.getSimpleName() + " | " + e);
        }
        return 0;
    }

    public static String dateTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    public static String timeTimestamp(long timestamp) {
        String timeFormat = "HH:mm";
        if (!Constants.is24HoursFormat) {
            timeFormat = "hh:mm a";
        }


        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
        Date time = new Date(timestamp);
        return sdf.format(time);
    }

    public static String calendarDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        Calendar calendar = Calendar.getInstance();
        return sdf.format(calendar.getTime());
    }

    public static String calendarTime() {
        String timeFormat = "HH:mm";
        if (!Constants.is24HoursFormat) {
            timeFormat = "hh:mm a";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
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
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy");
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
        String[] values = time.split(":");
        if (!Constants.is24HoursFormat) {
            if (values.length == 2 && values[1] != null) {
                String am = values[1];
                if (am.contains(" AM")) {
                    values[1] = values[1].substring(0, values[1].lastIndexOf(" AM"));
                } else {
                    values[0] = String.valueOf(Integer.valueOf(values[0]) + 12);
                    values[1] = values[1].substring(0, values[1].lastIndexOf(" PM"));
                }
            }
            return values;
        }
        return values;
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

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date date = sdf.parse(builder.toString());
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");
            return dayFormat.format(date).toString();
        } catch (Exception e) {
            Log.e("f.ninaber", "Exception: " + DateUtil.class.getSimpleName() + " | " + e);
        }
        return null;
    }

    public static String calendarGiven12Time(int hour, int minute) {
        boolean isAM = true;
        int time = hour;
        if (hour >= 12) {
            time = hour - 12;
            isAM = false;
        }
        String sHour = String.valueOf(time);
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

        if (isAM) {
            builder.append(" AM");
        } else {
            builder.append(" PM");
        }


        return builder.toString();
    }

    public static String calendarGiven24Time(int hour, int minute) {
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


    public static String getDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy");
        return sdf.format(timestamp);
    }

    public static long getBeginningOfday() {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(System.currentTimeMillis());
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getWeekAhead(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        cal.add(Calendar.WEEK_OF_MONTH, 1);
        return cal.getTimeInMillis();
    }

    public static long getMonthAhead(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        cal.add(Calendar.MONTH, 1);
        return cal.getTimeInMillis();
    }

    public static long getYearAhead(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        cal.add(Calendar.YEAR, 1);
        return cal.getTimeInMillis();
    }

    public static long getDayAhead(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        cal.add(Calendar.DAY_OF_WEEK, 1);
        return cal.getTimeInMillis();
    }

    public static long getDayBefore(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        cal.add(Calendar.DAY_OF_WEEK, -1);
        return cal.getTimeInMillis();
    }

    public static long getMonthBefore(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        cal.add(Calendar.MONTH, -1);
        return cal.getTimeInMillis();
    }

    public static long getWeekBefore(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        return cal.getTimeInMillis();
    }
}
