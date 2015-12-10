package com.jacky.commondraw.shaperecognize;

import android.graphics.Matrix;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class MyPoint {
    float x;
    float y;
    double abs = -1;

    public MyPoint(float tempx, float tempy, float size) {
        x = tempx;
        y = tempy;

        abs = Math.sqrt(x * x + y * y);
        x = (float) (x / abs * size);
        y = (float) (y / abs * size);

        abs = abs * size;
    }

    public MyPoint(float tempx, float tempy) {
        x = tempx;
        y = tempy;
        abs = -1;
    }

    public MyPoint getDistance(MyPoint p1) {
        return new MyPoint(x - p1.x, y - p1.y);
    }

    public MyPoint getTotalDistance(MyPoint p1) {
        return new MyPoint(x + p1.x, y + p1.y);
    }

    public Boolean isSimaler(MyPoint p1) {
        double p1abs = p1.getAbs();
        double tempabs = getAbs();
        double scale = 0;

        if (p1abs > tempabs) {
            scale = tempabs / p1abs;
        } else {
            scale = p1abs / tempabs;
        }

        if (scale < 0.5) {
            return false;
        }
        if (getCosTwoPoint(p1) > 0.7) {
            return true;
        }
        return false;
    }

    public double getAbs() {
        if (abs < 0) {
            abs = Math.sqrt(x * x + y * y);
        }
        return abs;
    }

    public MyPoint getCenterPoint(MyPoint p1) {
        return new MyPoint((x + p1.x) / 2, (y + p1.y) / 2);
    }

    public double getCosTwoPoint(MyPoint p1) {
        double cosValue = (x * p1.x + y * p1.y) / (getAbs() * p1.getAbs());
        if (cosValue > 1) {
            cosValue = 1;
        } else if (cosValue < -1) {
            cosValue = -1;
        }
        return cosValue;
    }

    public Boolean getDirect(MyPoint p1) {
        if (x * p1.y - p1.x * y > 0) {
            return true;
        }
        return false;
    }

    public float[] getRotateResMyPoint(float ang) {
        Matrix mt = new Matrix();
        mt.setRotate(ang, 0, 0);

        float[] src = new float[2];
        src[0] = x;
        src[1] = y;

        float[] dst = new float[2];
        mt.mapPoints(dst, src);

        return dst;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
