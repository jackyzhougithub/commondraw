package com.jacky.commondraw.views.doodleview.opereation;

import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;

import com.jacky.commondraw.manager.commandmanager.ICommand;
import com.jacky.commondraw.manager.modelmager.IModelManager;
import com.jacky.commondraw.manager.virsualmanager.IVisualManager;
import com.jacky.commondraw.model.stroke.InsertableObjectStroke;
import com.jacky.commondraw.views.doodleview.FrameCache;
import com.jacky.commondraw.views.doodleview.IInternalDoodle;
import com.jacky.commondraw.views.doodleview.drawstrategy.DrawStrategy;
import com.jacky.commondraw.visual.brush.HWPoint;

import java.util.List;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 * 图形识别模式下，跟用户交互的时候，绘制分段画笔的操作。
 * 如果从其他地方加载数据，绘制stroke，不能使用此类。
 */
public class SRSegmentStrokeTouchOperation extends SegmentStrokeTouchOperation {

    public SRSegmentStrokeTouchOperation(FrameCache frameCache,
                                         IModelManager modelManager, IVisualManager visualManager,
                                         InsertableObjectStroke stroke, List<HWPoint> onTimeDrawList,
                                         List<HWPoint> hwPoints, IInternalDoodle internalDoodle) {
        super(frameCache, modelManager, visualManager, stroke, onTimeDrawList,
                hwPoints, internalDoodle);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        /**
         * 先framecache->segmentFrameCache;
         */
        if (mMotionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
            Log.i(TAG, "onDraw,ACTION_DOWN");
            drawBitmap(mInternalDoodle.getTempFrameCache().getCanvas(),
                    mInternalDoodle.getFrameCache().getBitmap());
        }

        DrawStrategy drawStrategy = createDrawStrategy(canvas, null);
        if (drawStrategy != null)
            drawStrategy.draw();

        if (mMotionEvent.getActionMasked() == MotionEvent.ACTION_UP) {// 分段画笔，up的时候，需要把segmentFrameCache绘制到framenCache上
            // mInternalDoodle.getSegmentFrameCache().clearBitmap();//
            // 清空绘制，留给下一次用
            // 奇怪，这里清空后，显示居然不正常。why?
        }
    }

    @Override
    public ICommand onCreateCommand() {
        // TODO Auto-generated method stub
        return null;
    }

}

