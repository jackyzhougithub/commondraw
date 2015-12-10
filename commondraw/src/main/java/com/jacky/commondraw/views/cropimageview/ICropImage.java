package com.jacky.commondraw.views.cropimageview;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.jacky.commondraw.views.selectview.ILayer;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public abstract class ICropImage implements ILayer {
    public static interface IRefresh{
        void refresh();
    }
    public Rect mCropRect = null;
    protected IRefresh mRefresh = null;
    /**
     *
     * @param rect The content that will be cropped rect
     * @param refresh Notify outside to refresh screen
     */
    public ICropImage(Rect rect,IRefresh refresh) {
        if (rect != null) {
            mCropRect = new Rect(rect);
        } else {
            throw new NullPointerException("rect cannot null");
        }
        mRefresh = refresh;
    }
    /**
     *
     * @param canvas The dst canvas
     * @return the crop rect
     */
    public abstract Rect clipImageRegion(Canvas canvas);

}
