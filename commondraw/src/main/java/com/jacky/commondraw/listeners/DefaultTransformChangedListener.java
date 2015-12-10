package com.jacky.commondraw.listeners;

import android.graphics.Matrix;
import android.graphics.RectF;

import com.jacky.commondraw.model.InsertableObjectBase;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 * 默认实现了旋转，平移，缩放的操作。使用者可以实现自己的替换
 */
public class DefaultTransformChangedListener implements
        ITransformChangedListener  {
    @Override
    public void onScaled(InsertableObjectBase insertableObject, Matrix matrix) {
        // TODO Auto-generated method stub
        float[] p9 = new float[9];
        matrix.getValues(p9);
        Matrix m = new Matrix();
        RectF rectF = new RectF(insertableObject.getInitRectF());
        rectF = new RectF(0, 0, rectF.width(), rectF.height());
        insertableObject.getMatrix().mapRect(rectF);
        m.setScale(p9[Matrix.MSCALE_X], p9[Matrix.MSCALE_X], rectF.centerX(),
                rectF.centerY());
        insertableObject.getMatrix().postConcat(m);
        insertableObject.setMatrix(new Matrix(insertableObject.getMatrix()));
    }

    @Override
    public void onTranslate(InsertableObjectBase insertableObject, Matrix matrix) {
        // TODO Auto-generated method stub
        insertableObject.getMatrix().postConcat(matrix);
        insertableObject.setMatrix(new Matrix(insertableObject.getMatrix()));
    }

    @Override
    public void onRotate(InsertableObjectBase insertableObject, Matrix matrix) {
        // TODO Auto-generated method stub
        insertableObject.getMatrix().postConcat(matrix);
        insertableObject.setMatrix(new Matrix(insertableObject.getMatrix()));
    }
}
