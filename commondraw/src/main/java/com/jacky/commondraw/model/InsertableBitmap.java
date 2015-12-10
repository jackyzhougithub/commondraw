package com.jacky.commondraw.model;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;

import com.jacky.commondraw.model.propertyconfig.PropertyConfigBase;
import com.jacky.commondraw.views.doodleview.IInternalDoodle;
import com.jacky.commondraw.visual.VisualElementBase;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class InsertableBitmap  extends InsertableObjectBase {

    protected int mHeight = 0;
    protected int mWidth = 0;
    protected int mRawHeight = 0;
    protected int mRawWidth = 0;
    protected int mSampleSize = 1;// for load bitmap from file
    //protected Matrix mOldMatrix = null;

    public InsertableBitmap() {
        super(InsertableObjectBase.TYPE_IMAGE);
        // TODO Auto-generated constructor stub
        initProperties();
    }

    private void initProperties() {
        setMoveable(true);
        setRotateable(true);
        setScaleType(SCALE_TYPE_FREE);
        setVisible(true);
        // setSelectable(true);
        setMinHeight(1.0f);
        setMinWidth(1.0f);
        mMatrix = new Matrix();
        //mOldMatrix = null;
        mInitRectF = new RectF();
    }

    @Override
    public VisualElementBase createVisualElement(Context context,
                                                 IInternalDoodle internalDoodle) {
        // TODO Auto-generated method stub
        return new VisualElementBitmap(context, internalDoodle, this);
    }

    @Override
    public PropertyConfigBase getPropertyConfig() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setRawHeight(int h) {
        mRawHeight = h;
    }

    public int getRawHeight() {
        return mRawHeight;
    }

    public void setRawWidth(int w) {
        mRawWidth = w;
    }

    public int getRawWidth() {
        return mRawWidth;
    }

    public void setHeight(int h) {
        mHeight = h;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setWidth(int w) {
        mWidth = w;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setBitmapSampleSize(int size) {
        mSampleSize = size;
    }

    public int getBitmapSampleSize() {
        return mSampleSize;
    }
    /**
     * Store the current matrix as old matrix,and when you call the setMatrix(Matrix),the old value will be the
     * redo value.after call setMatrix(Matrix),the old matrix will be set null
     */
//	public void storeOldMatrix() {
//		mOldMatrix = new Matrix(mMatrix);
//	}
//
//	public void storeOldMatrix(Matrix oldMatrix) {
//		mOldMatrix = new Matrix(oldMatrix);
//	}
    /**
     * if you called the storeOldMatrix() , the  old value will be the stored value,or it will be the current value,and
     * the new value as the current matrix
     */
//	@Override
//	public void setMatrix(Matrix mMatrix) {
//		// TODO Auto-generated method stub
////		super.setMatrix(mMatrix);
//		if (mMatrix != this.mMatrix) {
//			Matrix oldValue = this.mMatrix;
//			this.mMatrix = mMatrix;
//			if (mOldMatrix!=null) {
//				oldValue = mOldMatrix;
//				mOldMatrix = null;
//			}
//			firePropertyChanged(PROPERTY_ID_MATRIX, oldValue, mMatrix);
//		}
//	}

//	private boolean mCreateCommand = false;

//	public boolean isCreateUndoRedoCommand() {
//		return mCreateCommand;
//	}

//	public void setCreateUndoRedoCommand(boolean create) {
//		mCreateCommand = create;
//	}
	/*
	 * Init the rect base 0,0.
	 */

    public void initVisualRect(int w, int h) {
        mMatrix.preTranslate(-w / 2, -h / 2);
        mInitRectF = new RectF(0.0f, 0.0f, w, h);
//		mMatrix.mapRect(mInitRectF);
    }

    @Override
    public boolean isSelectable() {
        // TODO Auto-generated method stub
        return true;
    }
}
