package com.jacky.commondraw.views.selectview;

import android.content.Context;
import android.view.View;

import com.jacky.commondraw.model.InsertableObjectBase;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class SelectViewFactory {
    public static ISelectView<InsertableObjectBase>  createSelectView(Context context,View objectParent,InsertableObjectBase data){
        return new SelectViewTwoDrag(context, data, objectParent);
    }
}
