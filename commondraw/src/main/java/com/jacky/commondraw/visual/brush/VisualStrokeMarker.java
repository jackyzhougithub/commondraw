package com.jacky.commondraw.visual.brush;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.jacky.commondraw.model.InsertableObjectBase;
import com.jacky.commondraw.views.doodleview.IInternalDoodle;
import com.jacky.commondraw.visual.VisualStrokeSpot;

import java.util.List;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 * 记号笔
 */
public class VisualStrokeMarker extends VisualStrokeSpot {
    private boolean isEnd = false;
    private boolean isFirstMove = false;

    public VisualStrokeMarker(Context context, IInternalDoodle internalDoodle,
                              InsertableObjectBase object) {
        super(context, internalDoodle, object);
        // TODO Auto-generated constructor stub
        mPaint.setAlpha(mInsertableObjectStroke.getAlpha());
    }

    protected void drawSegmentInternal(Canvas canvas,
                                       List<HWPoint> onTimeDrawList, List<HWPoint> hwPoints) {
        if (canvas == null)
            return;

        if (hwPoints == null || hwPoints.size() < 2)
            return;

        if (onTimeDrawList == null || onTimeDrawList.size() < 1)
            return;

        Paint paint = new Paint(mPaint);
        paint.setStyle(Paint.Style.FILL);

        if (isFirstMove) {
            drawStartPoints(hwPoints.get(0), hwPoints.get(1), canvas, paint);
            isFirstMove = false;
        }
        curPoint = onTimeDrawList.get(0);
        for (int i = 1; i < onTimeDrawList.size(); i++) {
            HWPoint point = onTimeDrawList.get(i);
            drawToPoint(canvas, point, paint);
            curPoint = point;
        }
        if (isEnd) {
            drawStartPoints(hwPoints.get(hwPoints.size() - 1),
                    hwPoints.get(hwPoints.size() - 2), canvas, paint);
            isEnd = false;
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
        drawSegmentInternal(canvas, onTimeDrawList, hwPoints);
    }

    @Override
    public void onDown(MotionElement mElement) {
        mBaseWidth = mPaint.getStrokeWidth();
        mPath = new Path();
        mPointList.clear();
        mHWPointList.clear();

        HWPoint curPoint = new HWPoint(mElement.x, mElement.y);

        mPointList.add(curPoint);
        mLastPoint = curPoint;
        mPath.moveTo(mElement.x, mElement.y);

        mOnTimeDrawList.clear();
    }

    @Override
    public void onMove(MotionElement mElement) {
        HWPoint curPoint = new HWPoint(mElement.x, mElement.y);

        double deltaX = curPoint.x - mLastPoint.x;
        double deltaY = curPoint.y - mLastPoint.y;
        double curDis = Math.hypot(deltaX, deltaY);

        if (mPointList.size() < 2) {
            mBezier.Init(mLastPoint, curPoint);
            isFirstMove = true;
        } else {
            mBezier.AddNode(curPoint);
        }

        mPointList.add(curPoint);

        mOnTimeDrawList.clear();
        int steps = 1 + (int) curDis / STEPFACTOR;
        double step = 1.0 / steps;
        for (double t = 0; t < 1.0; t += step) {
            HWPoint point = mBezier.GetPoint(t);
            mHWPointList.add(point);
            mOnTimeDrawList.add(point);
        }
        mHWPointList.add(mBezier.GetPoint(1.0));
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
        HWPoint curPoint = new HWPoint(mElement.x, mElement.y);
        mOnTimeDrawList.clear();
        double deltaX = curPoint.x - mLastPoint.x;
        double deltaY = curPoint.y - mLastPoint.y;
        double curDis = Math.hypot(deltaX, deltaY);

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
        isEnd = true;
    }

    @Override
    public void draw(Canvas canvas) {
        if (canvas == null)
            return;

        if (mHWPointList == null || mHWPointList.size() < 1)
            return;

        Paint paint = new Paint(mPaint);
        paint.setStyle(Paint.Style.FILL);

        if (mHWPointList.size() < 2) {
            HWPoint point = mHWPointList.get(0);
            canvas.drawCircle(point.x, point.y, point.width, paint);
        } else {
            drawStartPoints(mHWPointList.get(0), mHWPointList.get(1), canvas,
                    paint);
            curPoint = mHWPointList.get(0);
            for (int i = 1; i < mHWPointList.size(); i++) {
                HWPoint point = mHWPointList.get(i);
                drawToPoint(canvas, point, paint);
                curPoint = point;
            }
            drawStartPoints(mHWPointList.get(mHWPointList.size() - 1),
                    mHWPointList.get(mHWPointList.size() - 2), canvas, paint);
        }
    }

    private void drawStartPoints(HWPoint p0, HWPoint p1, Canvas canvas,
                                 Paint paint) {
        double d = 3;
        double k = 0;
        double x2, y2;
        HWPoint p2;

        if (p0.x != p1.x) {
            k = (p0.y - p1.y) / (p0.x - p1.x);
            x2 = p0.x - d / Math.sqrt(1 + k * k);
            y2 = p0.y - d * k / Math.sqrt(1 + k * k);
            p2 = new HWPoint((float) x2, (float) y2);
        } else {
            p2 = new HWPoint((float) p0.x, (float) (p0.y - d));
        }

        Paint pa = new Paint(paint);
        int alpha = pa.getAlpha() * 3;
        if (alpha > 255)
            alpha = 255;
        pa.setAlpha(alpha);
        drawPoints(canvas, p2, p0, pa);
    }

    @Override
    protected void drawLine(Canvas canvas, double x0, double y0, double w0,
                            double x1, double y1, double w1, Paint paint) {
        float curWidth = mPaint.getStrokeWidth();
        Path path = new Path();

        double tan = Math.tan(Math.toRadians(85));
        double temp = Math.sqrt(curWidth * curWidth / (4 * (1 + tan * tan)));

        path.moveTo((float) (x0 - temp), (float) (y0 - temp * tan));
        path.lineTo((float) (x1 - temp), (float) (y1 - temp * tan));
        path.lineTo((float) (x1 + temp), (float) (y1 + temp * tan));
        path.lineTo((float) (x0 + temp), (float) (y0 + temp * tan));
        path.lineTo((float) (x0 - temp), (float) (y0 - temp * tan));
        path.close();

        canvas.drawPath(path, paint);
    }

    private void drawPoints(Canvas canvas, HWPoint point1, HWPoint point2,
                            Paint paint) {
        drawLine(canvas, point1.x, point1.y, point1.width, point2.x, point2.y,
                point2.width, paint);
    }
}
