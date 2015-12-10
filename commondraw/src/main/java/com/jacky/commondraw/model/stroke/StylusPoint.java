package com.jacky.commondraw.model.stroke;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 * 一个画笔点
 */
public class StylusPoint {
    public float x;
    public float y;
    public float pressure = 1.0f;//压力值

    public StylusPoint() {
        this(0, 0, 1.0f);
    }

    public StylusPoint(float x, float y) {
        this.x = x;
        this.y = y;
        this.pressure = 1.0f;
    }

    public StylusPoint(float x, float y, float pressure) {
        this.x = x;
        this.y = y;
        this.pressure = pressure;
    }

    public StylusPoint(StylusPoint point) {
        x = point.x;
        y = point.y;
        pressure = point.pressure;
    }

    /**
     * 重新定义对象相等
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;

        StylusPoint point = (StylusPoint)obj;
        boolean isPointEqual = false;
        if(point!=null && point!=null){
            if(this.x == point.x&&this.y==point.y&&this.pressure==point.pressure)
                isPointEqual = true;
            else
                isPointEqual = false;
        }else
            isPointEqual = false;

        return isPointEqual;
    }
}
