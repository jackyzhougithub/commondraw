package com.jacky.commondraw.views.cropimageview;

import com.jacky.commondraw.manager.commandmanager.ICommand;
import com.jacky.commondraw.manager.modelmager.IModelManager;
import com.jacky.commondraw.manager.virsualmanager.IVisualManager;
import com.jacky.commondraw.model.InsertableObjectBase;
import com.jacky.commondraw.views.doodleview.FrameCache;
import com.jacky.commondraw.views.doodleview.opereation.DrawAllOperation;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class CropEndOperation extends DrawAllOperation {
    protected InsertableObjectBase mOldObjectBase;
    protected InsertableObjectBase mNewObjectBase;

    public CropEndOperation(FrameCache frameCache, IModelManager modelManager,
                            IVisualManager visualManager, InsertableObjectBase oldObjectBase,
                            InsertableObjectBase newObjectBase) {
        super(frameCache, modelManager, visualManager);
        // TODO Auto-generated constructor stub
        mOldObjectBase = oldObjectBase;
        mNewObjectBase = newObjectBase;
    }

    @Override
    public ICommand onCreateCommand() {
        // TODO Auto-generated method stub
        return new CropEndCommand(mModelManager, mOldObjectBase, mNewObjectBase);
    }

    public class CropEndCommand implements ICommand {
        protected InsertableObjectBase mOldObjectBase;
        protected InsertableObjectBase mNewObjectBase;

        public CropEndCommand(IModelManager modelManager,
                              InsertableObjectBase oldObjectBase,
                              InsertableObjectBase newObjectBase) {
            // TODO Auto-generated constructor stub
            mOldObjectBase = oldObjectBase;
            mNewObjectBase = newObjectBase;
        }

        @Override
        public InsertableObjectBase getInsertObject() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void undo() {
            // TODO Auto-generated method stub
            mModelManager.removeInsertableObject(mNewObjectBase, true);
            mModelManager.addInsertableObject(mOldObjectBase, true);
        }

        @Override
        public void redo() {
            // TODO Auto-generated method stub
            mModelManager.removeInsertableObject(mOldObjectBase, true);
            mModelManager.addInsertableObject(mNewObjectBase, true);
        }

    }

}
