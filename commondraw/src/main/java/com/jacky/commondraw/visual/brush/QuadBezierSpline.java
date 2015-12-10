package com.jacky.commondraw.visual.brush;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class QuadBezierSpline {
    private HWPoint mControl = new HWPoint();
    private HWPoint mDestination = new HWPoint();
    private HWPoint mNextControl = new HWPoint();
    private HWPoint mSource = new HWPoint();

    public QuadBezierSpline() {
    }

    public void Init(HWPoint last, HWPoint cur)
    {
        Init(last.x, last.y, last.width, cur.x, cur.y, cur.width);
    }

    public void Init(float lastx, float lasty, float lastWidth, float x, float y, float width)
    {
        mSource.Set(lastx, lasty, lastWidth);
        float xmid = GetMid(lastx, x);
        float ymid = GetMid(lasty, y);
        float wmid = GetMid(lastWidth, width);
        mDestination.Set(xmid, ymid, wmid);
        mControl.Set(GetMid(lastx,xmid),GetMid(lasty,ymid),GetMid(lastWidth,wmid));
        mNextControl.Set(x, y, width);
    }

    public void AddNode(HWPoint cur){
        AddNode(cur.x, cur.y, cur.width);
    }

    public void AddNode(float x, float y, float width){
        mSource.Set(mDestination);
        mControl.Set(mNextControl);
        mDestination.Set(GetMid(mNextControl.x, x), GetMid(mNextControl.y, y), GetMid(mNextControl.width, width));
        mNextControl.Set(x, y, width);
    }

    public void End() {
        mSource.Set(mDestination);
        float x = GetMid(mNextControl.x, mSource.x);
        float y = GetMid(mNextControl.y, mSource.y);
        float w = GetMid(mNextControl.width, mSource.width);
        mControl.Set(x, y, w);
        mDestination.Set(mNextControl);
    }

    public HWPoint GetPoint(double t){
        float x = (float)GetX(t);
        float y = (float)GetY(t);
        float w = (float)GetW(t);
        HWPoint point = new HWPoint();
        point.Set(x,y,w);
        return point;
    }

    private double GetValue(double p0, double p1, double p2, double t){
        double A = p2 - 2 * p1 + p0;
        double B = 2 * (p1 - p0);
        double C = p0;
        return A * t * t + B * t + C;
    }

    private double GetX(double t) {
        return GetValue(mSource.x, mControl.x, mDestination.x, t);
    }

    private double GetY(double t) {
        return GetValue(mSource.y, mControl.y, mDestination.y, t);
    }

    private double GetW(double t){
        return GetWidth(mSource.width, mDestination.width, t);
    }

    private float GetMid(float x1, float x2) {
        return (float)((x1 + x2) / 2.0);
    }

    // Linear change of width
    private double GetWidth(double w0, double w1, double t){
        return w0 + (w1 - w0) * t;
    }

}
