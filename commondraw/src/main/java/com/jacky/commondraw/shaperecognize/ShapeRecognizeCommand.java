package com.jacky.commondraw.shaperecognize;

import com.jacky.commondraw.manager.commandmanager.ICommand;
import com.jacky.commondraw.model.InsertableObjectBase;
import com.jacky.commondraw.model.propertyconfig.PropertyConfigStroke;
import com.jacky.commondraw.views.doodleview.IInternalDoodle;
import com.jacky.commondraw.views.doodleview.opereation.DoodleOperation;
import com.jacky.commondraw.views.doodleview.opereation.DrawAllOperation;
import com.visionobjects.myscript.shape.ShapeDocument;

import java.util.List;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class ShapeRecognizeCommand implements ICommand {
    private List<InsertableObjectBase> mCurrentObjectList;
    private IInternalDoodle mInternalDoodle;
    private boolean bSaved = false;
    private ShapeDocument mShapeDocument = null;
    private PropertyConfigStroke mProperty;
    private int mShapeDocIndex = -1;

    public ShapeRecognizeCommand(IInternalDoodle internalDoodle,
                                 List<InsertableObjectBase> list, int shapeDocIndex) {
        mCurrentObjectList = list;
        mInternalDoodle = internalDoodle;
        mShapeDocIndex = shapeDocIndex;
    }

    @Override
    public void undo() {
        // TODO Auto-generated method stub
        if (mShapeDocIndex < 0)
            return;
        else {
            List<InsertableObjectBase> savedList = mInternalDoodle
                    .getModelManager().getInsertableObjectList();
            if (savedList.containsAll(mCurrentObjectList)) {
                bSaved = true;
                mInternalDoodle.getModelManager().removeInsertableObject(
                        mCurrentObjectList);
            }
            if (mShapeDocIndex == 0) {
                mInternalDoodle.getShapeRecognizeManager().clearAsusShape();
                ShapeRecognizeManager.mObjectList.clear();

                DoodleOperation operation = new DrawAllOperation(
                        mInternalDoodle.getFrameCache(),
                        mInternalDoodle.getModelManager(),
                        mInternalDoodle.getVisualManager());
                mInternalDoodle.insertOperation(operation);
            } else {
                ShapeDocument shapeDocument = mInternalDoodle
                        .getShapeRecognizeManager().getDocuments()
                        .get(mShapeDocIndex - 1);
                mInternalDoodle.getShapeRecognizeManager().setShapeDoucument(
                        shapeDocument);

                PropertyConfigStroke property = mInternalDoodle
                        .getShapeRecognizeManager().getConfigProperties()
                        .get(mShapeDocIndex - 1);
                mInternalDoodle.getShapeRecognizeManager().setConfigProperty(
                        property);

                List<InsertableObjectBase> objects = mInternalDoodle
                        .getShapeRecognizeManager().getShapeResult();
                DoodleOperation operation = new UndoShapeOperation(
                        mInternalDoodle, objects, mShapeDocIndex);
                mInternalDoodle.insertOperation(operation);

                ShapeRecognizeManager.mObjectList.clear();
                ShapeRecognizeManager.mObjectList.addAll(objects);
            }
            mShapeDocument = mInternalDoodle.getShapeRecognizeManager()
                    .getDocuments().get(mShapeDocIndex);
            mInternalDoodle.getShapeRecognizeManager().getDocuments()
                    .remove(mShapeDocIndex);

            mProperty = mInternalDoodle.getShapeRecognizeManager()
                    .getConfigProperties().get(mShapeDocIndex);
            mInternalDoodle.getShapeRecognizeManager().getConfigProperties()
                    .remove(mShapeDocIndex);
        }
    }

    @Override
    public void redo() {
        // TODO Auto-generated method stub
        if (mShapeDocIndex < 0)
            return;
        else {
            if (bSaved) {
                DoodleOperation operation = new DrawAllOperation(
                        mInternalDoodle.getFrameCache(),
                        mInternalDoodle.getModelManager(),
                        mInternalDoodle.getVisualManager());
                mInternalDoodle.insertOperation(operation);

                mInternalDoodle.getModelManager().addInsertableObject(
                        mCurrentObjectList);
                ShapeRecognizeManager.mObjectList.clear();

                mInternalDoodle.getShapeRecognizeManager().clearAsusShape(); // 对于之前保存过的数据，redo成功后将不再与接下来的识别结果有关联行为
                mInternalDoodle.getShapeRecognizeManager().addShapeDoucument(
                        mShapeDocument);
                mInternalDoodle.getShapeRecognizeManager().addConfigProperty(
                        mProperty);
            } else {
                DoodleOperation operation = new ShapeRecognizeOperation(
                        mInternalDoodle, mCurrentObjectList, mShapeDocIndex);
                operation.setCreatingCommand(false);
                mInternalDoodle.insertOperation(operation);
                ShapeRecognizeManager.mObjectList.clear();
                ShapeRecognizeManager.mObjectList.addAll(mCurrentObjectList);

                mInternalDoodle.getShapeRecognizeManager().addShapeDoucument(
                        mShapeDocument);
                ShapeDocument shapeDocument = mInternalDoodle
                        .getShapeRecognizeManager().getDocuments()
                        .get(mShapeDocIndex);
                mInternalDoodle.getShapeRecognizeManager().setShapeDoucument(
                        shapeDocument);

                mInternalDoodle.getShapeRecognizeManager().addConfigProperty(
                        mProperty);
                PropertyConfigStroke property = mInternalDoodle
                        .getShapeRecognizeManager().getConfigProperties()
                        .get(mShapeDocIndex);
                mInternalDoodle.getShapeRecognizeManager().setConfigProperty(
                        property);
            }

        }
    }

    @Override
    public InsertableObjectBase getInsertObject() {
        // TODO Auto-generated method stub
        return null;
    }
}