package com.jacky.commondraw.views.selectview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import com.jacky.commondraw.R;
import com.jacky.commondraw.listeners.IClickedListener;
import com.jacky.commondraw.model.InsertableObjectBase;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class SelectViewTwoDrag extends SelectViewBase<InsertableObjectBase> {

    public SelectViewTwoDrag(Context context, InsertableObjectBase object, View parentView) {
        super(context, object, parentView);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onCreateContentLayer(LayerParent layerParent,
                                        LayerFactory layerFactory) {
        // TODO Auto-generated method stub
        final RectF rect =mData.getInitRectF();
//		GraphicLayer contentLayer = layerFactory.
    }

    @Override
    protected void onCreateDecorateLayer(LayerParent layerParent,
                                         LayerFactory layerFactory) {
        // TODO Auto-generated method stub
        // we need the T info to create

        GraphicLayer panel = createRestrictRegion(layerParent, layerFactory);
        final Rect region = panel.getSrcRect();

        Resources res=mContext.getResources();
        Bitmap cancelBitmap = BitmapFactory.decodeResource(res, R.drawable.edit_pic_delete);
        Bitmap rotateBitmap = BitmapFactory.decodeResource(res, R.drawable.edit_pic_rotation);

        RotateDragLayer rotateDragLayer = layerFactory.createRotateDragLayer
                (panel, new Point(region.right, region.bottom),
                        new Rect(region.right-rotateBitmap.getWidth()/2,
                                region.bottom-rotateBitmap.getHeight()/2,
                                region.right+rotateBitmap.getWidth()/2,
                                region.bottom+rotateBitmap.getHeight()/2),
                        new LayerFactory.BitmapDrawable(rotateBitmap,
                                new Paint(Paint.ANTI_ALIAS_FLAG)) ,
                        layerParent, panel);
        layerParent.addLayer(rotateDragLayer);

        ClickedableAnchorLayer clickLayer = layerFactory.createClickedableAnchorLayer(
                panel, new Point(region.right,region.top),
                new Rect(region.right-cancelBitmap.getWidth()/2,
                        region.top-cancelBitmap.getHeight()/2,
                        region.right+cancelBitmap.getWidth()/2,
                        region.top+cancelBitmap.getHeight()/2) ,
                new LayerFactory.BitmapDrawable(cancelBitmap, new Paint(Paint.ANTI_ALIAS_FLAG)) ,
                layerParent);
        layerParent.addLayer(clickLayer);
        clickLayer.setClickedListener(new IClickedListener() {

            @Override
            public void onClicked() {
                // TODO Auto-generated method stub
                if (mDeleteListener!=null) {
                    mDeleteListener.onClicked();
                }
            }
        });
    }
    protected GraphicLayer createRestrictRegion(LayerParent layerParent,LayerFactory layerFactory){
        final Resources resources = mContext.getResources();
        Paint paint= new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(resources.getColor(R.color.select_view_frame_line_color));
        paint.setStrokeWidth(resources.getDimension(R.dimen.select_view_frame_line_width));
        paint.setPathEffect(new DashPathEffect(new float[]{resources.getDimension(R.dimen.select_view_frame_line_height),resources.getDimension(R.dimen.select_view_frame_line_intervals)}, 0));
        final RectF objectRect =new RectF(mData.getInitRectF());
        final int left = 0;
        final int top = 0;
        final Rect rect = new Rect(left, top, (int)(left+objectRect.width()), (int)(top+objectRect.height()));
        final int tolorance = (int)SelectViewPolicy.getSelectViewPolicy(mContext).getTouchRestrictTolerance();
        Matrix matrix = new Matrix(mData.getMatrix());
        matrix.postTranslate(-mSrcView.getScrollX(), -mSrcView.getScrollY());
        GraphicLayer panel = layerFactory.createRectGraphicLayer(matrix, new Rect((int)rect.left-tolorance, (int)rect.top-tolorance, (int)rect.right+tolorance, (int)rect.bottom+tolorance), layerParent, paint);
        layerParent.addLayer(panel);
        return panel;
    }
}
