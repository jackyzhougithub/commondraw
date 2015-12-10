package com.jacky.commondraw.model.propertyconfig;

import android.graphics.Color;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.coms
 * 笔刷属性
 */
public class PropertyConfigStroke extends PropertyConfigBase {
    protected float mStrokeWidth = 20;// 画笔宽度
    protected int mColor = Color.RED;
    /**
     * 透明度 0-255
     */
    protected int mAlpha = 255;

    public float getStrokeWidth() {
        return mStrokeWidth;
    }

    public void setStrokeWidth(float mStrokeWidth) {
        this.mStrokeWidth = mStrokeWidth;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int mColor) {
        this.mColor = mColor;
    }

    public int getAlpha() {
        return mAlpha;
    }

    public void setAlpha(int mAlpha) {
        this.mAlpha = mAlpha;
    }
}
