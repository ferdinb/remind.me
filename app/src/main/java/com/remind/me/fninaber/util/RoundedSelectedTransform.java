package com.remind.me.fninaber.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

public class RoundedSelectedTransform implements com.squareup.picasso.Transformation {
    private final int radius;

    public RoundedSelectedTransform(final int radius) {
        this.radius = radius;
    }

    @Override
    public Bitmap transform(final Bitmap source) {
        int width = source.getWidth();
        int height = source.getHeight();

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawRoundRect(new RectF(0, 0, width, height), radius, radius, paint);
        canvas.drawRect(new RectF(0, height - radius * 2, width, height), paint);

        if (source != output) {
            source.recycle();
        }

        return output;
    }

    @Override
    public String key() {
        return "rounded";
    }
}
