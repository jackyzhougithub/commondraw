package com.jacky.commondraw.views.selectview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.jacky.commondraw.utils.MathUtils;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 * 双指缩放，单指拖动
 */
public class GraphicLayer implements ILayer, ITransformChanged, IAnchorable, IRotateCenter {

    private static String NONE_MODE = "NONE_MODE";
    private static String ONE_MODE = "ONE_MODE";
    private static String TWO_MODE = "TWO_MODE";

    private String mTouchMode = NONE_MODE;
    private int firstPointId = -1;
    private int secondPointId = -1;

    private Point mOldPoint1 = null;
    private Point mOldPoint0 = null;


    private ITransformChangedManager mTransformChangedManager = null;
    private Context mContext = null;
    private Matrix mMatrix = null;

    private Rect mSrcRect = null;
    private Rect mActionRect = null;

    private float mTwoPointDistance = 0.0F;
    private ViewConfiguration mViewConfiguration = null;
    private IDrawableSelf mDrawableSelf;

    public GraphicLayer(Context context,Matrix matrix,Rect actionRect,ITransformChangedManager m,IDrawableSelf drawable){
        mContext = context;
        mMatrix = matrix;
        mSrcRect = new Rect(actionRect);
        RectF temp= new RectF(actionRect);
        matrix.mapRect(temp);
        mActionRect = new Rect((int)temp.left, (int)temp.top, (int)temp.right, (int)temp.bottom);
        mTransformChangedManager = m;
        m.addTransformChanged(this);
        mDrawableSelf = drawable;
        mViewConfiguration = ViewConfiguration.get(mContext);
    }
    @Override
    public void onDrawSelf(Canvas canvas) {
        // TODO Auto-generated method stub
        int c = canvas.save();
        canvas.setMatrix(mMatrix);
        if (mDrawableSelf!=null) {
            mDrawableSelf.onDrawSelf(canvas);
        }
        canvas.restoreToCount(c);
    }

