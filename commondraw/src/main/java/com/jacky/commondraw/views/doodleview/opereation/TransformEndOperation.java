package com.jacky.commondraw.views.doodleview.opereation;

import android.graphics.Matrix;

import com.jacky.commondraw.manager.commandmanager.ICommand;
import com.jacky.commondraw.manager.commandmanager.TransformEndCommand;
import com.jacky.commondraw.manager.modelmager.IModelManager;
import com.jacky.commondraw.manager.virsualmanager.IVisualManager;
import com.jacky.commondraw.model.InsertableObjectBase;
import com.jacky.commondraw.views.doodleview.FrameCache;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class TransformEndOperation extends TransformingOperation {
    protected Matrix mOldMatrix = null;

    public TransformEndOperation(FrameCache frameCache,
                                 IModelManager modelManager, IVisualManager visualManager,
                                 InsertableObjectBase insertableObject, Matrix oldMatrix) {
        super(frameCache, modelManager, visualManager, insertableObject);
        // TODO Auto-generated constructor stub
        mOldMatrix = oldMatrix;
    }

    @Override
    public ICommand onCreateCommand() {
        // TODO Auto-generated method stub
        return new TransformEndCommand(mData, mOldMatrix);
    }
}