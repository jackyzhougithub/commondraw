package com.jacky.commondraw.views.doodleview.opereation;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.jacky.commondraw.manager.commandmanager.ICommand;
import com.jacky.commondraw.manager.modelmager.IModelManager;
import com.jacky.commondraw.manager.virsualmanager.IVisualManager;
import com.jacky.commondraw.views.doodleview.FrameCache;
import com.jacky.commondraw.views.doodleview.drawstrategy.RedrawStrategy;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 */
public class DrawAllOperation extends DoodleOperation {

    public DrawAllOperation(FrameCache frameCache, IModelManager modelManager,
                            IVisualManager visualManager) {
        super(frameCache, modelManager, visualManager);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        RedrawStrategy drawStrategy = new RedrawStrategy(canvas, mFrameCache,
                null, mModelManager.getInsertableObjectList(), mVisualManager);
        drawStrategy.draw();
    }

    @Override
    public ICommand onCreateCommand() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Rect computerDirty() {
        // TODO Auto-generated method stub
        return null;
    }
}
