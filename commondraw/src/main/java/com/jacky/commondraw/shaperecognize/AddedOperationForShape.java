package com.jacky.commondraw.shaperecognize;

import com.jacky.commondraw.manager.commandmanager.ICommand;
import com.jacky.commondraw.manager.modelmager.IModelManager;
import com.jacky.commondraw.manager.virsualmanager.IVisualManager;
import com.jacky.commondraw.model.InsertableObjectBase;
import com.jacky.commondraw.views.doodleview.FrameCache;
import com.jacky.commondraw.views.doodleview.opereation.AddedOperation;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class AddedOperationForShape extends AddedOperation {
    public AddedOperationForShape(FrameCache frameCache,
                                  IModelManager modelManager, IVisualManager visualManager,
                                  InsertableObjectBase insertableObject) {
        super(frameCache, modelManager, visualManager, insertableObject);
    }

    @Override
    public ICommand onCreateCommand() {
        return null;
    }
}

