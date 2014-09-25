package com.f.ninaber.android.widget;

import com.f.ninaber.android.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CircleAudioView extends View{
	private Paint paint;

	public CircleAudioView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	}

	public CircleAudioView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	}

	public CircleAudioView(Context context) {
		super(context);		
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		paint.setColor(getResources().getColor(R.color.black));
		canvas.drawCircle(100, 100, 5f, paint);
	}
	
}
