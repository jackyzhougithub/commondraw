package com.jacky.commondraw.views.selectview;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.jacky.commondraw.listeners.IClickedListener;
import com.jacky.commondraw.listeners.IObjectUnSelectedListener;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public abstract class SelectViewBase<T> implements ISelectView<T> {
    protected Context mContext = null;
    protected View mSrcView = null;
    protected T mData = null;
    private WindowManager mWindowManager = null;
    protected IClickedListener mDeleteListener = null;
    protected IObjectUnSelectedListener< T> mUnSelectedListener = null;
    protected ITransformChanged mTransformChanged =null;
    protected Rect mVisualRect = null;
    public  SelectViewBase(Context context,T object,View parentView) {
        mContext = context;
        mSrcView = parentView ;
        mData = object;
        mWindowManager = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        mVisualRect = new Rect();
        parentView.getLocalVisibleRect(mVisualRect);
        int[]ps=new int[2];
        mSrcView.getLocationOnScreen(ps);
        mVisualRect=new Rect(ps[0], ps[1], ps[0]+mVisualRect.width(), ps[1]+mVisualRect.height());
    }
    @Override
    public void setUnSelectedListener(IObjectUnSelectedListener<T> listener) {
        // TODO Auto-generated method stub
        mUnSelectedListener = listener;
    }

    @Override
    public void setOnDeleteListener(IClickedListener listener) {
        // TODO Auto-generated method stub
        mDeleteListener = listener;
    }

    @Override
    public void setTransformChangedListener(ITransformChanged changed) {
        // TODO Auto-generated method stub
        mTransformChanged = changed;
    }

    @Override
    public void showSelectView() {
        // TODO Auto-generated method stub
        if (isSelectViewShowing()) {
            return ;
        }
        dismissSelectView();
        ensureInitUi();
        if (mShowView != null&&mLayoutParams != null) {
            mWindowManager.addView(mShowView, mLayoutParams);
        }
    }
    private View mShowView=null;
    private WindowManager.LayoutParams mLayoutParams = null;
    private void ensureInitUi(){

        initWindowLayoutParams();

        LayerFactory layerFactory = new LayerFactory(mContext);
        final LayerView layerView = new LayerView(mContext);

        LayerParent layerParent = layerFactory.createLayerParent(layerView,mVisualRect);
        layerView.setRenderLayer(layerParent);

        layerParent.addTransformChanged(new ITransformChanged() {

            @Override
            public void onTranslate(Matrix matrix) {
                // TODO Auto-generated method stub
                if (mTransformChanged!=null) {
                    mTransformChanged.onTranslate(matrix);
                }
            }

            @Override
            public void onScaled(Matrix matrix) {
                // TODO Auto-generated method stub
                if (mTransformChanged!=null) {
                    mTransformChanged.onScaled(matrix);
                }
            }

            @Override
            public void onRotate(Matrix matrix) {
                // TODO Auto-generated method stub
                if (mTransformChanged!=null) {
                    mTransformChanged.onRotate(matrix);
                }
            }

            @Override
            public void onAction(SelectViewEnum.ActionType t, ILayer layer) {
                // TODO Auto-generated method stub
                if (mTransformChanged!=null) {
                    mTransformChanged.onAction(t, layer);
                }
            }
        });
        layerParent.setOutSideListener(new LayerParent.IOutsidePressed() {

            @Override
            public void onOutSidePressd() {
                // TODO Auto-generated method stub
                if (mUnSelectedListener!=null) {
                    mUnSelectedListener.objectUnSelected(mData);
                }
            }
        });
        onCreateContentLayer(layerParent, layerFactory);
        onCreateDecorateLayer(layerParent, layerFactory);

        mShowView = layerView;
    }
    private void initWindowLayoutParams() {
        final Rect visualRect = mVisualRect;
        mLayoutParams = new WindowManager.LayoutParams(
                visualRect.width(), visualRect.height(),
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        |WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);
        mLayoutParams.x=mVisualRect.left;
        mLayoutParams.y=mVisualRect.top;
        mLayoutParams.gravity= Gravity.TOP|Gravity.LEFT;
    }
    @Override
    public void dismissSelectView() {
        // TODO Auto-generated method stub
        if (mShowView != null) {
            mWindowManager.removeView(mShowView);
        }
        mShowView = null;
    }

    @Override
    public boolean isSelectViewShowing() {
        // TODO Auto-generated method stub
        if (mShowView !=null ) {
            return true;
        }
        return false;
    }
    protected abstract void onCreateContentLayer(LayerParent layerParent,LayerFactory layerFactory) ;
    protected abstract void onCreateDecorateLayer(LayerParent layerParent,LayerFactory layerFactory);

}

