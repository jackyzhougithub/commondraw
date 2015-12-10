package com.jacky.commondraw.shaperecognize;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.jacky.commondraw.model.InsertableObjectBase;
import com.jacky.commondraw.model.propertyconfig.PropertyConfigBase;
import com.jacky.commondraw.model.propertyconfig.PropertyConfigStroke;
import com.jacky.commondraw.model.stroke.StylusPoint;
import com.jacky.commondraw.views.doodleview.IInternalDoodle;
import com.jacky.commondraw.visual.VisualElementBase;
import com.visionobjects.myscript.shape.ShapeEllipticArcData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class InsertableObjectShape extends InsertableObjectBase {
    public static final String TAG = "InsertableObjectShape";
    public static final int STROKE_TYPE_NORMAL = 1;// 圆珠笔
    public static final int PROPERTY_ID_STROKE_WIDTH = 101;
    public static final int PROPERTY_ID_STROKE_COLOR = 102;
    public static final int PROPERTY_ID_STROKE_ALPHA = 103;
    protected int mStrokeType;
    protected float mStrokeWidth = 20;
    protected int mColor = Color.RED;
    protected int mAlpha = 255;

    protected List<StylusPoint> mPoints;
    protected ShapeEllipticArcData mArcData = null;

    public InsertableObjectShape() {
        super(InsertableObjectBase.TYPE_STROKE);
        mStrokeType = STROKE_TYPE_NORMAL;
        mPoints = new ArrayList<StylusPoint>();
    }

    @Override
    public VisualElementBase createVisualElement(Context context,
                                                 IInternalDoodle internalDoodle) {
        Log.i(TAG, "createVisualElement VisualStrokeShape");
        VisualElementBase visualElement = new VisualElementShape(context,
                internalDoodle, this);
        return visualElement;
    }

    @Override
    public PropertyConfigBase getPropertyConfig() {
        return null;
    }

    public List<StylusPoint> getPoints() {
        return mPoints;
    }

    public void setPoints(List<StylusPoint> mPoints) {
        this.mPoints = mPoints;
    }

    public ShapeEllipticArcData getArcData() {
        return mArcData;
    }

    public void setArcData(ShapeEllipticArcData arcData) {
        this.mArcData = arcData;
    }

    public int getStrokeType() {
        return mStrokeType;
    }

    public float getStrokeWidth() {
        return mStrokeWidth;
    }

    public void setStrokeWidth(float mStrokeWidth) {
        if (this.mStrokeWidth != mStrokeWidth) {
            float temp = this.mStrokeWidth;
            this.mStrokeWidth = mStrokeWidth;
            firePropertyChanged(PROPERTY_ID_STROKE_WIDTH, temp, mStrokeWidth);
        }
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int mColor) {
        if (this.mColor != mColor) {
            float temp = this.mColor;
            this.mColor = mColor;
            firePropertyChanged(PROPERTY_ID_STROKE_COLOR, temp, mColor);
        }
    }

    public int getAlpha() {
        return mAlpha;
    }

    public void setAlpha(int mAlpha) {
        if (this.mAlpha != mAlpha) {
            float temp = this.mAlpha;
            this.mAlpha = mAlpha;
            firePropertyChanged(PROPERTY_ID_STROKE_COLOR, temp, mAlpha);
        }
    }

    public static InsertableObjectShape newInsertableObjectShape(
            PropertyConfigStroke property) {
        InsertableObjectShape stroke = new InsertableObjectShape();
        stroke.setAlpha(property.getAlpha());
        stroke.setColor(property.getColor());
        stroke.setStrokeWidth(property.getStrokeWidth());
        return stroke;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        // must return false if the explicit parameter is null
        if (obj == null)
            return false;
        // if the class don't match,they can't be equal
        if (getClass() != obj.getClass())
            return false;

        InsertableObjectShape shape = (InsertableObjectShape) obj;
        boolean isPointsEqual = false;
        if (this.getPoints() == null && shape.getPoints() == null) {
            isPointsEqual = true;
        } else if (this.getPoints() != null && shape.getPoints() != null) {
            if (this.getPoints().containsAll(shape.getPoints())
                    && shape.getPoints().containsAll(this.getPoints()))
                isPointsEqual = true;
            else
                isPointsEqual = false;
        } else
            isPointsEqual = false;
        if (!isPointsEqual)
            return false;

        boolean isArcEqual = false;
        if (this.getArcData() == null && shape.getArcData() == null) {
            isArcEqual = true;
        } else if (this.getArcData() != null && shape.getArcData() != null) {
            isArcEqual = this.getArcData().equals(shape.getArcData());
        } else
            isArcEqual = false;
        if (!isArcEqual)
            return false;
        if (isArcEqual && isPointsEqual)
            return true;
        return false;
    }

}
