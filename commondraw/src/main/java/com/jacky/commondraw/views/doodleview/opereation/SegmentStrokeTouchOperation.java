package com.jacky.commondraw.views.doodleview.opereation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;

import com.jacky.commondraw.manager.modelmager.IModelManager;
import com.jacky.commondraw.manager.virsualmanager.IVisualManager;
import com.jacky.commondraw.model.stroke.InsertableObjectStroke;
import com.jacky.commondraw.views.doodleview.FrameCache;
import com.jacky.commondraw.views.doodleview.IInternalDoodle;
import com.jacky.commondraw.views.doodleview.drawstrategy.DrawStrategy;
import com.jacky.commondraw.visual.VisualElementBase;
import com.jacky.commondraw.visual.VisualStrokeSpot;
import com.jacky.commondraw.visual.brush.HWPoint;
import com.jacky.commondraw.visual.brush.operation.StrokeTouchOperation;

import java.util.List;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class SegmentStrokeTouchOperation extends StrokeTouchOperation {
    public final static String TAG = "SegmentStrokeTouchOperation";

    private VisualStrokeSpot mVisualSpot;
    protected IInternalDoodle mInternalDoodle;

    protected List<HWPoint> mOnTimeDrawList;
    protected List<HWPoint> mHwPoints;

    public SegmentStrokeTouchOperation(FrameCache frameCache,
                                       IModelManager modelManager, IVisualManager visualManager,
                                       InsertableObjectStroke stroke, List<HWPoint> onTimeDrawList,
                                       List<HWPoint> hwPoints, IInternalDoodle internalDoodle) {
        super(frameCache, modelManager, visualManager, stroke);
        // TODO Auto-generated constructor stub
        VisualElementBase visualElement = mVisualManager
                .getVisualElement(mStroke);
        mVisualSpot = (VisualStrokeSpot) visualElement;
        mOnTimeDrawList = onTimeDrawList;
        mHwPoints = hwPoints;
        mInternalDoodle = internalDoodle;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        /**
         * down的时候，framecache->segmentFrameCache;
         */
        if (mMotionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
            drawBitmap(mInternalDoodle.getTempFrameCache().getCanvas(),
                    mInternalDoodle.getFrameCache().getBitmap());
        }

        DrawStrategy drawStrategy = createDrawStrategy(canvas, null);
        if (drawStrategy != null)
            drawStrategy.draw();

        /**
         * up的时候, segmentFrameCache->framecache;
         */
        if (mMotionEvent.getActionMasked() == MotionEvent.ACTION_UP) {
            drawBitmap(mInternalDoodle.getFrameCache().getCanvas(),
                    mInternalDoodle.getTempFrameCache().getBitmap());

            // mInternalDoodle.getSegmentFrameCache().clearBitmap();//
            // 清空绘制，留给下一次用

        }
    }

    protected void drawBitmap(Canvas canvas, Bitmap bitmap) {
        canvas.drawBitmap(bitmap, 0, 0, DrawStrategy.sBitmapPaint);
    }

    protected DrawStrategy createDrawStrategy(Canvas canvas,
                                              FrameCache frameCache) {
        DrawStrategy drawStrategy = null;
        drawStrategy = new SegmentDrawStrategy(canvas,
                mInternalDoodle.getFrameCache(), mVisualSpot, mOnTimeDrawList,
                mHwPoints, mInternalDoodle.getTempFrameCache());
        return drawStrategy;
    }

    @Override
    public Rect computerDirty() {
        // if (mVisualSpot == null)
        // return null;
        // Rect rect = null;
        // rect = mVisualSpot.getDirtyRect();
        //
        // return rect;
        // 如果返回getDirtyRect，绘制的时候，会出现不连续的情况
        return null;
    }

    /**
     * 分段画笔策略
     *
     * @author noah
     *
     */
    class SegmentDrawStrategy extends DrawStrategy {
        protected VisualStrokeSpot mVisualStrokeSpot;
        protected List<HWPoint> mOnTimeDrawList;
        protected List<HWPoint> mHwPoints;
        protected FrameCache mSegmentFrameCache;

        public SegmentDrawStrategy(Canvas canvas, FrameCache frameCache,
                                   VisualElementBase visualElement, List<HWPoint> onTimeDrawList,
                                   List<HWPoint> hwPoints, FrameCache segmentFrameCache) {
            super(canvas, frameCache, visualElement);
            // TODO Auto-generated constructor stub
            mVisualStrokeSpot = (VisualStrokeSpot) visualElement;
            mOnTimeDrawList = onTimeDrawList;
            mHwPoints = hwPoints;
            mSegmentFrameCache = segmentFrameCache;
        }

        private void updateSegmentCache() {
            Canvas canvas = mSegmentFrameCache.getCanvas();
            drawSegmentVisualElement(canvas, mVisualStrokeSpot);
        }

        private void drawSegmentVisualElement(Canvas canvas,
                                              VisualStrokeSpot visualStrokeSpot) {
            visualStrokeSpot.drawSegment(canvas, mOnTimeDrawList, mHwPoints);
        }

        private void drawSegmentCache() {
            if (mViewCanvas != null && mSegmentFrameCache != null
                    && mSegmentFrameCache.getBitmap() != null) {// 第一次绘制的时候，mFrameCache.getBitmap()有可能为null
                drawBitmap(mViewCanvas, mSegmentFrameCache.getBitmap());
            }
        }

        @Override
        public void draw() {
            // TODO Auto-generated method stub
            updateSegmentCache();
            drawSegmentCache();
        }

        @Override
        protected void updateCache() {
            // TODO Auto-generated method stub

        }
    }

    // /**
    // * 分段画笔策略:up之后采用
    // *
    // * @author noah
    // *
    // */
    // class SegmentUpDrawStrategy extends DrawStrategy {
    // protected FrameCache mSegmentFrameCache;
    //
    // public SegmentUpDrawStrategy(Canvas canvas, FrameCache frameCache,
    // VisualElementBase visualElement, FrameCache segmentFrameCache) {
    // super(canvas, frameCache, visualElement);
    // // TODO Auto-generated constructor stub
    // mSegmentFrameCache = segmentFrameCache;
    // }
    //
    // @Override
    // public void draw() {
    // // TODO Auto-generated method stub
    // updateCache();
    // drawCache();
    // }
    //
    // @Override
    // protected void updateCache() {
    // // TODO Auto-generated method stub
    // if (mViewCanvas != null && mSegmentFrameCache != null
    // && mSegmentFrameCache.getBitmap() != null) {//
    // 第一次绘制的时候，mFrameCache.getBitmap()有可能为null
    // drawBitmap(mFrameCache.getCanvas(),
    // mSegmentFrameCache.getBitmap());
    // }
    // }
    //
    // }

}