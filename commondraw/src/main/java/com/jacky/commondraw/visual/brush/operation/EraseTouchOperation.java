package com.jacky.commondraw.visual.brush.operation;

import android.view.MotionEvent;

import com.jacky.commondraw.manager.commandmanager.AddedCommand;
import com.jacky.commondraw.manager.commandmanager.ICommand;
import com.jacky.commondraw.manager.modelmager.IModelManager;
import com.jacky.commondraw.manager.virsualmanager.IVisualManager;
import com.jacky.commondraw.model.stroke.InsertableObjectStroke;
import com.jacky.commondraw.views.doodleview.FrameCache;
import com.jacky.commondraw.visual.brush.VisualStrokeErase;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class EraseTouchOperation extends StrokeTouchOperation {
    protected VisualStrokeErase mVisualStrokeErase;

    public EraseTouchOperation(FrameCache frameCache,
                               IModelManager modelManager, IVisualManager visualManager,
                               InsertableObjectStroke stroke) {
        super(frameCache, modelManager, visualManager, stroke);
        // TODO Auto-generated constructor stub
        mVisualStrokeErase = (VisualStrokeErase) mVisualStroke;
    }

    @Override
    public ICommand onCreateCommand() {
        // TODO Auto-generated method stub
        ICommand command = null;
        if (mMotionEvent.getActionMasked() == MotionEvent.ACTION_UP
                && mVisualStrokeErase.intersects()) {
            command = new AddedCommand(mStroke, mModelManager);
        }
        return command;
    }

}
