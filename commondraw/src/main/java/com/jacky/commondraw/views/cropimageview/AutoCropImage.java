package com.jacky.commondraw.views.cropimageview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.RegionIterator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.jacky.commondraw.views.geometry.GeoVector2D;
import com.jacky.commondraw.views.selectview.ILayer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class AutoCropImage extends ICropImage {

    private Matrix mMatrix = null;
    private Rect mInitRect = null;
    private List<ILayer> mLayers = null;
    private LinkedList<ILayer> mRenderLayers = null;
    public AutoCropImage(Rect rect, IRefresh refresh) {
        super(rect, refresh);
        // TODO Auto-generated constructor stub
        mMatrix = new Matrix();
        int width = rect.width()/2==0?rect.width():rect.width()/2;
        int height = rect.height()/2==0?rect.height():rect.height()/2;
        mInitRect = new Rect(rect.left, rect.top, rect.left+width, rect.top+height);
        mMatrix.setTranslate((rect.width()-mInitRect.width())/2, (rect.height()-mInitRect.height())/2);// set the center
        initLayer();
    }
    private void initLayer(){
        mLayers = new ArrayList<ILayer>(5);
        mRenderLayers = new LinkedList<ILayer>();
        ILayer leftTop = new AnchorPoint(new Point(mInitRect.left, mInitRect.top), new Point(mInitRect.right, mInitRect.bottom));
        mLayers.add(leftTop);

        ILayer rightTop = new AnchorPoint(new Point(mInitRect.right, mInitRect.top), new Point(mInitRect.left, mInitRect.bottom));
        mLayers.add(rightTop);

        ILayer leftBottom = new AnchorPoint(new Point(mInitRect.left, mInitRect.bottom), new Point(mInitRect.right, mInitRect.top));
        mLayers.add(leftBottom);

        ILayer rightBottom = new AnchorPoint(new Point(mInitRect.right, mInitRect.bottom), new Point(mInitRect.left, mInitRect.top));
        mLayers.add(rightBottom);

        ILayer leftLine = new LineMoveLayer(ScaleMode.X, new LineSegment(new Point(mInitRect.left, mInitRect.top),new Point(mInitRect.left, mInitRect.bottom)),
                new LineSegment(new Point(mInitRect.left, mInitRect.top),new Point(mInitRect.right, mInitRect.top)));
        mLayers.add(leftLine);

        ILayer rightLine = new LineMoveLayer(ScaleMode.X, new LineSegment(new Point(mInitRect.right, mInitRect.top),new Point(mInitRect.right, mInitRect.bottom)),
                new LineSegment(new Point(mInitRect.right, mInitRect.top),new Point(mInitRect.left, mInitRect.top)));
        mLayers.add(rightLine);

        ILayer topLine = new LineMoveLayer(ScaleMode.Y, new LineSegment(new Point(mInitRect.left, mInitRect.top),new Point(mInitRect.right, mInitRect.top)),
                new LineSegment(new Point(mInitRect.left, mInitRect.top),new Point(mInitRect.left, mInitRect.bottom)));
        mLayers.add(topLine);

        ILayer bottomLine = new LineMoveLayer(ScaleMode.Y, new LineSegment(new Point(mInitRect.left, mInitRect.bottom),new Point(mInitRect.right, mInitRect.bottom)),
                new LineSegment(new Point(mInitRect.left, mInitRect.bottom),new Point(mInitRect.left, mInitRect.top)));
        mLayers.add(bottomLine);

        mLayers.add(new RectMoveLayer(new Rect(mInitRect)));
    }
    @Override
    public void onDrawSelf(Canvas canvas) {
        // TODO Auto-generated method stub
        for (ILayer layer : mLayers) {
            mRenderLayers.push(layer);
        }
        //reverse draw layer
        while(mRenderLayers.peek()!=null){
            mRenderLayers.pop().onDrawSelf(canvas);
        }
    }

    private ILayer mBingoLayer = null;
    @Override
    public boolean onEventAction(View parentView, MotionEvent event) {
        // TODO Auto-generated method stub
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mBingoLayer = null;
                Point point = new Point((int)event.getX(), (int)event.getY());
                for (ILayer layer : mLayers) {
                    if (layer.isHitInRect(point)) {
                        mBingoLayer=layer;
                        break;
                    }
                }
                if (mBingoLayer!=null) {
                    return mBingoLayer.onEventAction(parentView, event);
                }
                break;
            default:
                if (mBingoLayer!=null) {
                    return mBingoLayer.onEventAction(parentView, event);
                }
                break;
        }
        return true;
    }

    @Override
    public boolean isHitInRect(Point p) {
        // TODO Auto-generated method stub
        return mCropRect.contains(p.x, p.y);
    }

    @Override
    public Rect getCurrentRect() {
        // TODO Auto-generated method stub
        return mCropRect;
    }

    @Override
    public Rect clipImageRegion(Canvas canvas) {
        // TODO Auto-generated method stub
        RectF rectF = new RectF(mInitRect);
        mMatrix.mapRect(rectF);
        canvas.clipRect(rectF);
        return new Rect((int)(rectF.left), (int)(rectF.top), (int)(rectF.right), (int)(rectF.bottom));
    }
    private class AnchorPoint implements ILayer{
        private Point mAnchorPoint =null;
        private Point mCenterPoint = null;//for scale
        private static final float RADIUS = 13.0f;
        private Paint mPaint = null;
        public AnchorPoint(Point anchor,Point center){
            mAnchorPoint = anchor;
            mCenterPoint = center;
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(Color.BLUE);
            mPaint.setStyle(Paint.Style.FILL);
        }
        @Override
        public void onDrawSelf(Canvas canvas) {
            // TODO Auto-generated method stub
            int c = canvas.save(Canvas.MATRIX_SAVE_FLAG);
            float ps0[]=new float[]{mAnchorPoint.x,mAnchorPoint.y};
            mMatrix.mapPoints(ps0);
            canvas.drawCircle(ps0[0], ps0[1],RADIUS , mPaint);
            canvas.restoreToCount(c);
        }

        @Override
        public boolean onEventAction(View parentView, MotionEvent event) {
            // TODO Auto-generated method stub
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    Point dragPoint= new Point((int)(event.getX()), (int)(event.getY()));
                    if (mCropRect.contains(dragPoint.x, dragPoint.y)) {
                        scaleAction(dragPoint);
                    }
                    break;
                default:
                    break;
            }
            if (mRefresh!=null) {
                mRefresh.refresh();
            }
            return true;
        }

        @Override
        public boolean isHitInRect(Point p) {
            // TODO Auto-generated method stub
            return getCurrentRect().contains(p.x, p.y);
        }

        @Override
        public Rect getCurrentRect() {
            // TODO Auto-generated method stub
            float ps0[]=new float[]{mAnchorPoint.x,mAnchorPoint.y};
            mMatrix.mapPoints(ps0);
            return new Rect((int)(ps0[0]-RADIUS), (int)(ps0[1]-RADIUS), (int)(ps0[0]+RADIUS), (int)(ps0[1]+RADIUS));
        }

        private void scaleAction(Point dragPoint){
            float ps0[]=new float[]{mAnchorPoint.x,mAnchorPoint.y};
            mMatrix.mapPoints(ps0);
            Point head = new Point((int)ps0[0], (int)ps0[1]);
            ps0= new float[]{mInitRect.centerX(),mInitRect.centerY()};
            mMatrix.mapPoints(ps0);
            Point tail = new Point((int)ps0[0], (int)ps0[1]);
            GeoVector2D baseVector2d = new GeoVector2D(head, tail);
            GeoVector2D vector2d = new GeoVector2D(dragPoint, tail);
            GeoVector2D vectorParallel = vector2d.getParallelVectorBase(baseVector2d);
            Point parallelPoint = new Point((int)(tail.x+vectorParallel.getX()), (int)(tail.y+vectorParallel.getY()));
            GeoVector2D newVector2d = new GeoVector2D(parallelPoint, tail);
            double newLength = newVector2d.getVectorLength();
            double oldLength = baseVector2d.getVectorLength();
            float scale = (float)(1+(newLength-oldLength)*0.5/oldLength);//control the scale size
            if (baseVector2d.getVectorLength()<=(2*RADIUS)&&scale<1.0) {
                return;
            }
            ps0=new float[]{mCenterPoint.x,mCenterPoint.y};
            mMatrix.mapPoints(ps0);
            Matrix matrix = new Matrix();
            matrix.setScale(scale, scale, ps0[0], ps0[1]);
            mMatrix.postConcat(matrix);
//			mMatrix.postScale(scale, scale, ps0[0], ps0[1]);
        }
    }
    private class RectMoveLayer implements ILayer{
        private Rect mRenderRect = null;
        private Paint mDrawRectPaint = null;
        private Paint mDrawAllRectPaint = null;
        public RectMoveLayer(Rect rect){
            mRenderRect = new Rect(rect);
            mDrawRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mDrawRectPaint.setColor(Color.BLUE);
            mDrawRectPaint.setStrokeWidth(4.0f);
            mDrawRectPaint.setStyle(Paint.Style.STROKE);
            mDrawAllRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            int color = ((int)(0xff*0.8)&0xff)<<(8*3)|0xffffff;//alpha = 0.8*0xff ,rgb=white
            mDrawAllRectPaint.setColor(color);
        }
        @Override
        public void onDrawSelf(Canvas canvas) {
            // TODO Auto-generated method stub
            drawAllRect(canvas);
            drawRect(canvas);
        }
        private void drawAllRect(Canvas canvas){
            Region gloableRegion = new Region(mCropRect);
            gloableRegion.op(this.getCurrentRect(), Region.Op.DIFFERENCE);
            RegionIterator iterator = new RegionIterator(gloableRegion);
            Rect rect =new Rect();
            while (iterator.next(rect)) {
                canvas.drawRect(rect, mDrawAllRectPaint);
            }
        }
        private void drawRect(Canvas canvas){
            canvas.drawRect(this.getCurrentRect(), mDrawRectPaint);
        }
        private Point lastPoint = null;
        @Override
        public boolean onEventAction(View parentView, MotionEvent event) {
            // TODO Auto-generated method stub
            Point newPoint=new Point((int)event.getX(), (int)event.getY());
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    lastPoint =newPoint;
                    break;
                case MotionEvent.ACTION_MOVE:
                    onTranslate(newPoint);
                    lastPoint = newPoint;
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                default:
                    onTranslate(newPoint);
                    lastPoint=null;
                    break;
            }
            if (mRefresh!=null) {
                mRefresh.refresh();
            }
            return true;
        }
        private void onTranslate(Point newPoint){
            if (lastPoint!=null) {
                int dx=newPoint.x-lastPoint.x;
                int dy=newPoint.y-lastPoint.y;
                Matrix srcMatrix = new Matrix(mMatrix);
                srcMatrix.preTranslate(dx, dy);
                RectF rectF = new RectF(mRenderRect);
                srcMatrix.mapRect(rectF);
                if (new RectF(mCropRect).contains(rectF)) {
                    mMatrix.preTranslate(dx, dy);
                }
            }
        }
        @Override
        public boolean isHitInRect(Point p) {
            // TODO Auto-generated method stub
            return this.getCurrentRect().contains(p.x, p.y);
        }

        @Override
        public Rect getCurrentRect() {
            // TODO Auto-generated method stub
            RectF realRenderRectF = new RectF(mRenderRect);
            mMatrix.mapRect(realRenderRectF);
            return new Rect((int)(realRenderRectF.left), (int)(realRenderRectF.top), (int)(realRenderRectF.right), (int)(realRenderRectF.bottom));
        }

    }
    enum ScaleMode{X,Y}
    private class LineSegment{
        public Point p0;//head
        public Point p1;//tail
        public LineSegment(Point head,Point tail){
            p0=head;
            p1=tail;
        }
    }
    private class LineMoveLayer implements ILayer{
        private static final int LINETOUCHWIDTH = 13;
        private ScaleMode mScaleMode = ScaleMode.X;
        private LineSegment mDragSegment = null;//drag line
        private LineSegment mDirectSegment = null;// drag orientation
        private Rect mLineRect = null;
        private Matrix mTrackMatrix = null;

        public LineMoveLayer(ScaleMode mode, LineSegment segment,
                             LineSegment direction) {
            mScaleMode = mode;
            mDragSegment = segment;
            mDirectSegment = direction;
            int left = 0;
            int top = segment.p0.y;
            if (mode == ScaleMode.X) {
                left = segment.p0.x - LINETOUCHWIDTH / 2;
                top = segment.p0.y;
                mLineRect = new Rect(left, top, left + LINETOUCHWIDTH, top
                        + Math.abs(mDragSegment.p0.y - mDragSegment.p1.y));
            } else {
                left = segment.p0.x;
                top = segment.p0.y - LINETOUCHWIDTH / 2;
                mLineRect = new Rect(left, top, left
                        + Math.abs(mDragSegment.p0.x - mDragSegment.p1.x), top
                        + LINETOUCHWIDTH);
            }
            mTrackMatrix = new Matrix();
        }

        @Override
        public void onDrawSelf(Canvas canvas) {
            // TODO Auto-generated method stub
            //do nothing
        }
        private Point mDownPoint = null;
        @Override
        public boolean onEventAction(View parentView, MotionEvent event) {
            // TODO Auto-generated method stub
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mTrackMatrix = new Matrix();
                    mDownPoint = new Point((int)(event.getX()), (int)(event.getY()));
                    break;
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                default:
                    onScale(new Point((int)(event.getX()), (int)(event.getY())));
                    break;
            }
            if (mRefresh!=null) {
                mRefresh.refresh();
            }
            return true;
        }
        private void onScale(Point dragPoint){
            if (mDownPoint!=null) {
                //get the deta vector that move form down point to drag point
                float ps[]=new float[]{mDownPoint.x,mDownPoint.y};
                mTrackMatrix.mapPoints(ps);
                Point realDownPoint = new Point((int)ps[0], (int)ps[1]);
                GeoVector2D baseVector2d = getPerpendicularVector();
                GeoVector2D vector2d = new GeoVector2D(dragPoint, realDownPoint);
                GeoVector2D parallelVector2d = vector2d.getParallelVectorBase(baseVector2d);
                // get the scale
                double oldLength = baseVector2d.getVectorLength();
                float ps2[] = new float[]{mDirectSegment.p0.x,mDirectSegment.p0.y,mDirectSegment.p1.x,mDirectSegment.p1.y};
                mMatrix.mapPoints(ps2);
                GeoVector2D newLineVector2d = new GeoVector2D(new Point((int)(ps2[0]+parallelVector2d.getX()),(int)(ps2[1]+parallelVector2d.getY()) ), new Point((int)ps2[2], (int)ps2[3]));
                double newLength = newLineVector2d.getVectorLength();
                double scale = 1+(newLength-oldLength)*0.4/oldLength;
                Log.v("SCALE", scale + "");
                Matrix srcMatrix = new Matrix(mMatrix);
                Matrix midleMatrix = new Matrix();
                if (mScaleMode == ScaleMode.X) {
                    midleMatrix.setScale((float)scale, 1.0f, ps2[2], ps2[3]);
                }else {
                    midleMatrix.setScale(1.0f, (float)scale, ps2[2], ps2[3]);
                }
                RectF renderRectF = new RectF(mInitRect);
                srcMatrix.postConcat(midleMatrix);
                srcMatrix.mapRect(renderRectF);
                if (new RectF(mCropRect).contains(renderRectF)) {
                    boolean valiate=false;
                    if (scale<1) {
                        if (mScaleMode==ScaleMode.X) {
                            if (renderRectF.width()>=(2*LINETOUCHWIDTH)) {
                                valiate = true;
                            }
                        }else {
                            if (renderRectF.height()>=(2*LINETOUCHWIDTH)) {
                                valiate = true;
                            }
                        }
                    }else {
                        valiate = true;
                    }
                    if (valiate) {
                        mTrackMatrix.postConcat(midleMatrix);
                        mMatrix.postConcat(midleMatrix);
                    }
                }
            }
        }
        @Override
        public boolean isHitInRect(Point p) {
            // TODO Auto-generated method stub
            return this.getCurrentRect().contains(p.x, p.y);
        }

        @Override
        public Rect getCurrentRect() {
            // TODO Auto-generated method stub
            RectF rectF = new RectF(mLineRect);
            mMatrix.mapRect(rectF);
            return new Rect((int)(rectF.left+0.5), (int)(rectF.top+0.5), (int)(rectF.right+0.5), (int)(rectF.bottom+0.5));
        }
        /**
         * get the vector that is Perpendicular to the line
         * @return
         */
        private GeoVector2D getPerpendicularVector(){
            float ps[] = new float[]{mDirectSegment.p0.x,mDirectSegment.p0.y,mDirectSegment.p1.x,mDirectSegment.p1.y};
            mMatrix.mapPoints(ps);
            GeoVector2D vector2d = new GeoVector2D(new Point((int)ps[0], (int)ps[1]), new Point((int)ps[2], (int)ps[3]));
            return vector2d;
        }
    }
}
