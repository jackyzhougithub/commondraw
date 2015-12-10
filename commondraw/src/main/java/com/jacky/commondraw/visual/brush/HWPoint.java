package com.jacky.commondraw.visual.brush;

import com.jacky.commondraw.model.stroke.StylusPoint;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 * 画点
 */
public class HWPoint {
    public float x;
    public float y;
    public long timestamp; // timestamp
    public float pressure;
    public float width; // render width
    public int alpha = 255;

    public HWPoint() {
    }

    public HWPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public HWPoint(float x, float y, long t) {
        this.x = x;
        this.y = y;
        this.timestamp = t;
    }

    public void Set(float x, float y, float w) {
        this.x = x;
        this.y = y;
        this.width = w;
    }

    public void Set(HWPoint point) {
        this.x = point.x;
        this.y = point.y;
        this.width = point.width;
    }

    public void Set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        String str = "X = " + x + "; Y = " + y + "; W = " + width;
        return str;
    }

    // public Point ToPoint() {
    // return new Point(x,y);
    // }

    public StylusPoint ToStylusPoint() {
        return new StylusPoint(x, y, pressure);
    }
}
