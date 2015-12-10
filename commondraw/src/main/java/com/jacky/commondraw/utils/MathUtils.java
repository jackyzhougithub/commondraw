package com.jacky.commondraw.utils;

import android.graphics.Point;
import android.graphics.PointF;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class MathUtils {
    public static int caculateTwoPointDistance(Point p1,Point p2){
        if (p1 == null || p2 == null) {
            return 0;
        }
        return (int) Math.sqrt(Math.pow(p2.x-p1.x, 2)+Math.pow(p2.y-p1.y, 2));
    }
    public static float caculateTwoPointDistance(PointF p1, PointF p2){
        if (p1 == null || p2 == null) {
            return 0;
        }
        return  (float) Math.sqrt(Math.pow(p2.x-p1.x, 2)+Math.pow(p2.y-p1.y, 2));
    }
}
