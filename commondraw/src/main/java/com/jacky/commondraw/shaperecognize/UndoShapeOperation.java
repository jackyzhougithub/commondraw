package com.jacky.commondraw.shaperecognize;

import com.jacky.commondraw.manager.commandmanager.ICommand;
import com.jacky.commondraw.model.InsertableObjectBase;
import com.jacky.commondraw.views.doodleview.IInternalDoodle;

import java.util.List;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class UndoShapeOperation extends ShapeRecognizeOperation {
    public UndoShapeOperation(IInternalDoodle internalDoodle,
                              List<InsertableObjectBase> list, int shapeDocIndex) {
        super(internalDoodle, list, shapeDocIndex);
        // TODO Auto-generated constructor stub
    }

    @Override
    public ICommand onCreateCommand() {
        return null;
    }

}