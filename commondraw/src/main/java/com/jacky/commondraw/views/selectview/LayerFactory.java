package com.jacky.commondraw.views.selectview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class LayerFactory {
    private Context mContext = null;

    public LayerFactory(Context context){
        mContext = context;
    }

    public LayerParent createLayerParent(View view,Rect visualRect){
        LayerParent parent = new LayerParent(mContext,view);
        parent.setVisualRect(visualRect);
        return parent;
    }
    public  GraphicLayer createRectGraphicLayer(Matrix matrix,Rect actionRect,ITransformChangedManager m ,Paint drawPaint){
        return new GraphicLayer(mContext, matrix, actionRect, m, new RectDrawable(actionRect, drawPaint));
    }

    public  GraphicLayer createBitmapGraphicLayer(Matrix matrix,Rect actionRect,Bitmap bitmap,ITransformChangedManager m ,Paint drawPaint){
        return new GraphicLayer(mContext, matrix, actionRect, m, new BitmapDrawable(bitmap, drawPaint));
    }

    public RotateDragLayer createRotateDragLayer(IAnchorable anchorable, Point anchor,Rect actionRect, IDrawableSelf drawableSelf,
                                                 ITransformChangedManager m , IRotateCenter center){
        return new RotateDragLayer(anchorable, anchor, actionRect, drawableSelf, m, center);
    }

    public ClickedableAnchorLayer createClickedableAnchorLayer(IAnchorable anchorable, Point anchor,Rect actionRect ,IDrawableSelf drawableSelf,ITransformChangedManager m){
        return new ClickedableAnchorLayer(anchorable, anchor, actionRect, drawableSelf, m);
    }

    public static class RectDrawable implements IDrawableSelf{

        private Rect mDrawRect = null;
        private Paint mDrawPaint = null;
        public RectDrawable(Rect drawrect, Paint drawPaint){
            mDrawRect = new Rect(drawrect);
            mDrawPaint = new Paint(drawPaint);
        }
        @Override
        public void onDrawSelf(Canvas canvas) {
            // TODO Auto-generated method stub
            canvas.drawRect(mDrawRect, mDrawPaint);
        }
    }
    public static class BitmapDrawable implements IDrawableSelf{

        private Bitmap mBitmap = null;
        private Paint mPaint = null;
        public BitmapDrawable(Bitmap bitmap, Paint drawPaint){
            mBitmap = bitmap;
            mPaint = drawPaint;
        }
        @Override
        public void onDrawSelf(Canvas canvas) {
            // TODO Auto-generated method stub
            canvas.drawBitmap(mBitmap, 0.0f, 0.0f, mPaint);
        }

    }
}
