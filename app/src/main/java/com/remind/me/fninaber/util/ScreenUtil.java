package com.remind.me.fninaber.util;

import android.app.Activity;
import android.view.Display;

import java.util.Random;

public class ScreenUtil {
    public static int width(Activity act) {
        Display display = act.getWindowManager().getDefaultDisplay();
        return display.getWidth();
    }

    public static int height(Activity act) {
        Display display = act.getWindowManager().getDefaultDisplay();
        return display.getHeight();
    }

    public static int generateRandomNumber() {
        return new Random().nextInt(4);
    }
}
