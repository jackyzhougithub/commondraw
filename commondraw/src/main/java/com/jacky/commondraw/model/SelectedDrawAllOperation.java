package com.jacky.commondraw.model;

import android.graphics.Canvas;
import android.util.Log;

import com.jacky.commondraw.manager.modelmager.IModelManager;
import com.jacky.commondraw.manager.virsualmanager.IVisualManager;
import com.jacky.commondraw.views.doodleview.FrameCache;
import com.jacky.commondraw.views.doodleview.drawstrategy.RedrawStrategy;
import com.jacky.commondraw.views.doodleview.opereation.DrawAllOperation;
import com.jacky.commondraw.visual.VisualElementBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 * 重新绘制当前的所有对象.SelectedDrawAllOperation不对应Command
 * 该操作重绘所有的，与DrawAllOperation不同的是，该操作将选中的InsertableObject置于最前面
 */
public class SelectedDrawAllOperation extends DrawAllOperation {
    protected InsertableObjectBase mSelectedInsertableObject;

    public SelectedDrawAllOperation(FrameCache frameCache,
                                    IModelManager modelManager, IVisualManager visualManager,
                                    InsertableObjectBase selectedInsertableObject) {
        super(frameCache, modelManager, visualManager);
        // TODO Auto-generated constructor stub
        mSelectedInsertableObject = selectedInsertableObject;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        SelectedDrawStrategy drawStrategy = new SelectedDrawStrategy(canvas,
                mFrameCache, null, mModelManager.getInsertableObjectList(),
                mVisualManager);
        drawStrategy.draw();
    }

    class SelectedDrawStrategy extends RedrawStrategy {

        public SelectedDrawStrategy(Canvas canvas, FrameCache frameCache,
                                    VisualElementBase visualElement,
                                    List<InsertableObjectBase> list, IVisualManager visualManager) {
            super(canvas, frameCache, visualElement, list, visualManager);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void draw() {
            // TODO Auto-generated method stub
            // mViewCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            updateCache();
            drawCache();
            // 将选中元素绘制在最前面
            drawWholeVisualElement(mViewCanvas,
                    mVisualManager.getVisualElement(mSelectedInsertableObject));
        }

        /**
         * 擦出所有的，然后重绘。该算法可优化
         */
        @Override
        protected void updateCache() {
            // TODO Auto-generated method stub
            if (mInsertableObjectList == null)
                return;
            Log.i(TAG, "updateCache");
            mFrameCache.clearBitmap();
            Canvas canvas = mFrameCache.getCanvas();
            List<InsertableObjectBase> list = new ArrayList<InsertableObjectBase>(
                    mInsertableObjectList);// 复制一份新数据，否则会出异常
            list.remove(mSelectedInsertableObject);
            for (InsertableObjectBase object : list) {
                drawWholeVisualElement(canvas,
                        mVisualManager.getVisualElement(object));
            }
        }

    }

}

