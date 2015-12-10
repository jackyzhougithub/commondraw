package com.jacky.commondraw.views.selectview;

import android.graphics.Matrix;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public interface ITransformChanged {
    void onScaled(Matrix matrix);
    void onTranslate(Matrix matrix);
    void onRotate(Matrix matrix);
    void onAction(SelectViewEnum.ActionType t, ILayer layer);

}
