package com.jacky.commondraw.visual;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.jacky.commondraw.model.InsertableObjectBase;
import com.jacky.commondraw.model.stroke.StylusPoint;
import com.jacky.commondraw.views.doodleview.IInternalDoodle;
import com.jacky.commondraw.views.doodleview.opereation.SRSegmentStrokeTouchOperation;
import com.jacky.commondraw.views.doodleview.opereation.SegmentStrokeTouchOperation;
import com.jacky.commondraw.visual.brush.HWPoint;
import com.jacky.commondraw.visual.brush.QuadBezierSpline;
import com.jacky.commondraw.visual.brush.VisualStrokeBase;
import com.jacky.commondraw.visual.brush.operation.StrokeTouchOperation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 * 分段绘制基础
 */
public class VisualStrokeSpot extends VisualStrokeBase {
    protected int BOUND_TOLERANCE = 30;
    protected int INVALIDATE_MARGIN = 15;
    protected float DIS_VEL_CAL_FACTOR = 0.02f;
    protected float WIDTH_THRES_MAX = 0.6f;
    protected int STEPFACTOR = 8;

    protected ArrayList<HWPoint> mPointList;
    protected ArrayList<HWPoint> mHWPointList;
    protected float mWidth = 2;
    protected int mColor = 0;
    protected QuadBezierSpline mBezier;
    protected HWPoint mLastPoint;
    protected Path mPath; // for bound calc
    protected double mBaseWidth;
    protected ArrayList<HWPoint> mOnTimeDrawList;
    protected double mLastVel;
    protected double mLastWidth;
    protected HWPoint curPoint;

    public VisualStrokeSpot(Context context, IInternalDoodle internalDoodle,
                            InsertableObjectBase object) {
        super(context, internalDoodle, object);
        // TODO Auto-generated constructor stub
        mPointList = new ArrayList<HWPoint>();
        mHWPointList = new ArrayList<HWPoint>();

        mBezier = new QuadBezierSpline();
        mBaseWidth = mPaint.getStrokeWidth();

        mPath = new Path();
        mOnTimeDrawList = new ArrayList<HWPoint>();

        mLastPoint = new HWPoint(0, 0);
    }

    @Override
    public void onDown(MotionElement mElement) {
        // TODO Auto-generated method stub
        mBaseWidth = mPaint.getStrokeWidth();
        mPath = new Path();
        mPointList.clear();
        mHWPointList.clear();

        HWPoint curPoint = new HWPoint(mElement.x, mElement.y,
                mElement.timestamp);

        if (mElement.tooltype == MotionEvent.TOOL_TYPE_STYLUS) {
            mLastWidth = mElement.pressure * mBaseWidth;
        } else {
            mLastWidth = 0.8 * mBaseWidth;
        }
        curPoint.width = (float) mLastWidth;
        mLastVel = 0;

        mPointList.add(curPoint);
        mLastPoint = curPoint;
        mPath.moveTo(mElement.x, mElement.y);

        mOnTimeDrawList.clear();
    }

    @Override
    public void onMove(MotionElement mElement) {
        HWPoint curPoint = new HWPoint(mElement.x, mElement.y,
                mElement.timestamp);

        // V->W
        double deltaX = curPoint.x - mLastPoint.x;
        double deltaY = curPoint.y - mLastPoint.y;
        double curDis = Math.hypot(deltaX, deltaY);
        double curVel = curDis * DIS_VEL_CAL_FACTOR;
        double curWidth;

        if (mPointList.size() < 2) {
            if (mElement.tooltype == MotionEvent.TOOL_TYPE_STYLUS) {
                curWidth = mElement.pressure * mBaseWidth;
            } else {
                curWidth = calcNewWidth(curVel, mLastVel, curDis, 1.5,
                        mLastWidth);
            }
            curPoint.width = (float) curWidth;
            mBezier.Init(mLastPoint, curPoint);
        } else {
            mLastVel = curVel;
            if (mElement.tooltype == MotionEvent.TOOL_TYPE_STYLUS) {
                curWidth = mElement.pressure * mBaseWidth;
            } else {
                curWidth = calcNewWidth(curVel, mLastVel, curDis, 1.5,
                        mLastWidth);
            }
            curPoint.width = (float) curWidth;
            mBezier.AddNode(curPoint);
        }
        mLastWidth = curWidth;

        mPointList.add(curPoint);

        mOnTimeDrawList.clear();
        int steps = 1 + (int) curDis / STEPFACTOR;
        double step = 1.0 / steps;
        for (double t = 0; t < 1.0; t += step) {
            HWPoint point = mBezier.GetPoint(t);
            mHWPointList.add(point);
            mOnTimeDrawList.add(point);
        }
        mOnTimeDrawList.add(mBezier.GetPoint(1.0));
        calcNewDirtyRect(mOnTimeDrawList.get(0),
                mOnTimeDrawList.get(mOnTimeDrawList.size() - 1));

        mPath.quadTo(mLastPoint.x, mLastPoint.y,
                (mElement.x + mLastPoint.x) / 2,
                (mElement.y + mLastPoint.y) / 2);

        mLastPoint = curPoint;
    }

