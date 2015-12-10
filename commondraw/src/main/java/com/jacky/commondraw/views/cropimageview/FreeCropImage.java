package com.jacky.commondraw.views.cropimageview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.RegionIterator;
import android.view.MotionEvent;
import android.view.View;

import com.jacky.commondraw.R;
import com.jacky.commondraw.views.selectview.IDrawableSelf;


/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class FreeCropImage extends ICropImage{

    private Path mDrawPath = null;
    private IDrawableSelf mDrawable;
    private IDrawableSelf mPathDrawable;
    private IDrawableSelf mResultDrawable;
    private Context mContext = null;
    private int ACCEPT_COUNT = 5;
    public FreeCropImage(Rect rect, IRefresh refresh,Context context) {
        super(rect, refresh);
        // TODO Auto-generated constructor stub
        mContext = context;
        mPathDrawable = new DrawPath();
        mResultDrawable = new DrawResult();
        mDrawable = mResultDrawable;

    }

    public void setAcceptPointCount(int count) {
        ACCEPT_COUNT = count > 0 ? count : ACCEPT_COUNT;
    }
    @Override
    public void onDrawSelf(Canvas canvas) {
        // TODO Auto-generated method stub
        if (mDrawable!=null) {
            mDrawable.onDrawSelf(canvas);
        }
    }
    private boolean invalidate = false;
    private int count = 0;
    @Override
    public boolean onEventAction(View parentView, MotionEvent event) {
        // TODO Auto-generated method stub
        Point point = getPointInRect(event);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDrawPath = new Path();
                mDrawPath.moveTo(point.x, point.y);
                mDrawable=mPathDrawable;
                count = 0;
                invalidate = false;
                break;
            case MotionEvent.ACTION_MOVE:
                count++;
                if (count >= ACCEPT_COUNT) {
                    invalidate = true;
                }

                mDrawPath.lineTo(point.x, point.y);
                mDrawable=mPathDrawable;
                break;
            case MotionEvent.ACTION_UP:
                mDrawPath.lineTo(point.x, point.y);
                if (invalidate) {
                    mDrawable=mResultDrawable;
                }else {
                    mDrawPath = null;
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
        return mCropRect!=null&&mCropRect.contains(p.x,p.y);
    }

    @Override
    public Rect getCurrentRect() {
        // TODO Auto-generated method stub
        return mCropRect;

    }

    @Override
    public Rect clipImageRegion(Canvas canvas) {
        // TODO Auto-generated method stub
        Rect rect = null;
        if (mDrawPath!=null&&canvas!=null) {
            RectF rectF = new RectF();
            mDrawPath.computeBounds(rectF, false);
            rect = new Rect((int)rectF.left, (int)rectF.top, (int)rectF.right, (int)rectF.bottom);
          //  boolean result = rect.intersect(mCropRect);
            canvas.clipPath(mDrawPath);
        }
        return rect;
    }
    private Point getPointInRect(MotionEvent event){
        Point p=new Point((int)(event.getX()+0.5),(int)(event.getY()+0.5f));
        if (!mCropRect.contains(p.x, p.y)) {
            if (p.x<=mCropRect.left) {
                p.x=mCropRect.left+1;
            }else if(p.x>=mCropRect.right) {
                p.x=mCropRect.right-1;
            }
            if (p.y<=mCropRect.top) {
                p.y=mCropRect.top+1;
            }else if (p.y>=mCropRect.bottom) {
                p.y=mCropRect.bottom-1;
            }
        }
        return p;
    }
    private class DrawPath implements IDrawableSelf{
        private Paint mDrawPaint = null;
        public DrawPath(){
            final Resources resources = mContext.getResources();
            mDrawPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mDrawPaint.setStyle(Paint.Style.STROKE);
            mDrawPaint.setStrokeWidth(resources.getDimension(R.dimen.select_view_frame_line_width));
            mDrawPaint.setPathEffect(new DashPathEffect(new float[]{
                    resources.getDimension(R.dimen.select_view_frame_line_height),
                    resources.getDimension(R.dimen.select_view_frame_line_intervals)}, 0));
        }
        @Override
        public void onDrawSelf(Canvas canvas) {
            // TODO Auto-generated method stub
            if (mDrawPath!=null) {
                canvas.drawPath(mDrawPath, mDrawPaint);
            }
        }

    }
    private class DrawResult implements IDrawableSelf{
        private Paint mDrawPaint = null;
        public DrawResult(){
            mDrawPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            int color = ((int)(0xff*0.8)&0xff)<<(8*3)|0xffffff;//alpha = 0.8*0xff ,rgb=white
            mDrawPaint.setColor(color);
        }
        @Override
        public void onDrawSelf(Canvas canvas) {
            // TODO Auto-generated method stub
            if (mDrawPath!=null) {
                Region gloableRegion = new Region(mCropRect);
                RectF pathRectF = new RectF();
                mDrawPath.computeBounds(pathRectF, true);
                Region pathRegion = new Region();
                pathRegion.setPath(mDrawPath, new Region((int)pathRectF.left, (int)pathRectF.top, (int)pathRectF.right, (int)pathRectF.bottom));
                gloableRegion.op(pathRegion, Region.Op.DIFFERENCE);
                RegionIterator iterator = new RegionIterator(gloableRegion);
                Rect rect =new Rect();
                while (iterator.next(rect)) {
                    canvas.drawRect(rect, mDrawPaint);
                }
            }else {
                canvas.drawColor(mDrawPaint.getColor());
            }
        }

    }
}

