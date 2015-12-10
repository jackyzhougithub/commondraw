package com.jacky.commondraw.views.doodleview.drawstrategy;

import android.graphics.Canvas;
import android.util.Log;

import com.jacky.commondraw.manager.virsualmanager.IVisualManager;
import com.jacky.commondraw.model.InsertableObjectBase;
import com.jacky.commondraw.views.doodleview.FrameCache;
import com.jacky.commondraw.visual.VisualElementBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 */
public class RedrawStrategy extends DrawStrategy {

    private static final String TAG = "RedrawStrategy";
    protected List<InsertableObjectBase> mInsertableObjectList;
    protected IVisualManager mVisualManager;

    public RedrawStrategy(Canvas canvas, FrameCache frameCache,
                         VisualElementBase visualElement, List<InsertableObjectBase> list,
                         IVisualManager visualManager) {
        super(canvas, frameCache, visualElement);
        mInsertableObjectList = list;
        // TODO Auto-generated constructor stub
        mVisualManager = visualManager;
    }

    @Override
    public void draw() {
        // TODO Auto-generated method stub
        // mViewCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        updateCache();
        drawCache();
    }

    /**
     * 擦出所有的，然后重绘。该算法可优化
     */
    @Override
    protected void updateCache() {
        // TODO Auto-generated method stub
        if (mInsertableObjectList == null || mFrameCache == null)
            return;
        Log.i(TAG, "updateCache");
        mFrameCache.clearBitmap();
        Canvas canvas = mFrameCache.getCanvas();
        List<InsertableObjectBase> list = new ArrayList<InsertableObjectBase>(
                mInsertableObjectList);// 复制一份新数据，否则会出异常
        for (InsertableObjectBase object : list) {
            try {
                drawWholeVisualElement(canvas,
                        mVisualManager.getVisualElement(object));
            } catch (Exception e) {
                // TODO: handle exception
            }

        }
    }

}
