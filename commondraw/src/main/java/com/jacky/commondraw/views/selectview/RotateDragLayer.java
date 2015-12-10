package com.jacky.commondraw.views.selectview;

import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.jacky.commondraw.utils.MathUtils;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class RotateDragLayer extends ClickedableAnchorLayer {

    private IRotateCenter mCenter = null;
    public RotateDragLayer(IAnchorable anchorable, Point anchor,Rect actionRect, IDrawableSelf drawableSelf,
                           ITransformChangedManager m , IRotateCenter center) {
        super(anchorable, anchor,actionRect, drawableSelf, m);
        // TODO Auto-generated constructor stub
        mCenter = center;
    }
    @Override
    public boolean isClickedable() {
        // TODO Auto-generated method stub
        return false;
    }
    private PointF mOldPointF = null;
    private int mFirstPointerId = -1;
    @Override
    public boolean onEventAction(View parentView, MotionEvent event) {
        // TODO Auto-generated method stub
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mFirstPointerId = event.getPointerId(event.getActionIndex());
                mOldPointF = new PointF(event.getX(), event.getY());
                mTransformChangedManager.notifyAction(SelectViewEnum.ActionType.begin, this);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mFirstPointerId == -1 || (mFirstPointerId != event.getPointerId(event.getActionIndex()))) {
                    return true;
                }
                rotateEvent(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (mFirstPointerId != -1 &&(mFirstPointerId == event.getPointerId(event.getActionIndex()))) {
                    rotateEvent(event);
                    mFirstPointerId  = -1;
                    mTransformChangedManager.notifyAction(SelectViewEnum.ActionType.end, this);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (mFirstPointerId != -1 ) {
                    rotateEvent(event);
                    mFirstPointerId  = -1;
                    mTransformChangedManager.notifyAction(SelectViewEnum.ActionType.end, this);
                }
                break;
            default:
                break;
        }
        return true;
    }
    private void rotateEvent(MotionEvent event){
        PointF currentPointF = new PointF(event.getX(), event.getY());
        PointF centerPointF = mCenter.getRotateCenter();
        if (centerPointF == null) {
            centerPointF = new PointF(0.0f, 0.0f);
        }
        float cosq=
                ((currentPointF.x-centerPointF.x)*(mOldPointF.x-centerPointF.x)
                        +(currentPointF.y-centerPointF.y)*(mOldPointF.y-centerPointF.y))
                        /(MathUtils.caculateTwoPointDistance(currentPointF, centerPointF)
                        * MathUtils.caculateTwoPointDistance(mOldPointF, centerPointF));
        if (cosq <-1) {
            cosq = -1;
        }else if(cosq >1){
            cosq =1;
        }
        float degrees = (float) Math.toDegrees(Math.acos(cosq));
        float vx1=mOldPointF.x - centerPointF.x;
        float vy1=mOldPointF.y - centerPointF.y;
        float k1 = vx1 / vy1;
        float vx2 = currentPointF.x - centerPointF.x;
        float vy2 = currentPointF.y - centerPointF.y;
        float k2 = vx2 / vy2;
        float k45=(float) Math.tan(Math.toRadians(45));
        if (k1*k2<0&&(Math.abs(k2)>k45)) { // they are not in the same Phenomenon,it may happen shake when form -infinite to + infinite or reverse
            if (k1>0&&k2<0) {//we can try the k1 would be the same symbol with the k2
                k1=k2;
                k2+=0.1;
            }else { // k1<0 && k2>0
                k1=k2;
                k2-=0.1;
            }
        }
        if ((!Float.isInfinite(k2)&&!Float.isInfinite(k1))) {
            if (k1 < k2) {
                degrees =-degrees;
            }
        }

        Matrix tempM= new Matrix();
        tempM.setRotate(degrees, centerPointF.x, centerPointF.y);
        mTransformChangedManager.notifyChanged(SelectViewEnum.Type.ROTATE, tempM );
        mOldPointF = currentPointF;
    }

//    @Override
//    public void onAction(SelectViewEnum.ActionType t, ILayer layer) {
//
//    }
}
