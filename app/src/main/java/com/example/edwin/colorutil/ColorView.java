package com.example.edwin.colorutil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ColorView extends View {

    private static final Bitmap.Config confing565 = Bitmap.Config.RGB_565;
    private static final Bitmap.Config config8888 = Bitmap.Config.ARGB_8888;

    private Canvas canvas_565 = new Canvas();
    private Canvas canvas_8888 = new Canvas();

    private Bitmap bitmap_565;
    private Bitmap bitmap_8888;

    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int mCurrentColor;
    private int mViewWidth;
    private int mHeight  = 300;
    private int space = 30;

    public ColorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewWidth = getMeasuredWidth();
        textPaint.setTextSize(30);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        bitmap_565 = Bitmap.createBitmap(mViewWidth / 2, mHeight, confing565);
        canvas_565.setBitmap(bitmap_565);
       canvas_565.drawColor(0xFFFFFFFF);
        canvas_565.drawColor(mCurrentColor);

        bitmap_8888 = Bitmap.createBitmap(mViewWidth / 2, mHeight, config8888);
        canvas_8888.setBitmap(bitmap_8888);
       canvas_8888.drawColor(0xFFFFFFFF);
        canvas_8888.drawColor(mCurrentColor);


        canvas.drawText("565画布效果(APP的) :", 100, 30, textPaint);
        canvas.drawBitmap(bitmap_565,0,50,null);

        canvas.drawText("8888画布效果: ", mViewWidth / 2 + space, 30, textPaint);
        canvas.drawBitmap(bitmap_8888, mViewWidth / 2 + space, 50, null);
    }

    public void setColor(int color, float alpha) {
        mCurrentColor = Utils.rgb2Argb(color, alpha);
        invalidate();
    }
}
