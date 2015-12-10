package com.jacky.commondraw.model;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;

import com.jacky.commondraw.listeners.DefaultTransformChangedListener;
import com.jacky.commondraw.listeners.ITransformChangedListener;
import com.jacky.commondraw.model.propertyconfig.PropertyConfigBase;
import com.jacky.commondraw.views.doodleview.IInternalDoodle;
import com.jacky.commondraw.visual.VisualElementBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 * 可插入object
 */
public abstract class InsertableObjectBase {
    /**
     * 0-100留给默认实现的类型
     */
    public static final int TYPE_NONE = 0;
    public static final int TYPE_STROKE = 1;// 画笔
    public static final int TYPE_IMAGE = 2;// 图片

    /**
     * 定义缩放的方式
     */
    public static final int SCALE_TYPE_NO = 0;// 不支持缩放
    public static final int SCALE_TYPE_FREE = 1;// 可自由缩放

    public static final int PROPERTY_ID_NONE = 0;
    public static final int PROPERTY_ID_VISIBLE = 1;
    public static final int PROPERTY_ID_DRAWNRECTF = 2;
    public static final int PROPERTY_ID_MATRIX = 3;
    public static final int PROPERTY_ID_ATTACHFILEPATH = 4;
    protected int mType = TYPE_NONE;

    protected boolean mVisible = true;// 是否显示
    protected RectF mInitRectF;// 绘制区域;记录初始位置。该变量只应该记录初始位置
    // protected boolean mSelectable = true;// 是否能被选中

    protected boolean mRotateable = false;// 能否旋转
    // protected float mRotationAngle = 0;// 选中角度
    protected Matrix mMatrix = new Matrix();// 矩形变换。旋转，平移，缩放，都是通过这个矩阵来实现

    protected boolean mMoveable = true;

    protected int mScaleType = SCALE_TYPE_NO;

    protected float mMinWidth = 0;
    protected float mMinHeight = 0;
    protected float mMaxWidth = Float.MAX_VALUE;
    protected float mMaxHeight = Float.MAX_VALUE;

    protected String mAttachFilePath = null;

    protected HashMap<String, Object> mExtraProperties;

    protected List<IPropertyValueChangedListener> mPropertyValueChangedListenerList;

    public InsertableObjectBase(int type) {
        mType = type;
        mExtraProperties = new HashMap<String, Object>();
        mPropertyValueChangedListenerList = new ArrayList<IPropertyValueChangedListener>();
    }

    public boolean isVisible() {
        return mVisible;
    }

    public void setVisible(boolean mVisible) {
        if (mVisible != this.mVisible) {
            boolean oldValue = this.mVisible;
            this.mVisible = mVisible;
            firePropertyChanged(PROPERTY_ID_VISIBLE, oldValue, mVisible);
        }
    }

    public RectF getInitRectF() {
        return new RectF(mInitRectF);
    }

    public void setInitRectF(RectF initRectF) {
        RectF oldValue = this.mInitRectF;
        this.mInitRectF = initRectF;
        firePropertyChanged(PROPERTY_ID_DRAWNRECTF, oldValue, initRectF);
    }

    /**
     * 是否可以被选中。默认为false
     *
     * @return
     */
    public boolean isSelectable() {
        return false;
    }

    public int getType() {
        return mType;
    }

    public void setType(int mType) {
        this.mType = mType;
    }

    public boolean isMoveable() {
        return mMoveable;
    }

    public void setMoveable(boolean mMoveable) {
        this.mMoveable = mMoveable;
    }

    public boolean isRotateable() {
        return mRotateable;
    }

    public void setRotateable(boolean mRotateable) {
        this.mRotateable = mRotateable;
    }

    public Matrix getMatrix() {
        return mMatrix;
    }

    public void setMatrix(Matrix matrix) {
        setMatrix(matrix, false);
    }

    /**
     *
     * @param matrix
     * @param fromUndoRedo
     *            ：该属性的变化是否由undo,redo导致的
     */
    public void setMatrix(Matrix matrix, boolean fromUndoRedo) {
        Matrix oldValue = this.mMatrix;
        this.mMatrix = matrix;
        firePropertyChanged(PROPERTY_ID_MATRIX, oldValue, mMatrix);
    }

    public int getScaleType() {
        return mScaleType;
    }

    public void setScaleType(int mScaleType) {
        this.mScaleType = mScaleType;
    }

    public String getAttachFilePath() {
        return mAttachFilePath;
    }

    public void setAttachFilePath(String mAttachFilePath) {
        if (mAttachFilePath != this.mAttachFilePath) {
            String oldValue = this.mAttachFilePath;
            this.mAttachFilePath = mAttachFilePath;
            firePropertyChanged(PROPERTY_ID_ATTACHFILEPATH, oldValue,
                    mAttachFilePath);
        }
    }

    public float getMinWidth() {
        return mMinWidth;
    }

    public void setMinWidth(float mMinWidth) {
        this.mMinWidth = mMinWidth;
    }

