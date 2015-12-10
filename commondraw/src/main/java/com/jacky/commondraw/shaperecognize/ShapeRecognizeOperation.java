package com.jacky.commondraw.shaperecognize;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.jacky.commondraw.manager.commandmanager.ICommand;
import com.jacky.commondraw.model.InsertableObjectBase;
import com.jacky.commondraw.views.doodleview.IInternalDoodle;
import com.jacky.commondraw.views.doodleview.drawstrategy.DrawStrategy;
import com.jacky.commondraw.views.doodleview.opereation.DoodleOperation;

import java.util.List;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class ShapeRecognizeOperation extends DoodleOperation {
    private List<InsertableObjectBase> mInsertableObjectList;
    private IInternalDoodle mInternalDoodle;
    private int mShapeDocIndex = -1;

    public ShapeRecognizeOperation(IInternalDoodle internalDoodle,
                                   List<InsertableObjectBase> list, int shapeDocIndex) {
        super(internalDoodle.getFrameCache(), internalDoodle.getModelManager(),
                internalDoodle.getVisualManager());
        // TODO Auto-generated constructor stub
        mInsertableObjectList = list;
        mInternalDoodle = internalDoodle;
        mShapeDocIndex = shapeDocIndex;
    }

    @Override
    public ICommand onCreateCommand() {
        if (mShapeDocIndex >= 0) {
            ShapeRecognizeCommand command = new ShapeRecognizeCommand(
                    mInternalDoodle, mInsertableObjectList, mShapeDocIndex);
            return command;
        }
        return null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        DrawStrategy drawStrategy = new DrawStrategyForShape(canvas,
                mInternalDoodle, mInsertableObjectList);
        if (drawStrategy != null)
            drawStrategy.draw();
    }

    @Override
    public Rect computerDirty() {

        return null;
    }

}
