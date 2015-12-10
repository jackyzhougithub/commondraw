package com.jacky.commondraw.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.jacky.commondraw.utils.LoadUtils;
import com.jacky.commondraw.views.doodleview.DoodleEnum;
import com.jacky.commondraw.views.doodleview.IInternalDoodle;
import com.jacky.commondraw.views.doodleview.opereation.DoodleOperation;
import com.jacky.commondraw.views.doodleview.opereation.DrawAllOperation;
import com.jacky.commondraw.views.doodleview.opereation.TransformingOperation;
import com.jacky.commondraw.visual.VisualElementBase;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class VisualElementBitmap extends VisualElementBase {

    private Paint mDrawPaint = null;
    private Bitmap mCacheBitmap = null;

    public VisualElementBitmap(Context context, IInternalDoodle internalDoodle,
                               InsertableObjectBase object) {
        super(context, internalDoodle, object);
        // TODO Auto-generated constructor stub
        mDrawPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    public void draw(Canvas canvas) {
        // TODO Auto-generated method stub
        if (mInsertableObject.isVisible()
                && mInsertableObject instanceof InsertableBitmap) {
            InsertableBitmap data = (InsertableBitmap) mInsertableObject;
            final Bitmap bitmap = loadBitmap(data);
            if (null != bitmap) {
                int c = canvas.save();
                canvas.drawBitmap(bitmap, data.getMatrix(), mDrawPaint);
                // canvas.drawBitmap(bitmap, 0.0f, 0.0f, mDrawPaint);
                canvas.restoreToCount(c);
            }
        }
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub
        if (mInsertableObject instanceof InsertableBitmap) {
            InsertableBitmap data = (InsertableBitmap) mInsertableObject;
            loadBitmap(data);
        }
    }

    public Bitmap loadBitmap(InsertableBitmap data) {
        if (null == mCacheBitmap || mCacheBitmap.isRecycled()) {
            mCacheBitmap = null;
            LoadUtils load = LoadUtils.getInstance(mContext);
            LoadUtils.LoadResult resut = null;
            if (data.getHeight() == 0 || data.getWidth() == 0) {// first load
                resut = load.loadBitmapByPath(data.getAttachFilePath(),
                        LoadUtils.LoadType.Sampled);
                if (resut != null && resut.successed) {
                    data.setHeight(resut.sampledHeight);
                    data.setWidth(resut.sampledWidth);
                    data.setRawHeight(resut.rawHeight);
                    data.setRawWidth(resut.rawWidth);
                    data.setBitmapSampleSize(resut.sampleSize);
                    data.initVisualRect(resut.sampledWidth, resut.sampledHeight);
                    mCacheBitmap = resut.bitmap;
                }
            } else {// load sec
                resut = load.loadBitmapByPath(data.getAttachFilePath(),
                        data.getBitmapSampleSize());
                if (null != resut && resut.successed) {
                    mCacheBitmap = resut.bitmap;
                }
            }
        }
        return mCacheBitmap;
    }

    public void clearCache() {
        if (mCacheBitmap != null && !mCacheBitmap.isRecycled()) {
            mCacheBitmap.recycle();
            mCacheBitmap = null;
        }
    }

    @Override
    public void onPropertyValeChanged(InsertableObjectBase insertableObject,
                                      int propertyId, Object oldValue, Object newValue,
                                      boolean fromUndoRedo) {
        // TODO Auto-generated method stub
        if (propertyId == InsertableObjectBase.PROPERTY_ID_MATRIX) {
            // Matrix oldMatrix = (Matrix) oldValue;
            // Matrix newMatrix = (Matrix) newValue;
            DoodleOperation operation = null;
            if (mInternalDoodle.getSelectionMode() == DoodleEnum.SelectionMode.SELECTION) {
                TransformingOperation tempoperation = new TransformingOperation(
                        mInternalDoodle.getFrameCache(),
                        mInternalDoodle.getModelManager(),
                        mInternalDoodle.getVisualManager(), insertableObject);
                // operation.setDrawBitmapCommondParams(oldMatrix, newMatrix);
                if (insertableObject instanceof InsertableBitmap) {
                    if (!fromUndoRedo) {
                        tempoperation.setCreatingCommand(true);
                    } else {
                        tempoperation.setCreatingCommand(false);
                    }
                }
                // tempoperation.setCommandParams((Matrix) oldValue);
                operation = tempoperation;
            } else {
                operation = new DrawAllOperation(
                        mInternalDoodle.getFrameCache(),
                        mInternalDoodle.getModelManager(),
                        mInternalDoodle.getVisualManager());
            }
            sendOperation(operation);
        }
    }
}

