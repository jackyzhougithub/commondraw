package com.jacky.commondraw.views.doodleview.opereation;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.jacky.commondraw.manager.commandmanager.ICommand;
import com.jacky.commondraw.manager.modelmager.IModelManager;
import com.jacky.commondraw.manager.virsualmanager.IVisualManager;
import com.jacky.commondraw.model.InsertableObjectBase;
import com.jacky.commondraw.views.doodleview.FrameCache;
import com.jacky.commondraw.views.doodleview.drawstrategy.AddElementDrawStrategy;
import com.jacky.commondraw.views.doodleview.drawstrategy.DrawStrategy;
import com.jacky.commondraw.visual.VisualElementBase;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class TransformingOperation extends DoodleOperation {
    private VisualElementBase mVisualElement;
    protected InsertableObjectBase mData = null;

    public TransformingOperation(FrameCache frameCache,
                                 IModelManager modelManager, IVisualManager visualManager,
                                 InsertableObjectBase insertableObject) {
        super(frameCache, modelManager, visualManager);
        // TODO Auto-generated constructor stub
        mVisualElement = mVisualManager.getVisualElement(insertableObject);
        mData = insertableObject;
    }

    @Override
    public ICommand onCreateCommand() {
        return null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        DrawStrategy drawStrategy = new AddElementDrawStrategy(canvas, mFrameCache,
                mVisualElement);
        if (drawStrategy != null)
            drawStrategy.draw();
    }

    @Override
    public Rect computerDirty() {
        // TODO Auto-generated method stub
        // final RectF drawRectF = new RectF(mInsertableBitmap.getInitRectF());
        // if (drawRectF.isEmpty()) {
        // return null;
        // }
        // RectF dest = new RectF(drawRectF);
        // Matrix matrix = mInsertableBitmap.getMatrix();
        // if (matrix != null) {
        // matrix.mapRect(dest);
        // }
        // dest.union(drawRectF);
        // Rect rect = new Rect((int) dest.left, (int) dest.top, (int)
        // dest.right,
        // (int) dest.bottom);
        // return rect;
        return null;
    }
}
