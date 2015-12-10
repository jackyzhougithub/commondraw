package com.jacky.commondraw.views.doodleview.drawstrategy;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.jacky.commondraw.views.doodleview.FrameCache;
import com.jacky.commondraw.visual.VisualElementBase;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 * 一个策略主要包括：绘制缓存,绘制visual element,以及更新缓存
 */
public abstract class DrawStrategy {
    public static Paint sBitmapPaint;
    static {
        sBitmapPaint = new Paint();
        sBitmapPaint.setDither(true);
        sBitmapPaint.setAntiAlias(true);
        sBitmapPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    }
    protected Canvas mViewCanvas;
    protected FrameCache mFrameCache;
    // protected DrawTool mDrawTool;
    protected VisualElementBase mVisualElement;

    // /**
    // * 绘制笔画的画笔
    // */
    // protected Paint mPaint;

    /**
     * 绘制的主过程。包括刷新view，以及更新缓存
     *
     * @param canvas
     *            :view所在的canvas
     * @param frameCache
     */
    // public DrawStrategy(Canvas canvas, FrameCache frameCache, DrawTool
    // drawInfo) {
    // mViewCanvas = canvas;
    // mFrameCache = frameCache;
    // mDrawTool = drawInfo;
    // // mPaint = paint;
    // }
    public DrawStrategy(Canvas canvas, FrameCache frameCache,
                        VisualElementBase visualElement) {
        mViewCanvas = canvas;
        mFrameCache = frameCache;
        mVisualElement = visualElement;
        // mPaint = paint;
    }

    /**
     * 绘制的主过程。包括刷新view，以及更新缓存。
     *
     * @param canvas
     *            :view所在的canvas
     * @param frameCache
     */
    public abstract void draw();

    /**
     * 绘制缓存
     */
    protected void drawCache() {
        if (mViewCanvas != null && mFrameCache != null
                && mFrameCache.getBitmap() != null) {// 第一次绘制的时候，mFrameCache.getBitmap()有可能为null
            drawBitmap(mViewCanvas, mFrameCache.getBitmap());
        }
    }

    /**
     * 绘制可视化元素
     */
    protected void drawVisualElement() {
        if (mVisualElement != null)
            drawWholeVisualElement(mViewCanvas, mVisualElement);
    }

    /**
     * 更新缓存
     */
    protected abstract void updateCache();

    protected void drawBitmap(Canvas canvas, Bitmap bitmap) {
        canvas.drawBitmap(bitmap, 0, 0, sBitmapPaint);
    }

    // /**
    // * 调用VisualElementBase.drawSegment进行绘制
    // *
    // * @param canvas
    // * @param visualElement
    // */
    // protected void drawSegmentVisualElement(Canvas canvas,
    // VisualElementBase visualElement) {
    // // drawTool.getDrawTool().draw(canvas, false, drawTool);
    // // drawTool.resetDirty();
    // visualElement.drawSegment(canvas);
    //
    // }

    /**
     * 调用visualElement.draw进行绘制
     *
     * @param canvas
     * @param visualElement
     */
    protected void drawWholeVisualElement(Canvas canvas,
                                          VisualElementBase visualElement) {
        // drawTool.getDrawTool().draw(canvas, false, drawTool);
        // drawTool.resetDirty();
        visualElement.draw(canvas);
    }
}
