package com.jacky.commondraw.visual.brush;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.jacky.commondraw.R;
import com.jacky.commondraw.model.InsertableObjectBase;
import com.jacky.commondraw.views.doodleview.IInternalDoodle;
import com.jacky.commondraw.visual.VisualStrokeSpot;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 * 毛笔
 */
public class VisualStrokeBrush extends VisualStrokeSpot {
    public static final boolean ISBRUSHWITHALPHA = true;
    protected Bitmap mTexture;
    protected Bitmap mTextureOrigin;
    protected Rect src = new Rect();
    protected RectF dst = new RectF();

    public VisualStrokeBrush(Context context, IInternalDoodle internalDoodle,
                             InsertableObjectBase object) {
        super(context, internalDoodle, object);
        // TODO Auto-generated constructor stub
        initTexture();
        setTexture(mTextureOrigin);
    }

    private void initTexture() {
        mTextureOrigin = BitmapFactory.decodeResource(
                mContext.getResources(), R.drawable.brush);
    }

    private void setTexture(Bitmap texture) {
        Canvas canvas = new Canvas();
        mTexture = Bitmap.createBitmap(texture.getWidth(), texture.getHeight(),
                Bitmap.Config.ARGB_8888);
        mTexture.eraseColor(Color.rgb(Color.red(mPaint.getColor()),
                Color.green(mPaint.getColor()), Color.blue(mPaint.getColor())));
        canvas.setBitmap(mTexture);

        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(texture, 0, 0, paint);

        src.set(0, 0, mTexture.getWidth(), mTexture.getHeight());
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
            if (ISBRUSHWITHALPHA) {
                point = dealWithPointAlpha(point);
            }
            mHWPointList.add(point);
            mOnTimeDrawList.add(point);
        }
        if (ISBRUSHWITHALPHA) {
            mOnTimeDrawList.add(dealWithPointAlpha(mBezier.GetPoint(1.0)));
        } else {
            mOnTimeDrawList.add(mBezier.GetPoint(1.0));
        }
        calcNewDirtyRect(mOnTimeDrawList.get(0),
                mOnTimeDrawList.get(mOnTimeDrawList.size() - 1));

        mPath.quadTo(mLastPoint.x, mLastPoint.y,
                (mElement.x + mLastPoint.x) / 2,
                (mElement.y + mLastPoint.y) / 2);

        mLastPoint = curPoint;
    }

    @Override
    public void onUp(MotionElement mElement) {
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
            if (ISBRUSHWITHALPHA) {
                point = dealWithPointAlpha(point);
            }
            mHWPointList.add(point);
            mOnTimeDrawList.add(point);
        }

        mBezier.End();
        for (double t = 0; t < 1.0; t += step) {
            HWPoint point = mBezier.GetPoint(t);
            if (ISBRUSHWITHALPHA) {
                point = dealWithPointAlpha(point);
            }
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

    private HWPoint dealWithPointAlpha(HWPoint point) {
        HWPoint nPoint = new HWPoint();
        nPoint.x = point.x;
        nPoint.y = point.y;
        nPoint.width = point.width;
        int alpha = (int) (255 * point.width / mBaseWidth / 2);
        if (alpha < 10) {
            alpha = 10;
        } else if (alpha > 255) {
            alpha = 255;
        }
        nPoint.alpha = alpha;
        return nPoint;
    }

    @Override
    protected void drawToPoint(Canvas canvas, HWPoint point, Paint paint) {
        // avoiding repaint
        if ((curPoint.x == point.x) && (curPoint.y == point.y))
            return;
        if (ISBRUSHWITHALPHA) {
            drawLine(canvas, curPoint.x, curPoint.y, curPoint.width,
                    curPoint.alpha, point.x, point.y, point.width, point.alpha,
                    paint);
        } else {
            drawLine(canvas, curPoint.x, curPoint.y, curPoint.width, point.x,
                    point.y, point.width, paint);
        }
    }

    @Override
    protected void drawLine(Canvas canvas, double x0, double y0, double w0,
                            double x1, double y1, double w1, Paint paint) {
        double curDis = Math.hypot(x0 - x1, y0 - y1);
        int factor = 2;
        if (paint.getStrokeWidth() < 6) {
            factor = 1;
        } else if (paint.getStrokeWidth() > 60) {
            factor = 3;
        }
        int steps = 1 + (int) (curDis / factor);
        double deltaX = (x1 - x0) / steps;
        double deltaY = (y1 - y0) / steps;
        double deltaW = (w1 - w0) / steps;
        double x = x0;
        double y = y0;
        double w = w0;

        for (int i = 0; i < steps; i++) {
            if (w < 1.5)
                w = 1.5;
            dst.set((float) (x - w / 2.0f), (float) (y - w / 2.0f),
                    (float) (x + w / 2.0f), (float) (y + w / 2.0f));
            canvas.drawBitmap(mTexture, src, dst, paint);
            x += deltaX;
            y += deltaY;
            w += deltaW;
        }
    }

    protected void drawLine(Canvas canvas, double x0, double y0, double w0,
                            int a0, double x1, double y1, double w1, int a1, Paint paint) {
        double curDis = Math.hypot(x0 - x1, y0 - y1);
        int factor = 2;
        if (paint.getStrokeWidth() < 6) {
            factor = 1;
        } else if (paint.getStrokeWidth() > 60) {
            factor = 3;
        }
        int steps = 1 + (int) (curDis / factor);
        double deltaX = (x1 - x0) / steps;
        double deltaY = (y1 - y0) / steps;
        double deltaW = (w1 - w0) / steps;
        double deltaA = (a1 - a0) / steps;
        double x = x0;
        double y = y0;
        double w = w0;
        double a = a0;

        for (int i = 0; i < steps; i++) {
            if (w < 1.5)
                w = 1.5;
            dst.set((float) (x - w / 2.0f), (float) (y - w / 2.0f),
                    (float) (x + w / 2.0f), (float) (y + w / 2.0f));
            paint.setAlpha((int) (a / 2.0f));
            canvas.drawBitmap(mTexture, src, dst, paint);
            x += deltaX;
            y += deltaY;
            w += deltaW;
            a += deltaA;
        }
    }
}

