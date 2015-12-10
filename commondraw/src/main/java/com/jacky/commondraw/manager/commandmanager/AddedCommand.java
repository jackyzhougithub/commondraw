package com.jacky.commondraw.manager.commandmanager;

import com.jacky.commondraw.manager.modelmager.IModelManager;
import com.jacky.commondraw.model.InsertableObjectBase;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 * 新增一个对象的时候，对应的Command
 */
public class AddedCommand implements ICommand {
    // protected DrawStrokeOperation mDrawStrokeOperation;
    // protected DrawTool mDrawTool;
    // protected List<DrawTool> mAllDrawTools;
    protected InsertableObjectBase mInsertableObject;
    protected IModelManager mModelManager;

    // public DrawStrokeCommand(DrawStrokeOperation operation,
    // List<DrawTool> allDrawTools) {
    // mDrawStrokeOperation = operation;
    // mDrawTool = operation.getDrawTool();
    // mAllDrawTools = allDrawTools;
    // }
    public AddedCommand(InsertableObjectBase insertableObject,
                        IModelManager modelManager) {
        mInsertableObject = insertableObject;
        mModelManager = modelManager;
    }

    @Override
    public void undo() {
        // TODO Auto-generated method stub
        mModelManager.removeInsertableObject(mInsertableObject, true);
    }

    @Override
    public void redo() {
        // TODO Auto-generated method stub
        mModelManager.addInsertableObject(mInsertableObject, true);
        // mDrawStrokeOperation.draw();
    }

    @Override
    public InsertableObjectBase getInsertObject() {
        // TODO Auto-generated method stub
        return mInsertableObject;
    }

}