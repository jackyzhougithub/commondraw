package com.jacky.commondraw.manager.commandmanager;

import android.graphics.Matrix;

import com.jacky.commondraw.model.InsertableObjectBase;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 * 旋转平移缩放操作结束的时候，对应的Icommand
 */
public class TransformEndCommand implements ICommand {

    private InsertableObjectBase mData = null;
    protected Matrix mUndoMatrix = null;
    protected Matrix mRedoMatrix = null;

    public TransformEndCommand(InsertableObjectBase data, Matrix oldMatrix) {
        mData = data;
        mUndoMatrix = oldMatrix;
    }

    @Override
    public InsertableObjectBase getInsertObject() {
        // TODO Auto-generated method stub
        return mData;
    }

    @Override
    public void undo() {
        // TODO Auto-generated method stub
        mRedoMatrix = new Matrix(mData.getMatrix());
        if (mUndoMatrix != null) {
            mData.setMatrix(new Matrix(mUndoMatrix), true);
        }

    }

    @Override
    public void redo() {
        // TODO Auto-generated method stub
        mUndoMatrix = new Matrix(mData.getMatrix());
        if (mUndoMatrix != null) {
            mData.setMatrix(new Matrix(mRedoMatrix), true);
        }
    }

}