    @Override
    public void onUp(MotionElement mElement) {
        // TODO Auto-generated method stub
        HWPoint curPoint = new HWPoint(mElement.x, mElement.y,
                mElement.timestamp);
        mOnTimeDrawList.clear();
        double deltaX = curPoint.x - mLastPoint.x;
        double deltaY = curPoint.y - mLastPoint.y;
        double curDis = Math.hypot(deltaX, deltaY);

        if (mElement.tooltype == MotionEvent.TOOL_TYPE_STYLUS) {
            curPoint.width = (float) (mElement.pressure * mBaseWidth);
        } else {
            curPoint.width = 0;
        }

        mPointList.add(curPoint);

        mBezier.AddNode(curPoint);

        int steps = 1 + (int) curDis / STEPFACTOR;
        double step = 1.0 / steps;
        for (double t = 0; t < 1.0; t += step) {
            HWPoint point = mBezier.GetPoint(t);
            mHWPointList.add(point);
            mOnTimeDrawList.add(point);
        }

        mBezier.End();
        for (double t = 0; t < 1.0; t += step) {
            HWPoint point = mBezier.GetPoint(t);
            mHWPointList.add(point);
            mOnTimeDrawList.add(point);
        }

        calcNewDirtyRect(mOnTimeDrawList.get(0),
                mOnTimeDrawList.get(mOnTimeDrawList.size() - 1));
        mPath.quadTo(mLastPoint.x, mLastPoint.y,
                (mElement.x + mLastPoint.x) / 2,
                (mElement.y + mLastPoint.y) / 2);
        mPath.lineTo(mElement.x, mElement.y);
    }

    protected void drawSegmentInternal(Canvas canvas,
                                       List<HWPoint> onTimeDrawList) {
        // TODO Auto-generated method stub
        mPaint.setStyle(Paint.Style.FILL);

        if (onTimeDrawList == null || onTimeDrawList.size() < 1)
            return;

        curPoint = onTimeDrawList.get(0);
        for (int i = 1; i < onTimeDrawList.size(); i++) {
            HWPoint point = onTimeDrawList.get(i);
            drawToPoint(canvas, point, mPaint);
            curPoint = point;
        }
    }

    /**
     * 分段绘制
     *
     * @param canvas
     * @param onTimeDrawList
     * @param hwPoints
     */
    public void drawSegment(Canvas canvas, List<HWPoint> onTimeDrawList,
                            List<HWPoint> hwPoints) {
        drawSegmentInternal(canvas, onTimeDrawList);
    }

    public void drawSegment(Canvas canvas) {
        drawSegmentInternal(canvas, mOnTimeDrawList);
    }

    @Override
    public void draw(Canvas canvas) {
        // TODO Auto-generated method stub
        mPaint.setStyle(Paint.Style.FILL);

        if (mHWPointList == null || mHWPointList.size() < 1)
            return;

        if (mHWPointList.size() < 2) {
            HWPoint point = mHWPointList.get(0);
            canvas.drawCircle(point.x, point.y, point.width, mPaint);
        } else {
            curPoint = mHWPointList.get(0);
            for (int i = 1; i < mHWPointList.size(); i++) {
                HWPoint point = mHWPointList.get(i);
                drawToPoint(canvas, point, mPaint);
                curPoint = point;
            }
        }
    }

    @Override
    public List<StylusPoint> getPoints() {
        // TODO Auto-generated method stub
        ArrayList<StylusPoint> points = new ArrayList<StylusPoint>();
        for (HWPoint point : mPointList) {
            points.add(point.ToStylusPoint());
        }
        return points;
    }

