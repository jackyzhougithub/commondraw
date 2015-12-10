package com.jacky.commondraw.views.selectview;

import android.graphics.Point;
import android.graphics.Rect;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public interface ILayer extends IDrawableSelf, ITouchable {
    boolean isHitInRect(Point p);
    Rect getCurrentRect();
}