package com.jacky.commondraw.views.selectview;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public interface ITouchable {
    boolean onEventAction(View parentView, MotionEvent event);
}
