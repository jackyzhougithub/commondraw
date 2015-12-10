package com.jacky.commondraw.shaperecognize;

import android.graphics.Canvas;

import com.jacky.commondraw.model.InsertableObjectBase;
import com.jacky.commondraw.views.doodleview.IInternalDoodle;
import com.jacky.commondraw.views.doodleview.drawstrategy.DrawStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 * 图形识别策略
 */
public class DrawStrategyForShape extends DrawStrategy {
    List<InsertableObjectBase> mInsertableObjectList;
    IInternalDoodle mInternalDoodle;

    public DrawStrategyForShape(Canvas canvas, IInternalDoodle internalDoodle,
                                List<InsertableObjectBase> list) {
        super(canvas, internalDoodle.getFrameCache(), null);
        mInsertableObjectList = list;
        mInternalDoodle = internalDoodle;
    }

    @Override
    public void draw() {
        if (mInsertableObjectList == null || mInsertableObjectList.size() <= 0)
            return;
        mFrameCache.clearBitmap();
        drawAllToCache(mFrameCache.getCanvas());// 对于图形识别来说，只能重绘，没有更好的办法？
        updateCache();
        drawCache();
    }

    void drawAllToCache(Canvas canvas) {
        List<InsertableObjectBase> list = mInternalDoodle.getModelManager()
                .getInsertableObjectList();
        for (InsertableObjectBase object : list) {
            drawWholeVisualElement(canvas, mInternalDoodle.getVisualManager()
                    .getVisualElement(object));
        }
    }

    @Override
    protected void updateCache() {
        // TODO Auto-generated method stub
        List<InsertableObjectBase> list = new ArrayList<InsertableObjectBase>(
                mInsertableObjectList);
        for (InsertableObjectBase object : list) {
            drawWholeVisualElement(mFrameCache.getCanvas(), mInternalDoodle
                    .getVisualManager().getVisualElement(object));
        }
    }
}