    @Override
    public boolean onEventAction(View parentView, MotionEvent event) {
        // TODO Auto-generated method stub
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            {
                mOldPoint0 = new Point((int)event.getX(), (int)event.getY());
                firstPointId = event.getPointerId(event.getActionIndex());
                mTouchMode = NONE_MODE;
            }
            break;
            case MotionEvent.ACTION_POINTER_DOWN:
            {
                if (mTouchMode == NONE_MODE&& firstPointId != -1) {
                    final int index = event.getActionIndex();
                    secondPointId = event.getPointerId(index);
                    mOldPoint1 = new Point((int)event.getX(index), (int)event.getY(index));
                    mTwoPointDistance = MathUtils.caculateTwoPointDistance(mOldPoint0, mOldPoint1);
                    if (mTwoPointDistance == 0) {
                        mTwoPointDistance = 1;
                    }
                    mTouchMode = TWO_MODE;
                    mTransformChangedManager.notifyAction(SelectViewEnum.ActionType.begin, this);
                }
            }
            break;
            case MotionEvent.ACTION_MOVE:
            {
                if (mTouchMode == NONE_MODE && firstPointId!=-1&&event.getPointerCount()==1) {
                    final int touchSlop = mViewConfiguration.getScaledTouchSlop();
                    final int index1 = event.findPointerIndex(firstPointId);
                    Point currentPoint=new Point((int)event.getX(index1), (int)event.getY(index1));
                    if ((MathUtils.caculateTwoPointDistance(mOldPoint0, currentPoint) > touchSlop)) {
                        mTouchMode = ONE_MODE;
                        mTransformChangedManager.notifyAction(SelectViewEnum.ActionType.begin, this);
                    }
                }
                if (mTouchMode == ONE_MODE) {
                    dispatchOneMode(event);
                }
                if (mTouchMode == TWO_MODE) {
                    dispatchTwoMode(event);
                }
            }
            break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            {
                final int currentpointId = event.getPointerId(event.getActionIndex());
                if (mTouchMode == ONE_MODE) {
                    dispatchOneMode(event);
                    if (firstPointId == currentpointId) {
                        mTouchMode = NONE_MODE;
                        firstPointId = -1;
                        mTransformChangedManager.notifyAction(SelectViewEnum.ActionType.end, this);
                    }
                }else if (mTouchMode == TWO_MODE) {
                    dispatchTwoMode(event);
                    if (currentpointId == firstPointId || currentpointId == secondPointId) {
                        mTouchMode = NONE_MODE;
                        firstPointId = -1;
                        secondPointId = -1;
                        mTransformChangedManager.notifyAction(SelectViewEnum.ActionType.end, this);
                    }
                }
            }
            break;
            default:
                break;
        }
        return true;
    }
    private void dispatchOneMode(MotionEvent event) {
        final int index1 = event.findPointerIndex(firstPointId);
        Point currentPoint=new Point((int)event.getX(index1), (int)event.getY(index1));
        translateChanged(mOldPoint0,currentPoint);
        mOldPoint0 = currentPoint;
    }
    private void dispatchTwoMode(MotionEvent event) {
        final int index1= event.findPointerIndex(firstPointId);
        final int index2= event.findPointerIndex(secondPointId);
        float distance = MathUtils.caculateTwoPointDistance(new PointF(event.getX(index2), event.getY(index2)),new PointF(event.getX(index1), event.getY(index1)));
        sizeChanged(distance/mTwoPointDistance);
        this.mTwoPointDistance = distance >= 1?distance:1;
    }

    @Override
    public PointF getRotateCenter() {
        // TODO Auto-generated method stub
        return new PointF(mActionRect.centerX(), mActionRect.centerY());
    }

    @Override
    public Point anchor(Point p) {
        // TODO Auto-generated method stub
        float[] points = new float[2];
        points[0]= p.x;
        points[1]= p.y;
        mMatrix.mapPoints(points);
        return new Point((int)points[0], (int)points[1]);
    }

    @Override
    public void onScaled(Matrix matrix) {
        // TODO Auto-generated method stub
        onTransformChanged(matrix, false);
    }

    @Override
    public void onTranslate(Matrix matrix) {
        // TODO Auto-generated method stub
        onTransformChanged(matrix, false);
    }

    @Override
    public void onRotate(Matrix matrix) {
        // TODO Auto-generated method stub
        onTransformChanged(matrix, false);
    }
    @Override
    public void onAction(SelectViewEnum.ActionType t, ILayer layer) {
        // TODO Auto-generated method stub

    }
    @Override
    public boolean isHitInRect(Point p) {
        // TODO Auto-generated method stub
        float ps[]=new float[]{p.x,p.y};
        Matrix matrix = new Matrix();
        mMatrix.invert(matrix);
        matrix.mapPoints(ps);
        if (mSrcRect.contains((int)ps[0], (int)ps[1])) {
            return true;
        }
//		if (mActionRect.contains(p.x, p.y)) {
//			return true;
//		}
        return false;
    }

    @Override
    public Rect getCurrentRect() {
        // TODO Auto-generated method stub
        return mActionRect;
    }
    public Rect getSrcRect(){
        return mSrcRect;
    }
    private void onTransformChanged(Matrix paramMatrix, boolean isUsePre)
    {
        if (isUsePre) {
            mMatrix.preConcat(paramMatrix);
        }else {
            mMatrix.postConcat(paramMatrix);
        }
        RectF localRectF = new RectF(this.mSrcRect);
        mMatrix.mapRect(localRectF);
        mActionRect = new Rect((int)localRectF.left, (int)localRectF.top, (int)localRectF.right, (int)localRectF.bottom);
    }
    private void translateChanged(Point p1, Point p2){
        Matrix tempMatrix = new Matrix();
        tempMatrix.setTranslate(p2.x-p1.x, p2.y-p1.y);
        mTransformChangedManager.notifyChanged(SelectViewEnum.Type.TRANSLATE, tempMatrix);
    }
    private void sizeChanged(float scale)
    {
        Matrix tempMatrix = new Matrix();
        tempMatrix.setScale(scale, scale, mActionRect.centerX(), mActionRect.centerY());
        mTransformChangedManager.notifyChanged(SelectViewEnum.Type.SCALE, tempMatrix);
    }

}
