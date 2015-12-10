package com.jacky.commondraw.visual.brush;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

import com.jacky.commondraw.model.InsertableObjectBase;
import com.jacky.commondraw.model.stroke.StylusPoint;
import com.jacky.commondraw.views.doodleview.IInternalDoodle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 * 圆珠笔
 */
public class VisualStrokePath extends VisualStrokeBase {
    public static final String TAG = "VisualStrokePath";
    protected static final int INVALIDATE_MARGIN = 15;
    protected static final int BOUND_TOLERANCE = 30;
    protected Path mPath = null;
    protected List<StylusPoint> mPointList = null;
    protected float mControlX, mControlY;
    protected float mEndX, mEndY;

    public VisualStrokePath(Context context, IInternalDoodle internalDoodle,
                            InsertableObjectBase object) {
        super(context, internalDoodle, object);
        // TODO Auto-generated constructor stub
        mPath = new Path();
        mPointList = new ArrayList<StylusPoint>();
    }

    @Override
    public void draw(Canvas canvas) {
        // TODO Auto-generated method stub
        if (canvas == null)
            return;
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    protected RectF getStrictBounds() {
        RectF bounds = new RectF();
        mPath.computeBounds(bounds, false);
        if (bounds.height() < BOUND_TOLERANCE) {
            bounds.top = bounds.centerY() - BOUND_TOLERANCE;
            bounds.bottom = bounds.centerY() + BOUND_TOLERANCE;
        }
        if (bounds.width() < BOUND_TOLERANCE) {
            bounds.left = bounds.centerX() - BOUND_TOLERANCE;
            bounds.right = bounds.centerX() + BOUND_TOLERANCE;
        }
        return bounds;
    }

    @Override
    public void onDown(MotionElement mElement) {
        mPath = new Path();
        mPointList.clear();

        mEndX = mElement.x;
        mEndY = mElement.y;
        mControlX = mElement.x;
        mControlY = mElement.y;
        mPath.moveTo(mElement.x, mElement.y);
        mPointList.add(new StylusPoint(mElement.x, mElement.y));

        mDirtyRect = null;
    }

    @Override
    public void onMove(MotionElement mElement) {
        float tempCtrlX = mEndX;
        float tempCtrlY = mEndY;

        mEndX = mElement.x;
        mEndY = mElement.y;

        mPath.quadTo(tempCtrlX, tempCtrlY, (mElement.x + tempCtrlX) / 2,
                (mElement.y + tempCtrlY) / 2);
        mPointList.add(new StylusPoint(mElement.x, mElement.y));

        computeDirty(mElement.x, mElement.y);

        mControlX = tempCtrlX;
        mControlY = tempCtrlY;
    }

    @Override
    public void onUp(MotionElement mElement) {
        mEndX = mElement.x;
        mEndY = mElement.y;
        mPath.lineTo(mEndX, mEndY);
        mPointList.add(new StylusPoint(mEndX, mEndY));
    }

    @Override
    public void initFloatPoints(float[] fpoints, boolean isWithPressure) {

        float[] points;
        if (isWithPressure) {
            int count = fpoints.length / 3;
            points = new float[count * 2];
            int j1 = 0;
            int i1 = 0;
            while (i1 < count * 3 - 1) {
                points[j1++] = fpoints[i1++];
                points[j1++] = fpoints[i1++];
                i1++;
            }
        } else {
            points = fpoints;
        }
        int length = points.length;
        int index = 0;
        while (index < length) {
            StylusPoint stylusPoint = new StylusPoint(points[index++],
                    points[index++]);
            mPointList.add(stylusPoint);
        }
        initPath();
    }

    @Override
    public List<StylusPoint> getPoints() {
        return mPointList;
    }

    protected void computeDirty(float x, float y) {
        int invalidateMargin = getInvalidateMargin();
        int left = (int) ((x < mControlX) ? x : mControlX) - invalidateMargin;
        int right = (int) ((x < mControlX) ? mControlX : x) + invalidateMargin;
        int top = (int) ((y < mControlY) ? y : mControlY) - invalidateMargin;
        int bottom = (int) ((y < mControlY) ? mControlY : y) + invalidateMargin;
        if (mDirtyRect == null) {
            mDirtyRect = new Rect(left, top, right, bottom);
        } else {
            mDirtyRect.union(left, top, right, bottom);
        }
    }

    protected int getInvalidateMargin() {
        return INVALIDATE_MARGIN + (int) (mPaint.getStrokeWidth());
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub
        if (mInsertableObjectStroke.getPoints() == null
                || mInsertableObjectStroke.getPoints().size() <= 0) {
            return;
        }
        mPointList = mInsertableObjectStroke.getPoints();
        initPath();
    }

    private void initPath() {
        float mX = 0;
        float mY = 0;
        StylusPoint point = mPointList.get(0);
        // Start Point
        mPath.moveTo(point.x, point.y);
        mX = point.x;
        mY = point.y;
        // Other Points
        for (int i = 1; i < mPointList.size() - 1; i++) {
            point = mPointList.get(i);
            mPath.quadTo(mX, mY, (point.x + mX) / 2, (point.y + mY) / 2);
            mX = point.x;
            mY = point.y;
        }
        // Last Point
        mPath.lineTo(mX, mY);
    }
}
