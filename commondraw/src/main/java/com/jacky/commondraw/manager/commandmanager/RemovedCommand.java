package com.jacky.commondraw.manager.commandmanager;

import com.jacky.commondraw.manager.modelmager.IModelManager;
import com.jacky.commondraw.model.InsertableObjectBase;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 * 删除一个对象的时候，对应的Command
 */
public class RemovedCommand implements ICommand {
    // protected DrawStrokeOperation mDrawStrokeOperation;
    // protected DrawTool mDrawTool;
    // protected List<DrawTool> mAllDrawTools;
    protected InsertableObjectBase mInsertableObject;
    protected IModelManager mModelManager;

    public RemovedCommand(InsertableObjectBase insertableObject,
                          IModelManager modelManager) {
        mInsertableObject = insertableObject;
        mModelManager = modelManager;
    }

    @Override
    public void undo() {
        // TODO Auto-generated method stub
        mModelManager.addInsertableObject(mInsertableObject, true);
    }

    @Override
    public void redo() {
        // TODO Auto-generated method stub
        mModelManager.removeInsertableObject(mInsertableObject, true);
        // mDrawStrokeOperation.draw();
    }

    @Override
    public InsertableObjectBase getInsertObject() {
        // TODO Auto-generated method stub
        return mInsertableObject;
    }

}
