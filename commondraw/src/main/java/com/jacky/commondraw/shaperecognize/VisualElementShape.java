package com.jacky.commondraw.shaperecognize;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;

import com.jacky.commondraw.model.InsertableObjectBase;
import com.jacky.commondraw.model.stroke.InsertableObjectStroke;
import com.jacky.commondraw.model.stroke.StylusPoint;
import com.jacky.commondraw.views.doodleview.IInternalDoodle;
import com.jacky.commondraw.views.doodleview.opereation.RemovedOperation;
import com.jacky.commondraw.visual.VisualElementBase;
import com.visionobjects.myscript.shape.ShapeEllipticArcData;
import com.visionobjects.myscript.shape.ShapePointData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class VisualElementShape extends VisualElementBase {

    public static final String TAG = "VisualStrokeShape";
    protected static final int INVALIDATE_MARGIN = 15;
    protected static final int BOUND_TOLERANCE = 30;
    protected Path mPath = null;
    protected List<StylusPoint> mPointList = null;
    protected InsertableObjectShape mInsertableObjectShape;
    protected ShapeEllipticArcData mArcData = null;
    protected Paint mPaint;

    public VisualElementShape(Context context, IInternalDoodle internalDoodle,
                              InsertableObjectBase object) {
        super(context, internalDoodle, object);
        // TODO Auto-generated constructor stub
        mPath = new Path();
        mPointList = new ArrayList<StylusPoint>();
        if (mInsertableObject instanceof InsertableObjectShape) {
            mInsertableObjectShape = (InsertableObjectShape) object;
        }
        mPaint = new Paint();
        updatePaint();
    }

    @Override
    public void draw(Canvas canvas) {
        // TODO Auto-generated method stub
        if (canvas == null)
            return;
        canvas.drawPath(mPath, mPaint);
        mInsertableObjectShape.setInitRectF(getBounds());
    }

    @Override
    public RectF getBounds() {
        RectF bounds = addStrokeToBounds(getStrictBounds());
        return bounds;
    }

    protected RectF addStrokeToBounds(RectF bounds) {
        float strokeWidth = mPaint.getStrokeWidth();
        RectF result = new RectF(bounds);
        result.left -= strokeWidth;
        result.top -= strokeWidth;
        result.right += strokeWidth;
        result.bottom += strokeWidth;
        return result;
    }

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
    public AddedOperationForShape createdAddedOperation() {
        return new AddedOperationForShape(mInternalDoodle.getFrameCache(),
                mInternalDoodle.getModelManager(),
                mInternalDoodle.getVisualManager(), mInsertableObject);
    }

    protected void updatePaint() {
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(mInsertableObjectShape.getColor());
        mPaint.setStrokeWidth(mInsertableObjectShape.getStrokeWidth());
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setPathEffect(null);
        mPaint.setAlpha(0xFF);
    }

    @Override
    public void onPropertyValeChanged(InsertableObjectBase insertableObject,
                                      int propertyId, Object oldValue, Object newValue, boolean fromUndoRedo) {
        // TODO Auto-generated method stub
        super.onPropertyValeChanged(insertableObject, propertyId, oldValue,
                newValue, fromUndoRedo);
        switch (propertyId) {
            case InsertableObjectStroke.PROPERTY_ID_STROKE_COLOR:
                mPaint.setColor(mInsertableObjectShape.getColor());
                break;
            case InsertableObjectStroke.PROPERTY_ID_STROKE_WIDTH:
                mPaint.setStrokeWidth(mInsertableObjectShape.getStrokeWidth());
            default:
                break;
        }
    }

    public List<StylusPoint> getPoints() {
        return mPointList;
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub
        if (mInsertableObjectShape.getPoints() == null
                || mInsertableObjectShape.getPoints().size() <= 0) {
            if (mInsertableObjectShape.getArcData() == null)
                return;
        }
        initPath();
    }

    private float calculatorRealEllipseArg(float arg, float maxR, float minR) {
        double argRad = arg * Math.PI / 180;
        Double tangValue = Math.tan(argRad);
        if (tangValue == 0 || tangValue.isNaN()) {
            return arg;
        }
        double x = 1 / Math.sqrt((1 / (maxR * maxR) + tangValue * tangValue
                / (minR * minR)));
        if (x >= maxR) {
            x = maxR;
        }
        if (Math.cos(argRad) < 0) {
            x = -x;
        }
        double realArgRad = Math.acos(x / maxR);
        if (Math.sin(argRad) < 0) {
            realArgRad = -realArgRad;
        }
        double realArgDeg = Math.toDegrees(realArgRad);
        while (arg - realArgDeg > 90) {
            realArgDeg += 360;
        }
        while (arg - realArgDeg < -90) {
            realArgDeg -= 360;
        }
        return (float) realArgDeg;
    }

    private void initPath() {
        mPointList = mInsertableObjectShape.getPoints();
        mArcData = mInsertableObjectShape.getArcData();
        if (mArcData != null) {
            Log.i(TAG, "draw arc data!");
            StylusPoint[] mPoints = new StylusPoint[4];
            final ShapePointData center = mArcData.getCenter();
            short maxR = (short) mArcData.getMaxRadius();
            short minR = (short) mArcData.getMinRadius();

            float[] pts = new float[8];
            pts[0] = -maxR;
            pts[1] = minR;

            pts[2] = maxR;
            pts[3] = minR;

            pts[4] = maxR;
            pts[5] = -minR;

            pts[6] = -maxR;
            pts[7] = -minR;

            Matrix matrix = new Matrix();
            matrix.setRotate((float) Math.toDegrees(mArcData.getOrientation()),
                    0, 0);
            matrix.mapPoints(pts);

            pts[0] += (short) center.getX();
            pts[1] += (short) center.getY();

            pts[2] += (short) center.getX();
            pts[3] += (short) center.getY();

            pts[4] += (short) center.getX();
            pts[5] += (short) center.getY();

            pts[6] += (short) center.getX();
            pts[7] += (short) center.getY();

            for (int i = 0; i < 4; i++) {
                mPoints[i] = new StylusPoint(pts[2 * i], pts[(2 * i) + 1]);
            }

            float[] info = new float[7];
            info[0] = (short) center.getX();
            info[1] = (short) center.getY();
            info[2] = maxR;
            info[3] = minR;

            info[4] = (float) Math.toDegrees(mArcData.getStartAngle());
            info[5] = (float) Math.toDegrees(mArcData.getSweepAngle());
            info[6] = (float) Math.toDegrees(mArcData.getOrientation());

            MyPoint mp0 = new MyPoint(mPoints[0].x, mPoints[0].y);
            MyPoint mp1 = new MyPoint(mPoints[1].x, mPoints[1].y);
            MyPoint mp2 = new MyPoint(mPoints[2].x, mPoints[2].y);
            MyPoint mp3 = new MyPoint(mPoints[3].x, mPoints[3].y);

            double addRotate = 0;
            MyPoint vectorNow = mp1.getDistance(mp0);
            double cosAddRotate = vectorNow.getCosTwoPoint(new MyPoint(1, 0));
            addRotate = Math.acos(cosAddRotate);
            if (!vectorNow.getDirect(new MyPoint(1, 0))) {
                addRotate = 0 - addRotate;
            }
            addRotate = Math.toDegrees(addRotate);

            float arcInfo[] = info;
            float centerPointX = (mPoints[0].x + mPoints[1].x + mPoints[2].x + mPoints[3].x) / 4;
            float centerPointY = (mPoints[0].y + mPoints[1].y + mPoints[2].y + mPoints[3].y) / 4;

            float maxRadius = (float) (mp1.getDistance(mp0)).getAbs() / 2;
            float minRadius = (float) (mp3.getDistance(mp0)).getAbs() / 2;

            float beginArg = calculatorRealEllipseArg(arcInfo[4], maxRadius,
                    minRadius);
            float endArg = calculatorRealEllipseArg(arcInfo[4] + arcInfo[5],
                    maxRadius, minRadius);
            float sweepArg = endArg - beginArg;

            RectF bounds = new RectF();
            mPath.computeBounds(bounds, false);
            Matrix matrix1 = new Matrix();
            matrix1.postRotate((float) -addRotate, centerPointX, centerPointY);
            mPath.transform(matrix);
            RectF oval = new RectF(centerPointX - maxRadius, centerPointY
                    - minRadius, centerPointX + maxRadius, centerPointY
                    + minRadius);
            mPath.addArc(oval, beginArg, sweepArg);
            Matrix matrix2 = new Matrix();
            matrix2.postRotate((float) -addRotate, centerPointX, centerPointY);
            mPath.transform(matrix2);
            mPath.computeBounds(bounds, false);
        } else {
            if (mPointList.size() > 0 && mPointList != null) {
                Log.i(TAG, "draw line path!");
                StylusPoint pointStart = mPointList.get(0);
                StylusPoint pointEnd = mPointList.get(1);
                mPath.moveTo(pointStart.x, pointStart.y);
                mPath.lineTo(pointEnd.x, pointEnd.y);

            }
        }
    }

    @Override
    public RemovedOperation createdRemovedOperation() {
        // TODO Auto-generated method stub
        return null;
    }

}