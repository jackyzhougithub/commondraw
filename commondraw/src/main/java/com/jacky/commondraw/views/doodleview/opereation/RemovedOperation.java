package com.jacky.commondraw.views.doodleview.opereation;

import com.jacky.commondraw.manager.commandmanager.ICommand;
import com.jacky.commondraw.manager.commandmanager.RemovedCommand;
import com.jacky.commondraw.manager.modelmager.IModelManager;
import com.jacky.commondraw.manager.virsualmanager.IVisualManager;
import com.jacky.commondraw.model.InsertableObjectBase;
import com.jacky.commondraw.views.doodleview.FrameCache;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 */
public class RemovedOperation extends DrawAllOperation {
    protected InsertableObjectBase mRemovedObject;

    public RemovedOperation(FrameCache frameCache, IModelManager modelManager,
                            IVisualManager visualManager, InsertableObjectBase removedObject) {
        super(frameCache, modelManager, visualManager);
        // TODO Auto-generated constructor stub
        mRemovedObject = removedObject;
    }

    @Override
    public ICommand onCreateCommand() {
        // TODO Auto-generated method stub
        return new RemovedCommand(mRemovedObject, mModelManager);
        // return null;
    }

}