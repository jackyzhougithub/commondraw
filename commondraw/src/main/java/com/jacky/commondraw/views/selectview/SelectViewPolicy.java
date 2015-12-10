package com.jacky.commondraw.views.selectview;

import android.content.Context;
import android.content.res.Resources;
import android.view.ViewConfiguration;

import com.jacky.commondraw.R;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class SelectViewPolicy {
    private Context mContext ;
    private ViewConfiguration mViewConfiguration ;
    private static SelectViewPolicy sSelectViewPolicy;
    public static SelectViewPolicy getSelectViewPolicy(Context context){
        if (sSelectViewPolicy == null) {
            sSelectViewPolicy = new SelectViewPolicy(context);
        }
        return sSelectViewPolicy;
    }
    private SelectViewPolicy(Context context){
        mContext = context;
        mViewConfiguration = ViewConfiguration.get(context);
    }
    public float getTouchRestrictTolerance(){
        final Resources resources = mContext.getResources();
        return resources.getDimension(R.dimen.select_view_frame_line_padding);
    }
}
