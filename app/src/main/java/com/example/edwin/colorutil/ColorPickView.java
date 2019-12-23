package com.example.edwin.colorutil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import static com.example.edwin.colorutil.Utils.getRotationBetweenLines;

public class ColorPickView extends View {

    private Paint colorWheelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint whiteWheelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint colorRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint markerPaint    = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Bitmap colorWheelBitmap = null;
    private Bitmap colorRingBitmap = null;
    private Bitmap colorRingBtnBitmap   = null;
//    private Bitmap markerBitmap    = null;

    private PointF markerPoint = new PointF();
    private Point colorRingBtnPoint = new Point();
    private PointF currentPoint = new PointF();

    private Rect  mColorWheelRect = new Rect();
    private Rect  mColorRingRect = new Rect();

    private Matrix colorRingMatrix = new Matrix();

    private int currentColor;
    private int colorWheelRadius = 300; // px
    private int whiteWheelRadius = 320; // px
    private int colorRingRadius = 420; // px
    private int ringWidth       = 20;  // px;
    private int markOpreateRadius = 50; // px
    private int markRingRadius = 30; // px

    private int centerWheelX = 0;
    private int centerWheelY = 0;

    private float mValue = 1; // hsv模型里的v

    private boolean hasInit  = false;

    private IColorPickerListener onColorPickerChanger;

    public ColorPickView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        init();

        //绘制白色圆圈
//        drawWhiteWheel(canvas);

        //绘制色盘
        canvas.drawBitmap(colorWheelBitmap, mColorWheelRect.left, mColorWheelRect.top, null);

        //绘制外圈彩色圆环
        canvas.drawBitmap(colorRingBitmap, mColorRingRect.left, mColorRingRect.top, null);

        //绘制点标记
        drawMarker(canvas);