    @Override
    protected RectF getStrictBounds() {
        // TODO Auto-generated method stub
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
    public void initFloatPoints(float[] fpoints, boolean isWithPressure) {
        // TODO Auto-generated method stub
        float[] points;
        if (!isWithPressure) {
            int count = fpoints.length / 2;
            points = new float[count * 3];
            int j1 = 0;
            int i1 = 0;
            while (i1 < count * 2 - 1) {
                points[j1++] = fpoints[i1++];
                points[j1++] = fpoints[i1++];
                points[j1++] = 1.0f;
            }
        } else {
            points = fpoints;
        }

        float mX = 0;
        float mY = 0;
        int length = points.length - 2;
        int index = 0;
        HWPoint point;
        HWPoint pointLast;
        double curWidth = 0;
        double curDis = 0;

        // Start Point
        point = new HWPoint(points[index++], points[index++]);
        point.width = (float) (points[index++] * mPaint.getStrokeWidth());

        mPointList.add(point);
        mPath.moveTo(point.x, point.y);
        mX = point.x;
        mY = point.y;
        pointLast = point;

        // Other Points
        while (index < length) {
            point = new HWPoint(points[index++], points[index++]);
            curWidth = (points[index++] * mPaint.getStrokeWidth());

            mPath.quadTo(mX, mY, (point.x + mX) / 2, (point.y + mY) / 2);
            mX = point.x;
            mY = point.y;

            if (index == 6) {
                point.width = (float) curWidth;
                mBezier.Init(mPointList.get(0), point);
            } else {
                point.width = (float) curWidth;
                mBezier.AddNode(point);
            }

            mPointList.add(point);
            curDis = getDistance(pointLast, point);
            int steps = 1 + (int) curDis / STEPFACTOR;
            double step = 1.0 / steps;
            for (double t = 0; t < 1.0; t += step) {
                mHWPointList.add(mBezier.GetPoint(t));
            }
            pointLast = point;
        }

        // Last Point
        mPath.lineTo(mX, mY);
        mBezier.End();
        curDis = getDistance(pointLast, point);
        int steps = 1 + (int) curDis / STEPFACTOR;
        double step = 1.0 / steps;
        for (double t = 0; t < 1.0; t += step) {
            mHWPointList.add(mBezier.GetPoint(t));
        }
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub
        if (mInsertableObjectStroke.getPoints() == null
                || mInsertableObjectStroke.getPoints().size() <= 3) {
            return;
        }
        initPath();
    }

    private void initPath() {
        // float mX = 0;
        // float mY = 0;
        // StylusPoint point = mPointList.get(0);
        // // Start Point
        // mPath.moveTo(point.x, point.y);
        // mX = point.x;
        // mY = point.y;
        // // Other Points
        // for (int i = 1; i < mPointList.size() - 1; i++) {
        // point = mPointList.get(i);
        // mPath.quadTo(mX, mY, (point.x + mX) / 2, (point.y + mY) / 2);
        // mX = point.x;
        // mY = point.y;
        // }
        // // Last Point
        // mPath.lineTo(mX, mY);
        List<StylusPoint> list = mInsertableObjectStroke.getPoints();

        float mX = 0;
        float mY = 0;
        // int length = points.length - 2;
        int index = 0;
        HWPoint point;
        HWPoint pointLast;
        double curWidth = 0;
        double curDis = 0;

        // Start Point

        point = new HWPoint(list.get(0).x, list.get(0).y);
        point.width = (float) (list.get(0).pressure * mPaint.getStrokeWidth());

        mPointList.add(point);
        mPath.moveTo(point.x, point.y);
        mX = point.x;
        mY = point.y;
        pointLast = point;

        // Other Points
        for (int i = 1; i < list.size() - 1; i++) {
            point = new HWPoint(list.get(i).x, list.get(i).y);
            curWidth = (list.get(i).pressure * mPaint.getStrokeWidth());

            mPath.quadTo(mX, mY, (point.x + mX) / 2, (point.y + mY) / 2);
            mX = point.x;
            mY = point.y;

            if (index == 6) {
                point.width = (float) curWidth;
                mBezier.Init(mPointList.get(0), point);
            } else {
                point.width = (float) curWidth;
                mBezier.AddNode(point);
            }

            mPointList.add(point);
            curDis = getDistance(pointLast, point);
            int steps = 1 + (int) curDis / STEPFACTOR;
            double step = 1.0 / steps;
            for (double t = 0; t < 1.0; t += step) {
                mHWPointList.add(mBezier.GetPoint(t));
            }
            pointLast = point;
        }

        // Last Point
        mPath.lineTo(mX, mY);
        mBezier.End();
        curDis = getDistance(pointLast, point);
        int steps = 1 + (int) curDis / STEPFACTOR;
        double step = 1.0 / steps;
        for (double t = 0; t < 1.0; t += step) {
            mHWPointList.add(mBezier.GetPoint(t));
        }
    }

    protected double calcNewWidth(double curVel, double lastVel, double curDis,
                                  double factor, double lastWidth) {
        // A simple low pass filter to mitigate velocity aberrations.
        double calVel = curVel * 0.6 + lastVel * (1 - 0.6);
        double vfac = Math.log(factor * 2.0f) * (-calVel);
        double calWidth = mBaseWidth * Math.exp(vfac);

        double mMoveThres = curDis * 0.01f;
        if (mMoveThres > WIDTH_THRES_MAX) {
            mMoveThres = WIDTH_THRES_MAX;
        }

        if (Math.abs(calWidth - mBaseWidth) / mBaseWidth > mMoveThres) {
            if (calWidth > mBaseWidth) {
                calWidth = mBaseWidth * (1 + mMoveThres);
            } else {
                calWidth = mBaseWidth * (1 - mMoveThres);
            }
        } else if (Math.abs(calWidth - lastWidth) / lastWidth > mMoveThres) {
            if (calWidth > lastWidth) {
                calWidth = lastWidth * (1 + mMoveThres);
            } else {
                calWidth = lastWidth * (1 - mMoveThres);
            }
        }
        return calWidth;
    }

    protected void calcNewDirtyRect(HWPoint p0, HWPoint p1) {
        int margin = getMargin();
        mDirtyRect = new Rect();
        mDirtyRect.left = (p0.x < p1.x) ? (int) p0.x - margin : (int) p1.x
                - margin;
        mDirtyRect.right = (p0.x > p1.x) ? (int) p0.x + margin : (int) p1.x
                + margin;
        mDirtyRect.top = (p0.y < p1.y) ? (int) p0.y - margin : (int) p1.y
                - margin;
        mDirtyRect.bottom = (p0.y > p1.y) ? (int) p0.y + margin : (int) p1.y
                + margin;
    }

    protected int getMargin() {
        return INVALIDATE_MARGIN + (int) (mPaint.getStrokeWidth());
    }

    protected void drawToPoint(Canvas canvas, HWPoint point, Paint paint) {
        if ((curPoint.x == point.x) && (curPoint.y == point.y))
            return;
        drawLine(canvas, curPoint.x, curPoint.y, curPoint.width, point.x,
                point.y, point.width, paint);
    }

    protected void drawLine(Canvas canvas, double x0, double y0, double w0,
                            double x1, double y1, double w1, Paint paint) {
        double curDis = Math.hypot(x0 - x1, y0 - y1);
        int steps = 1 + (int) (curDis / 2.0);
        double deltaX = (x1 - x0) / steps;
        double deltaY = (y1 - y0) / steps;
        double deltaW = (w1 - w0) / steps;
        double x = x0;
        double y = y0;
        double w = w0;

        for (int i = 0; i < steps; i++) {
            canvas.drawCircle((float) x, (float) y, (float) w / 2.0f, paint);
            x += deltaX;
            y += deltaY;
            w += deltaW;
        }
    }

    protected double getDistance(HWPoint p0, HWPoint p1) {
        return Math.sqrt((p0.x - p1.x) * (p0.x - p1.x) + (p0.y - p1.y)
                * (p0.y - p1.y));
    }

    @Override
    public boolean isSegmentDraw() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    protected void sendTouchOperation(MotionEvent motionEvent) {
        // TODO Auto-generated method stub
        // 分段绘制的画笔比较特殊，需要将中间态传给操作
        List<HWPoint> onTimeDrawList = new ArrayList<HWPoint>(mOnTimeDrawList);
        List<HWPoint> hwPointList = new ArrayList<HWPoint>(mHWPointList);
        if (mInternalDoodle.isShapeRecognition()) {
            SRSegmentStrokeTouchOperation operation = new SRSegmentStrokeTouchOperation(
                    mInternalDoodle.getFrameCache(),
                    mInternalDoodle.getModelManager(),
                    mInternalDoodle.getVisualManager(),
                    mInsertableObjectStroke, onTimeDrawList, hwPointList,
                    mInternalDoodle);
            operation.setMotionEvent(motionEvent);
            sendOperation(operation);
        } else {
            StrokeTouchOperation operation = new SegmentStrokeTouchOperation(
                    mInternalDoodle.getFrameCache(),
                    mInternalDoodle.getModelManager(),
                    mInternalDoodle.getVisualManager(),
                    mInsertableObjectStroke, onTimeDrawList, hwPointList,
                    mInternalDoodle);
            operation.setMotionEvent(motionEvent);
            sendOperation(operation);
        }
    }

}

