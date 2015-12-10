package com.jacky.commondraw.views.selectview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class LayerView extends View {
    private ILayer mRenderLayer = null;
    public LayerView(Context context){
        super(context);
    }
    public LayerView(Context context, AttributeSet attrs){
        super(context, attrs);
    }
    public LayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
    }
    public void setRenderLayer(ILayer layer){
        mRenderLayer = layer;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (mRenderLayer!=null) {
            return mRenderLayer.onEventAction(this, event);
        }else {
            return super.onTouchEvent(event);
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        if (mRenderLayer!=null) {
            mRenderLayer.onDrawSelf(canvas);
        }
    }
}

