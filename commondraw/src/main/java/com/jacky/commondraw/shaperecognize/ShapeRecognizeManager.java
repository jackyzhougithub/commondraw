package com.jacky.commondraw.shaperecognize;

import android.content.Context;

import com.jacky.commondraw.listeners.IStrokeReadyListener;
import com.jacky.commondraw.model.InsertableObjectBase;
import com.jacky.commondraw.model.propertyconfig.PropertyConfigStroke;
import com.jacky.commondraw.model.stroke.InsertableObjectStroke;
import com.jacky.commondraw.views.doodleview.IInternalDoodle;
import com.visionobjects.myscript.shape.ShapeDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public  class ShapeRecognizeManager implements IShapeRecognizeManager,
        IStrokeReadyListener {
    private MyShape mShape = null;
    private IInternalDoodle mInternalDoodle = null;
    private PropertyConfigStroke mCurrentProperty;
    private List<ShapeDocument> mShapeDocuments;
    private List<PropertyConfigStroke> mStrokePropertys;
    public static List<InsertableObjectBase> mObjectList;

    public ShapeRecognizeManager(IInternalDoodle internalDoodle, Context context) {
        mInternalDoodle = internalDoodle;
        mShape = new MyShape();
        mShapeDocuments = new ArrayList<ShapeDocument>();
        mStrokePropertys = new ArrayList<PropertyConfigStroke>();
        mObjectList = new ArrayList<InsertableObjectBase>();
        CFG.setContext(context);
    }

    @Override
    public void saveShapeResult(boolean bSave) {
        if (bSave) {
            if (mObjectList != null && mObjectList.size() > 0) {
                for (InsertableObjectBase object : mObjectList) {
                    mInternalDoodle.getModelManager().removeInsertableObject(
                            object);
                }
                mInternalDoodle.getModelManager().addInsertableObject(
                        mObjectList);
                mObjectList.clear();
                clearAsusShape();
                mInternalDoodle.getCommandsManager().clearRedo();
            }
        } else {
            mShape.clearStrokes();
            mObjectList.clear();
            mShapeDocuments.clear();
            mStrokePropertys.clear();
        }
    }

    @Override
    public List<InsertableObjectBase> getShapeResult() {
        ShapeResultParser parser = new ShapeResultParser(mShape,
                mShape.getShapeDocument(), mCurrentProperty);
        return parser.getShapeResult();
    }

    @Override
    public List<ShapeDocument> getDocuments() {
        return mShapeDocuments;
    }

    @Override
    public void setShapeDoucument(ShapeDocument document) {
        mShape.setShapeDocument(document);
    }

    @Override
    public void addShapeDoucument(ShapeDocument document) {
        mShapeDocuments.add(document);
    }

    @Override
    public void setConfigProperty(PropertyConfigStroke property) {
        this.mCurrentProperty = property;
    }

    @Override
    public void addConfigProperty(PropertyConfigStroke property) {
        this.mStrokePropertys.add(property);
    }

    @Override
    public List<PropertyConfigStroke> getConfigProperties() {
        return this.mStrokePropertys;
    }

    @Override
    public boolean canDoVO() {
        return CFG.getCanDoVO();
    }

    @Override
    public void initShape() {
        if (mShape != null) {
            mShape.initShapeRecognizer();
            mShape.prepareShapeDocument();
        }
    }

    @Override
    public void clearAsusShape() {
        if (mShape != null) {
            mShape.clearStrokes();
        }
    }

    @Override
    public void deInitShape() {
        if (mShape != null) {
            mObjectList.clear();
            mShapeDocuments.clear();
            mStrokePropertys.clear();
            mShape.clearStrokes();
            mShape.deinitShapeRecognizer();
        }
    }

    @Override
    public void onStrokeReady(InsertableObjectStroke insertableObjectStroke) {
        // mObjectList.clear();
        ShapeRecognizeTask task = new ShapeRecognizeTask(mInternalDoodle,
                insertableObjectStroke, mShape);
        task.execute();
    }

}