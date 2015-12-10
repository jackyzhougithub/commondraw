package com.jacky.commondraw.views.selectview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class LayerParent implements ITransformChangedManager,ILayer{

    private List<ILayer> mLayers = new LinkedList<ILayer>();
    private List<ITransformChanged> mITransformChangeds = new LinkedList<ITransformChanged>();
    private Rect mAllRect = null;
    private IOutsidePressed mOutsidePressed = null;
    private View mParentView =null;
    private Rect mVisualRect = null;//global visual x,y
    public interface IOutsidePressed{
        void onOutSidePressd();
    }
    public LayerParent(Context context,View parentView){
        mAllRect = new Rect();
        mParentView = parentView;
    }
    public void setVisualRect(Rect rect){
        mVisualRect = rect;
    }
    public void addLayer(ILayer layer){
        if (!mLayers.contains(layer)) {
            mLayers.add(layer);
            reCaculateRect();
        }
    }
    public void removeLayer(IDrawableSelf layer){
        if (mLayers.contains(layer)) {
            mLayers.remove(layer);
            reCaculateRect();
        }
    }
    /**
     * ===========Begin transform manager=========================
     */
    @Override
    public void addTransformChanged(ITransformChanged t) {
        // TODO Auto-generated method stub
        if (!mITransformChangeds.contains(t)) {
            mITransformChangeds.add(t);
        }
    }

    @Override
    public void removeTransformChanged(ITransformChanged t) {
        // TODO Auto-generated method stub
        if (mITransformChangeds.contains(t)) {
            mITransformChangeds.remove(t);
        }
    }

    private Matrix mMatrix =new Matrix();
    @Override
    public void notifyChanged(SelectViewEnum.Type t, Matrix matrix) {
        // TODO Auto-generated method stub

        switch (t) {
            case ROTATE:
                mMatrix.postConcat(matrix);
                for (ITransformChanged ifc : mITransformChangeds) {
                    ifc.onRotate(matrix);
                }
                break;
            case TRANSLATE:
                mMatrix.postConcat(matrix);
                for (ITransformChanged ifc : mITransformChangeds) {
                    ifc.onTranslate(matrix);
                }
                break;
            case SCALE:
                mMatrix.preConcat(matrix);
                for (ITransformChanged ifc : mITransformChangeds) {
                    ifc.onScaled(matrix);
                }
                break;
            default:
                break;
        }
        reCaculateRect();
        if (mParentView!=null) {
            mParentView.invalidate();
        }
    }
    @Override
    public void notifyAction(SelectViewEnum.ActionType t, ILayer layer) {
        // TODO Auto-generated method stub
        for (ITransformChanged ifc : mITransformChangeds) {
            ifc.onAction(t, layer);
        }
    }
    public void setSrcMatrix(Matrix m) {
        if (m!=null) {
            mMatrix = new Matrix(m);
        }
    }
    public Matrix getOutMatrix(){
        return new Matrix(mMatrix);
    }
    /**
     * =====================================================
     */
    private ITouchable mActionTarget=null;
    @Override
    public boolean onEventAction(View parentView, MotionEvent event) {
        // TODO Auto-generated method stub
        if (onInterceptEvent(event)) {
            if (mActionTarget!=null) {
//				mActionTarget.onEventAction(parentView, MotionEvent.)//send cancel
                MotionEvent e = cloneCancelEvent(event);
                mActionTarget.onEventAction(parentView, e);
                e.recycle();
                mActionTarget = null;
                return true;
            }
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mActionTarget = findHitIntLayer(event);
                if (mActionTarget != null) {
                    return mActionTarget.onEventAction(parentView, event);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mActionTarget!=null) {
                    final boolean ret=mActionTarget.onEventAction(parentView, event);
                    mActionTarget = null;
                    return ret;
                }
                break;
            default:
                if (mActionTarget!=null) {
                    return mActionTarget.onEventAction(parentView, event);
                }
                break;
        }
        return false;
    }
    @Override
    public void onDrawSelf(Canvas canvas) {
        // TODO Auto-generated method stub
        for (IDrawableSelf layer : mLayers) {
            layer.onDrawSelf(canvas);
        }
    }
    @Override
    public boolean isHitInRect(Point p) {
        // TODO Auto-generated method stub
        return mAllRect.contains(p.x, p.y);
    }
    public void setOutSideListener(IOutsidePressed out){
        mOutsidePressed = out;
    }
    private int mFirstPointId = -1;
    protected boolean onInterceptEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (!mAllRect.contains((int)event.getX(), (int)event.getY())) {// outside the rect
                    mFirstPointId = event.getPointerId(event.getActionIndex());
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (mFirstPointId != -1 && (mFirstPointId == event.getPointerId(event.getActionIndex()))) {
                    mFirstPointId = -1;
                    if (mOutsidePressed!=null) {
                        mOutsidePressed.onOutSidePressd();
                    }
                    return true;
                }
                break;
            default:
                if (mFirstPointId == -1&&mVisualRect!=null) {
                    if (!mVisualRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                        return true;
                    }
                }
                if (mFirstPointId != -1) {
                    return true;
                }
                break;
        }
        return false;
    }
    private ITouchable findHitIntLayer(MotionEvent event){
        for (ILayer layer : mLayers) {
            if (layer.isHitInRect(new Point((int)event.getX(), (int)event.getY()))) {
                return layer;
            }
        }
        return null;
    }
    private void reCaculateRect(){
        Rect current=new Rect();
        for (ILayer layer : mLayers) {
            current.union(layer.getCurrentRect());
        }
        mAllRect = new Rect(current);
    }

    private MotionEvent cloneCancelEvent(MotionEvent event){
        MotionEvent cloneEvent = MotionEvent.obtain(event);
        cloneEvent.setAction(MotionEvent.ACTION_CANCEL);
        return cloneEvent;
    }
    @Override
    public Rect getCurrentRect() {
        // TODO Auto-generated method stub
        return mAllRect;
    }
}

