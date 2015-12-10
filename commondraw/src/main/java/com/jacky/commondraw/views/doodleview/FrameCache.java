package com.jacky.commondraw.views.doodleview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 * 通过一个Bitmap缓存一帧。不用的时候，需要调用recycle方法释放
 */
public class FrameCache {
    /**
     * 释放内存时候用
     */
    private static Bitmap sFreeBitmap = Bitmap.createBitmap(1, 1,
            Bitmap.Config.RGB_565);

    private Bitmap mBitmap;
    private Canvas mCanvas;

    public FrameCache(int cacheWidth, int cacheHeight) {
        mBitmap = Bitmap.createBitmap(cacheWidth, cacheHeight,
                Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    public void recycle() {
        if (mBitmap == null)
            return;
        if (mCanvas == null) {
            mBitmap.recycle();
        } else {
            mCanvas.setBitmap(sFreeBitmap);
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    public Canvas getCanvas() {
        return mCanvas;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    /**
     * 清空Bitmap上的绘图
     */
    public void clearBitmap() {
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        // if(mBitmap != null)
        // mBitmap.eraseColor(Color.TRANSPARENT);
    }

    /**
     * 绘制透明背景
     */
    public void drawTransparentBg() {
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }
}
