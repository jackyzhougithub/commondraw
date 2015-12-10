package com.jacky.commondraw.visual.brush.operation;

import android.graphics.Canvas;

import com.jacky.commondraw.manager.commandmanager.ICommand;
import com.jacky.commondraw.manager.modelmager.IModelManager;
import com.jacky.commondraw.manager.virsualmanager.IVisualManager;
import com.jacky.commondraw.model.stroke.InsertableObjectStroke;
import com.jacky.commondraw.views.doodleview.FrameCache;
import com.jacky.commondraw.views.doodleview.drawstrategy.DrawStrategy;
import com.jacky.commondraw.views.doodleview.drawstrategy.EditDrawStrategy;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 * 图形识别模式下，跟用户交互的时候，绘制非分段画笔的操作。
 *
 */
public class SRStrokeTouchOperationOperation extends StrokeTouchOperation {

    public SRStrokeTouchOperationOperation(FrameCache frameCache,
                                           IModelManager modelManager, IVisualManager visualManager,
                                           InsertableObjectStroke stroke) {
        super(frameCache, modelManager, visualManager, stroke);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected DrawStrategy createDrawStrategy(Canvas canvas,
                                              FrameCache frameCache) {
        // TODO Auto-generated method stub
        DrawStrategy drawStrategy = null;
        drawStrategy = new EditDrawStrategy(canvas, frameCache, mVisualStroke);
        return drawStrategy;
    }

    @Override
    public ICommand onCreateCommand() {
        // TODO Auto-generated method stub
        return null;
    }

}