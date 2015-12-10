package com.jacky.commondraw.views.geometry;

import android.graphics.Point;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public final class GeoVector2D {
    private double x;
    private double y;
    /**
     * Vector [x,y]
     * @param head Head point in Vector
     * @param tail Tail point in Vector
     */
    public GeoVector2D(Point head,Point tail){
        x = head.x-tail.x;
        y = head.y-tail.y;
    }
    /**
     * Vector [x,y]
     * @param x
     * @param y
     */
    public GeoVector2D(double x,double y){
        this.x=x;
        this.y=y;
    }
    public GeoVector2D(GeoVector2D vector2d){
        this.x=vector2d.x;
        this.y=vector2d.y;
    }
    public double getVectorLength(){
        return Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2));
    }
    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }
    public double dotProductVector(GeoVector2D vector){
        if (vector==null) {
            return 0;
        }else {
            return x*vector.getX()+y*vector.getY();
        }
    }
    /**
     * the cosine of the θ between the vectors
     * @param vector
     * @return
     */
    public double getCosineWithVector(GeoVector2D vector){
        if (vector==null) {
            throw new IllegalArgumentException("vector cannot be null");
        }
        return dotProductVector(vector)/(getVectorLength()*vector.getVectorLength());
    }
    /**
     * The ret value is the projection of this vector parallel to vectorBase
     * @param vectorBase
     * @return
     */
    public GeoVector2D getParallelVectorBase(GeoVector2D vectorBase){
        if (vectorBase==null) {
            throw new IllegalArgumentException("VectorBase cannot be null");
        }
        double scalar= dotProductVector(vectorBase)/Math.pow(vectorBase.getVectorLength(), 2);
        GeoVector2D ret = vectorBase.cloneSelf();
        ret.multiplyByScalar(scalar);
        return ret;
    }
    /**
     * The ret value is perpendicular to vectorBase
     * @param vectorBase
     * @return
     */
    public GeoVector2D getPerpendicularVectorBase(GeoVector2D vectorBase) {
        if (vectorBase == null) {
            throw new IllegalArgumentException("VectorBase cannot be null");
        }
        GeoVector2D parallelVector2d = getParallelVectorBase(vectorBase);
        GeoVector2D perpVector2d = new GeoVector2D(x - parallelVector2d.x, y
                - parallelVector2d.y);
        return perpVector2d;
    }

    public GeoVector2D cloneSelf() {
        GeoVector2D vector2d = new GeoVector2D(x, y);
        return vector2d;
    }

    public void multiplyByScalar(double scalar) {
        x *= scalar;
        y *= scalar;
    }

    public void normalize() {
        double magSq = x * x + y * y;
        if (magSq > 0) {
            double oneOverMag = 1.0 / Math.sqrt(magSq);
            x *= oneOverMag;
            y *= oneOverMag;
        }
    }
}