        // 绘制圆点
        drawColorRingBtn(canvas);
    }

    private void init() {
        if (hasInit)
            return;

        colorWheelBitmap = createColorWheelBitmap(colorWheelRadius * 2, colorWheelRadius * 2);
        colorRingBitmap = createColorRingBitmap(colorRingRadius * 2 + ringWidth, colorRingRadius * 2 + ringWidth);

        centerWheelX = getMeasuredWidth() / 2;
        centerWheelY = 500;
        mColorWheelRect.set(centerWheelX - colorWheelRadius, centerWheelY - colorWheelRadius, centerWheelX + colorWheelRadius, centerWheelY + colorWheelRadius);

        int ringRadius = colorRingBitmap.getWidth() / 2;
        mColorRingRect.set(centerWheelX - ringRadius, centerWheelY - ringRadius, centerWheelX + ringRadius, centerWheelY + ringRadius);

        markerPoint.x = centerWheelX;
        markerPoint.y = centerWheelY;
        mValue = 1;
        currentColor = getColorAtPoint(markerPoint.x, markerPoint.y);

        colorRingBtnBitmap = createRingBtnBitmap(markRingRadius * 2, markRingRadius * 2);
        hasInit = true;
    }

    //创建色盘Bitmap
    private Bitmap createColorWheelBitmap(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int colorCount = 12;
        int colorAngleStep = 360 / colorCount;
        int colors[] = new int[colorCount + 1];
        float hsv[] = new float[]{0f, 1f, 1f};
        for (int i = 0; i < colors.length; i++) {
            hsv[0] = 360 - (i * colorAngleStep) % 360;
            colors[i] = Color.HSVToColor(hsv);
        }
        colors[colorCount] = colors[0];
        SweepGradient sweepGradient = new SweepGradient(width >> 1, height >> 1, colors, null);
        RadialGradient radialGradient = new RadialGradient(width >> 1, height >> 1, colorWheelRadius, 0xFFFFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP);
        ComposeShader composeShader = new ComposeShader(sweepGradient, radialGradient, PorterDuff.Mode.SRC_OVER);
        colorWheelPaint.setShader(composeShader);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawCircle(width >> 1, height >> 1, colorWheelRadius, colorWheelPaint);

        //默认取圆心颜色，给一个默认颜色用于显示点标记
//        currentColor = getColorAtPoint(markerPoint.x, markerPoint.y);
        return bitmap;
    }

    private void drawWhiteWheel(Canvas canvas) {
        //绘制白色圆圈
        whiteWheelPaint.setColor(Color.WHITE);
        canvas.drawCircle(centerWheelX, centerWheelY, whiteWheelRadius, whiteWheelPaint);
    }

    //创建彩色圆环bitmap
    private Bitmap createColorRingBitmap(int width, int height) {
        //设置画笔为描边
        colorRingPaint.setStyle(Paint.Style.STROKE);
        colorRingPaint.setStrokeWidth(ringWidth);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int colorCount = 12;
        int colorAngleStep = 360 / colorCount;
        int colors[] = new int[colorCount + 1];
        float hsv[] = new float[]{0f, 1f, 1f};
        for (int i = 0; i < colors.length; i++) {
            hsv[0] = 360 - (i * colorAngleStep) % 360;
            colors[i] = Color.HSVToColor(hsv);
        }
        colors[colorCount] = colors[0];
        SweepGradient sweepGradient = new SweepGradient(width >> 1, height >> 1, colors, null);
        RadialGradient radialGradient = new RadialGradient(width >> 1, height >> 1, colorRingRadius, 0xFFFFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP);
        ComposeShader composeShader = new ComposeShader(sweepGradient, radialGradient, PorterDuff.Mode.SRC_OVER);
        colorRingPaint.setShader(composeShader);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawCircle(width >> 1, height >> 1, colorRingRadius, colorRingPaint);
        return bitmap;
    }

    private void drawMarker(Canvas canvas) {
        markerPaint.setColor(0xFFFFFFFF);
        canvas.drawCircle(markerPoint.x, markerPoint.y - markOpreateRadius, markOpreateRadius, markerPaint);//绘制点标记中的变色小圆
        markerPaint.setColor(currentColor);//设置颜色为当前颜色
        canvas.drawCircle(markerPoint.x, markerPoint.y - markOpreateRadius, markOpreateRadius - 5, markerPaint);//绘制点标记中的变色小圆
    }

    private void drawColorRingBtn(Canvas canvas) {
        int colorRingBtnWidth = colorRingBtnBitmap.getWidth();
        int colorRingBtnHeight = colorRingBtnBitmap.getHeight();
        int left = centerWheelX - colorRingBtnWidth;
        int top = centerWheelY - colorRingRadius - colorRingBtnHeight;
        // colorRingBtnRect = new RectF(left, top, left + colorRingBtnWidth, top + colorRingBtnHeight);
        colorRingBtnPoint.x = left + colorRingBtnWidth / 2;
        colorRingBtnPoint.y = top + colorRingBtnHeight / 2;
        colorRingMatrix.preTranslate(colorRingBtnPoint.x, colorRingBtnPoint.y);
        // canvas.drawBitmap(colorRingBtnBitmap, null, colorRingBtnRect, null);
        canvas.drawBitmap(colorRingBtnBitmap, colorRingMatrix, null);
        colorRingMatrix.reset();
    }

    private Bitmap createRingBtnBitmap(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0xFFFFFFFF);
        canvas.drawCircle(markRingRadius, markRingRadius, markRingRadius, paint);
        return bitmap;
    }

    private static int colorTmp;///用于判断颜色是否发生改变
    private PointF downPointF = new PointF();//按下的位置

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                colorTmp = currentColor;
                downPointF.x = event.getX();
                downPointF.y = event.getY();
            case MotionEvent.ACTION_MOVE:
                update(event);
                return true;
            case MotionEvent.ACTION_UP:
                if (colorTmp != currentColor) {
                    onColorPickerChanger();
                }
                break;
            default:
                return true;
        }
        return super.onTouchEvent(event);
    }
    private void update(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        updateSelector(x, y);
        updateRingSelector(x, y);
    }

    public void setOnColorPickerChanger(IColorPickerListener onColorPickerChanger) {
        this.onColorPickerChanger = onColorPickerChanger;
    }

    private void onColorPickerChanger() {
        if (onColorPickerChanger != null) {
            int[] rgb = Utils.color2rgb(currentColor);
            onColorPickerChanger.onColorPickerChanger(currentColor, rgb[0], rgb[1], rgb[2]);
        }

    }

    /**
     * 刷新s色盘所选择的颜色
     * @param eventX
     * @param eventY
     */
    private void updateSelector(float eventX, float eventY) {
        float x = eventX - centerWheelX;
        float y = eventY - centerWheelY;
        double r = Math.sqrt(x * x + y * y);
        //判断是否在圆内
        if (r > colorWheelRadius) {
            //不在圆形范围内
            return;
        }
        //同时旋转外圈滑动按钮
        colorRingMatrix.preRotate(getRotationBetweenLines(centerWheelX, centerWheelY, eventX, eventY), centerWheelX, centerWheelY);
        currentPoint.x = x + centerWheelX;
        currentPoint.y = y + centerWheelY;
        markerPoint.x = currentPoint.x;//改变点标记位置
        markerPoint.y = currentPoint.y;
        currentColor = getColorAtPoint(eventX, eventY);//获取到的颜色
        invalidate();
    }


    /**
     * 刷新色环选择
     *
     * @param eventX
     * @param eventY
     */
    private void updateRingSelector(float eventX, float eventY) {
        float x = downPointF.x - centerWheelX;
        float y = downPointF.y - centerWheelY;
        double r = Math.sqrt(x * x + y * y);//按下位置的半径
        //判断是否在圆内,或者色环上
        if ((r < colorRingRadius + ringWidth && r > colorRingRadius - ringWidth)) {
            colorRingMatrix.preRotate(getRotationBetweenLines(centerWheelX, centerWheelY, eventX, eventY), centerWheelX, centerWheelY);
            currentColor = getColorAtPoint(eventX, eventY);//int值颜色
            float[] hsv = getHSVColorAtPoint(eventX, eventY);//hsv值颜色
            float h = hsv[0];//hsv色盘色点角度
            float s = hsv[1];//hsv色盘色点相对于半径的比值
            float colorDotRadius = colorWheelRadius * s;//色点半径
            //根据角度和半径获取坐标
            float radian = (float) Math.toRadians(-h);
            float colorDotX = (float) (centerWheelX + Math.cos(radian) * colorDotRadius);
            float colorDotY = (float) (centerWheelY + Math.sin(radian) * colorDotRadius);
            markerPoint.x = colorDotX;
            markerPoint.y = colorDotY;
            invalidate();
        }
    }


    /**
     * 根据坐标获取HSV颜色值
     * @param eventX
     * @param eventY
     * @return
     */
    private float[] getHSVColorAtPoint(float eventX, float eventY) {
        float x = eventX - centerWheelX;
        float y = eventY - centerWheelY;
        double r = Math.sqrt(x * x + y * y);
        float[] hsv = {0, 0, 1};
        hsv[0] = (float) (Math.atan2(y, -x) / Math.PI * 180f) + 180;
        hsv[1] = Math.max(0f, Math.min(1f, (float) (r / colorWheelRadius)));
        return hsv;

    }

    public void setColor(int color, float v) {
        float[] hsv = {0, 0, 1};
        mValue = v;
        Color.colorToHSV(color, hsv);
        //根据hsv角度及半径获取坐标
        //根据角度和半径获取坐标
        float radian = (float) Math.toRadians(-hsv[0]);
        float colorDotRadius = hsv[1] * colorWheelRadius;
        float colorDotX = (float) (centerWheelX + Math.cos(radian) * colorDotRadius);
        float colorDotY = (float) (centerWheelY + Math.sin(radian) * colorDotRadius);
        //设置marker位置
        markerPoint.x = colorDotX;
        markerPoint.y = colorDotY;
        currentColor = getColorAtPoint(markerPoint.x, markerPoint.y);//设置当前颜色
        //设置色环按钮位置
        colorRingMatrix.preRotate(getRotationBetweenLines(centerWheelX, centerWheelY, markerPoint.x, markerPoint.y), centerWheelX, centerWheelY);
        invalidate();
        // paint.setColor(Color.rgb(red, green, blue));
    }

    /**
     *  根据坐标获取颜色
     * @param eventX
     * @param eventY
     * @return
     */
    private int getColorAtPoint(float eventX, float eventY) {
        float x = eventX - centerWheelX;
        float y = eventY - centerWheelY;
        double r = Math.sqrt(x * x + y * y);
        float[] hsv = {0, 0, 1};
        hsv[0] = (float) (Math.atan2(y, -x) / Math.PI * 180f) + 180;
        hsv[1] = Math.max(0f, Math.min(1f, (float) (r / colorWheelRadius)));
        hsv[2] = mValue;
        return Color.HSVToColor(hsv);
    }
}
