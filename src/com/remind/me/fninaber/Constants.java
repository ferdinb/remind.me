package com.remind.me.fninaber;

public interface Constants {
	public static final String TID = "TID";
	public static final int ON_GOING = 0;
	public static final int EXPIRED = 1;
	public static final int DELETED = -1;

	public static final String TASK = "TASK";
	public static final int MENU_EDIT = 0;
	public static final int MENU_DELETE = 1;

	public static final String EMAIL = "ferdi.ninaber@gmail.com";
	public static final String SUBJECT = "remind.me - feedback";

	public static final String VIEW = "view_task";
	public static final String ALARM = "alarm";
	public static int SNOOZE_DURATION = -1;

	public static final int REPEAT_DAY = 0;
	public static final int REPEAT_WEEK = 1;
	public static final int REPEAT_MONTH = 2;
	public static final int REPEAT_YEAR = 3;

	public static final String IMAGE_FOLDER = "/remindme/image/";
	public static final String PREF_KEY_MASTER = "com.f.ninaber.android.remind.me";
	public static final String PIN_LOCK_SCREEN = "pin_lock_screen";
}
