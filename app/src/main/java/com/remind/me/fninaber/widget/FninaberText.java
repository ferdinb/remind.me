package com.remind.me.fninaber.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.remind.me.fninaber.R;


public class FninaberText extends TextView {

    public FninaberText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        customizeSetup(context, attrs);
    }

    public FninaberText(Context context, AttributeSet attrs) {
        super(context, attrs);
        customizeSetup(context, attrs);
    }

    public FninaberText(Context context) {
        super(context);
    }


    public void customizeSetup(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FninaberText, 0, 0);
        try {
            switch (a.getInt(R.styleable.FninaberText_type, 0)) {
                case 1:
                    this.setTypeface(Typeface.createFromAsset(context.getAssets(), "perfectly.ttf"));
                    break;
                default:
                    this.setTypeface(Typeface.createFromAsset(context.getAssets(), "perfectly.ttf"));
                    break;
            }
        } finally {
            a.recycle();
        }

    }
}