    public float getMinHeight() {
        return mMinHeight;
    }

    public void setMinHeight(float mMinHeight) {
        this.mMinHeight = mMinHeight;
    }

    public float getMaxWidth() {
        return mMaxWidth;
    }

    public void setMaxWidth(float mMaxWidth) {
        this.mMaxWidth = mMaxWidth;
    }

    public float getMaxHeight() {
        return mMaxHeight;
    }

    public void setMaxHeight(float mMaxHeight) {
        this.mMaxHeight = mMaxHeight;
    }

    /**
     * 添加一个key。如果该key已经存在，则替换原来的值。
     *
     * @param key
     * @param object
     */
    public void addOrModifyExtraProperty(String key, Object object) {
        Object oldValue = mExtraProperties.get(key);
        if (oldValue == null) {
            mExtraProperties.put(key, object);
            fireExtraPropertyValueAdded(key);
        } else {
            mExtraProperties.put(key, object);
            fireExtraPropertyValueChanged(key, oldValue, object);
        }
    }

    public void remoceExtraProperty(String key) {
        Object oldValue = mExtraProperties.get(key);
        if (oldValue != null) {
            mExtraProperties.remove(key);
            fireExtraPropertyValueDeleted(key);
        }
    }

    public Object getExtraPropertyValue(String key) {
        return mExtraProperties.get(key);
    }

    public void firePropertyChanged(int propertyId, Object oldValue,
                                    Object newValue) {
        firePropertyChanged(propertyId, oldValue, newValue, false);
    }

    public void firePropertyChanged(int propertyId, Object oldValue,
                                    Object newValue, boolean fromUndoRedo) {
        if (mPropertyValueChangedListenerList.size() > 0) {
            for (IPropertyValueChangedListener listener : mPropertyValueChangedListenerList) {
                listener.onPropertyValeChanged(this, propertyId, oldValue,
                        newValue, fromUndoRedo);
            }
        }
    }

    public void fireExtraPropertyValueAdded(String key) {
        fireExtraPropertyValueAdded(key, false);
    }

    public void fireExtraPropertyValueAdded(String key, boolean fromUndoRedo) {
        if (mPropertyValueChangedListenerList.size() > 0) {
            for (IPropertyValueChangedListener listener : mPropertyValueChangedListenerList) {
                listener.onExtraPropertyValueAdded(this, key, fromUndoRedo);
            }
        }
    }

    public void fireExtraPropertyValueDeleted(String key, boolean fromUndoRedo) {
        if (mPropertyValueChangedListenerList.size() > 0) {
            for (IPropertyValueChangedListener listener : mPropertyValueChangedListenerList) {
                listener.onExtraPropertyValueDeleted(this, key, fromUndoRedo);
            }
        }
    }

    public void fireExtraPropertyValueDeleted(String key) {
        fireExtraPropertyValueDeleted(key, false);
    }

    public void fireExtraPropertyValueChanged(String key, Object oldValue,
                                              Object newValue) {
        fireExtraPropertyValueChanged(key, oldValue, newValue, false);
    }

    public void fireExtraPropertyValueChanged(String key, Object oldValue,
                                              Object newValue, boolean fromUndoRedo) {
        if (mPropertyValueChangedListenerList.size() > 0) {
            for (IPropertyValueChangedListener listener : mPropertyValueChangedListenerList) {
                listener.onExtraPropertyValueChanged(this, key, oldValue,
                        newValue, fromUndoRedo);
            }
        }
    }

    public void addPropertyChangedListener(
            IPropertyValueChangedListener listener) {
        if (!mPropertyValueChangedListenerList.contains(listener)) {
            mPropertyValueChangedListenerList.add(listener);
        }
    }

    public void deletePropertyChangedListener(
            IPropertyValueChangedListener listener) {
        if (mPropertyValueChangedListenerList.contains(listener)) {
            mPropertyValueChangedListenerList.remove(listener);
        }
    }

    /**
     * 创建该对象对应的Visual层元素
     *
     * @return
     */
    public abstract VisualElementBase createVisualElement(Context context,
                                                          IInternalDoodle internalDoodle);

    /**
     * 创建该对象对应的ITransformChanged
     *
     * @return
     */
    public ITransformChangedListener createTransformChangedListener() {
        return new DefaultTransformChangedListener();
    }

    /**
     * 可以使用橡皮擦，擦除。默认为true
     *
     * @return
     */
    public boolean canErased() {
        return true;
    }

    /**
     * 获得该对象对应的Property Config
     *
     * @return
     */
    public abstract PropertyConfigBase getPropertyConfig();

    /**
     * 获得变换后的RectF
     *
     * @return
     */
    public static RectF getTransformedRectF(
            InsertableObjectBase insertableObject) {
        RectF src = insertableObject.getInitRectF();
        RectF dst = new RectF(src);
        Matrix matrix = insertableObject.getMatrix();
        if (matrix != null)
            matrix.mapRect(dst, src);
        return dst;
    }

}
