package com.jacky.commondraw.views.selectview;

import android.graphics.Matrix;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public interface ITransformChangedManager {

    void addTransformChanged(ITransformChanged t);
    void removeTransformChanged(ITransformChanged t);
    void notifyChanged(SelectViewEnum.Type t,Matrix matrix);
    void notifyAction(SelectViewEnum.ActionType t,ILayer layer);
}
