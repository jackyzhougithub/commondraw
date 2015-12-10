package com.jacky.commondraw.visual.brush;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.jacky.commondraw.model.InsertableObjectBase;
import com.jacky.commondraw.views.doodleview.IInternalDoodle;
import com.jacky.commondraw.visual.brush.operation.EraseTouchOperation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 * 橡皮擦
 */
public class VisualStrokeErase extends VisualStrokePath {

    public VisualStrokeErase(Context context, IInternalDoodle internalDoodle,
                             InsertableObjectBase object) {
        super(context, internalDoodle, object);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void updatePaint() {
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(mInsertableObjectStroke.getColor());
        mPaint.setStrokeWidth(mInsertableObjectStroke.getStrokeWidth());
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mPaint.setPathEffect(null);
        mPaint.setAlpha(0xFF);
    }

    /**
     * 是否跟另外一个RectF相交
     *
     * @param bounds
     * @return
     */
    public boolean intersects(RectF bounds) {
        if (bounds == null)
            return false;
        RectF testBounds = new RectF();

        mPath.computeBounds(testBounds, true);
        if (!RectF.intersects(bounds, testBounds)
                && !RectF.intersects(testBounds, bounds)
                && !bounds.contains(testBounds) && !testBounds.contains(bounds)) {
            return false;
        }
        return true;
    }

    /**
     * Earse是否与其他的笔画相交
     *
     * @return
     */
    public boolean intersects() {
        List<InsertableObjectBase> list = new ArrayList<InsertableObjectBase>(
                mInternalDoodle.getModelManager().getInsertableObjectList());
        for (InsertableObjectBase insertableObjectBase : list) {
            if (insertableObjectBase.canErased()) {
                RectF dst = InsertableObjectBase
                        .getTransformedRectF(insertableObjectBase);
                if (dst != null) {
                    if (intersects(dst)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        // event会被下一次事件重用，这里必须生成新的，否则会有问题
        MotionEvent event2 = MotionEvent.obtain(event);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                onDown(createMotionElement(event2));
                sendTouchOperation(event2);
                return true;
            case MotionEvent.ACTION_MOVE:
                onMove(createMotionElement(event2));
                sendTouchOperation(event2);
                return true;
            case MotionEvent.ACTION_UP:
                onUp(createMotionElement(event2));
                sendTouchOperation(event2);
                mInsertableObjectStroke.setPoints(getPoints());// up的时候，给模型层设置数据
                mInsertableObjectStroke.setInitRectF(getBounds());
                // if (intersects()) {
                // mInternalDoodle.getModelManager().addInsertableObject(
                // mInsertableObjectStroke);
                // }
                return true;
            default:
                break;
        }
        return super.onTouchEvent(event2);
    }

    protected void sendTouchOperation(MotionEvent motionEvent) {
        // TODO Auto-generated method stub
        EraseTouchOperation operation = new EraseTouchOperation(
                mInternalDoodle.getFrameCache(),
                mInternalDoodle.getModelManager(),
                mInternalDoodle.getVisualManager(), mInsertableObjectStroke);
        operation.setMotionEvent(motionEvent);
        sendOperation(operation);
    }

}

