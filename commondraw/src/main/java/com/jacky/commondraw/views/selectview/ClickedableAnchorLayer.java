package com.jacky.commondraw.views.selectview;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.jacky.commondraw.listeners.IClickedListener;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class ClickedableAnchorLayer extends AnchorLayer implements ITransformChanged{

    private Rect mSrcRect = null;
    private IDrawableSelf mDrawableSelf = null;
    protected ITransformChangedManager mTransformChangedManager = null;
    private IClickedListener mClickedListener = null;

    public ClickedableAnchorLayer(IAnchorable anchorable, Point anchor,Rect actionRect ,IDrawableSelf drawableSelf,ITransformChangedManager m) {
        super(anchorable, anchor);
        // TODO Auto-generated constructor stub
        mSrcRect = new Rect(actionRect);
        mDrawableSelf = drawableSelf;
        mTransformChangedManager = m;
        m.addTransformChanged(this);
    }
    public boolean isClickedable(){
        return true;
    }
    private int mFirstPointId = -1;
    @Override
    public boolean onEventAction(View parentView, MotionEvent event) {
        // TODO Auto-generated method stub
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mFirstPointId = event.getPointerId(event.getActionIndex());
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                if (mFirstPointId == event.getPointerId(event.getActionIndex())) {
                    mFirstPointId = -1;
                    if (isClickedable()&&mClickedListener != null&&isHitInRect(new Point((int)event.getX(), (int)event.getY()))) {
                        mClickedListener.onClicked();
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    public void setClickedListener(IClickedListener listener){
        mClickedListener = listener;
    }

    @Override
    public void onDrawSelf(Canvas canvas) {
        // TODO Auto-generated method stub
        canvas.save();
        Point p= getAnchorPoint();
        canvas.translate(p.x-mSrcRect.width()/2, p.y-mSrcRect.height()/2);
//		canvas.drawBitmap(mBitmap, p.x-mBitmap.getWidth()/2, p.y-mBitmap.getHeight()/2, mDrawPaint);
        if (mDrawableSelf != null) {
            mDrawableSelf.onDrawSelf(canvas);
        }
        canvas.restore();
    }

    @Override
    public boolean isHitInRect(Point p) {
        // TODO Auto-generated method stub
        return getCurrentRect().contains(p.x, p.y);
    }

    @Override
    public Rect getCurrentRect() {
        // TODO Auto-generated method stub
        Point pp= getAnchorPoint();
        Rect rect=new Rect( pp.x-mSrcRect.width()/2, pp.y-mSrcRect.height()/2,  pp.x+mSrcRect.width()/2, pp.y+mSrcRect.height()/2);
        return rect;
    }

    @Override
    public void onScaled(Matrix matrix) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTranslate(Matrix matrix) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRotate(Matrix matrix) {
        // TODO Auto-generated method stub

    }
    @Override
    public void onAction(SelectViewEnum.ActionType t, ILayer layer) {
        // TODO Auto-generated method stub

    }

}
