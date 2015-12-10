package com.jacky.commondraw.wigets.drawpickers;

import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class PaintSelector {
    public static final int DEFAULT_PAINT_WIDTH = 12;
    private static final int DEFAULT_COLOR = Color.RED;
    private static final int PAINT_SHIFT_VALUE = 0;
    private static final int PENCIL_ALPHA = 80;
    private static final int NORMAL_ALPHA = 60;
    private static final float NORMAL_BLUR = 6f;
    private static final float PEN_BLUR = 1f;
    private static final float PENCIL_BLUR = 1f;
    private static final int MARKPEN_ALPHA = 0x5f;//add by wendy

    public static void initPaint(Paint paint, int color, float strokeWidth) {
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(strokeWidth);
        paint.setXfermode(null);
        paint.setPathEffect(null);
        paint.setAlpha(0xFF);
    }

    public static void reset(Paint paint) {//Richard modify private to public
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setMaskFilter(null);
        paint.setXfermode(null);
        paint.setPathEffect(null);
        paint.setAlpha(0xFF);
        //begin wendy
        paint.setStrokeJoin(Paint.Join.ROUND);

        //end wendy
    }

    public static void setBlur(Paint paint) {
        reset(paint);
        BlurMaskFilter blur = new BlurMaskFilter(NORMAL_BLUR, BlurMaskFilter.Blur.NORMAL);
        paint.setMaskFilter(blur);
        paint.setStrokeCap(Paint.Cap.BUTT);
    }

    // Because the method, Paint.setColor(), will also reset the alpha value, so we need keep
    // original alpha value
    public static void setColor(Paint paint, int color) {
        // BEGIN ryan_lin@asus.com
        if (paint == null) {
            return;
        }
        // END ryan_lin@asus.com
        int alpha;
        alpha = paint.getAlpha();
        paint.setColor(color);
        paint.setAlpha(alpha);
    }

    // BEGIN: Better
    public static void setAlpha(Paint paint, int alpha) {
        if (paint == null) {
            return;
        }

        paint.setAlpha(alpha);
    }
    // END: Better

    public static void setDotted(Paint paint, int color) {
        reset(paint);
        paint.setPathEffect(new DashPathEffect(new float[] { 30, 10 }, 10));
        paint.setColor(color);
    }

    public static void setEmboss(Paint paint) {
        float blurRadius = Math.max(1f, paint.getStrokeWidth() / 3f);
        EmbossMaskFilter emboss = new EmbossMaskFilter(new float[] { 0x3f800000, 0x3f800000, 0x3f800000 }, 0.3f, 6f, blurRadius);
        reset(paint);
        paint.setMaskFilter(emboss);
    }

    public static void setErase(Paint paint) {
        reset(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public static void setLight(Paint paint) {
        reset(paint);
        paint.setAlpha(NORMAL_ALPHA);
        paint.setStrokeCap(Paint.Cap.BUTT);
    }

    public static void setAirBrush(Paint paint) {
        reset(paint);
    }

    public static void setNormal(Paint paint) {
        reset(paint);
    }

    public static void setPaintWidth(Paint paint, float width) {
        // BEGIN ryan_lin@asus.com
        if (paint == null) {
            return;
        }
        // END ryan_lin@asus.com
        paint.setStrokeWidth(width + PAINT_SHIFT_VALUE);
    }

    public static void setPen(Paint paint) {
        reset(paint);
        BlurMaskFilter blur = new BlurMaskFilter(PEN_BLUR, BlurMaskFilter.Blur.NORMAL);
        paint.setMaskFilter(blur);
    }

    public static void setPencil(Paint paint) {
        reset(paint);
        BlurMaskFilter blur = new BlurMaskFilter(PENCIL_BLUR, BlurMaskFilter.Blur.NORMAL);
        paint.setMaskFilter(blur);
        paint.setAlpha(PENCIL_ALPHA);
    }
    //begin wendy
    public static void setMarker(Paint paint)
    {
        reset(paint);
//    	paint.setAlpha(MARKPEN_ALPHA);
        paint.setStrokeCap(Paint.Cap.SQUARE);
        //paint.setStrokeJoin(Paint.Join.BEVEL);
    }
    public static void setBrush(Paint paint)
    {
        reset(paint);
        //paint.setStrokeWidth(paint.getStrokeWidth()/2);
    }
    //end wendy

    private Paint mPaint;

    public PaintSelector() {
        mPaint = new Paint();
        initPaint(mPaint, DEFAULT_COLOR, DEFAULT_PAINT_WIDTH);
    }

    public PaintSelector(Paint paint) {
        mPaint = paint;
    }

    public int getColor() {
        return mPaint.getColor();
    }

    public Paint getPaint() {
        return mPaint;
    }

    public boolean isEarseMode() {
        return mPaint.getXfermode() != null;
    }

    public void setBlur() {
        setBlur(mPaint);
    }

    public void setColor(int color) {
        setColor(mPaint, color);
    }

    public void setEmboss() {
        setEmboss(mPaint);
    }

    public void setErase(float earseWidth) {
        reset(mPaint);
        setErase(mPaint);
        earseWidth += PAINT_SHIFT_VALUE;
        mPaint.setStrokeWidth(earseWidth);
    }

    public void setLight() {
        setLight(mPaint);
    }

    public void setNeon() {
        setAirBrush(mPaint);
    }

    public void setNormal(float paintWidth) {
        setNormal(mPaint);
        mPaint.setStrokeWidth(paintWidth + PAINT_SHIFT_VALUE);
    }

    public void setPaintWidth(float width) {
        setPaintWidth(mPaint, width);
    }

    public void setScribble() {
        setPen(mPaint);
    }

    public void setSketch() {
        setPencil(mPaint);
    }
}
