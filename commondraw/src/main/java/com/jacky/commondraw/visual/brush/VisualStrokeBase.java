package com.jacky.commondraw.visual.brush;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

import com.jacky.commondraw.model.InsertableObjectBase;
import com.jacky.commondraw.model.stroke.InsertableObjectStroke;
import com.jacky.commondraw.model.stroke.StylusPoint;
import com.jacky.commondraw.views.doodleview.IInternalDoodle;
import com.jacky.commondraw.visual.VisualElementBase;
import com.jacky.commondraw.visual.brush.operation.SRStrokeTouchOperationOperation;
import com.jacky.commondraw.visual.brush.operation.StrokeTouchOperation;

import java.util.List;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 * 定义Stoke的可视化基类。
 */
public abstract class VisualStrokeBase extends VisualElementBase {
    public static final String TAG = "VisualStrokeBase";
    protected InsertableObjectStroke mInsertableObjectStroke;
    protected Paint mPaint;
    /***
     * 实事绘制时候的脏区域 。对于不分段绘制的画笔来说，该区域等于笔画的可绘制区域 对于分段绘制的画笔来说,该区域等于正在绘制的分段区域
     */
    protected Rect mDirtyRect = null;

    /**
     *
     * @param context
     * @param internalDoodle
     * @param object
     */
    public VisualStrokeBase(Context context, IInternalDoodle internalDoodle,
                            InsertableObjectBase object) {
        super(context, internalDoodle, object);
        mInsertableObjectStroke = (InsertableObjectStroke) object;
        mPaint = new Paint();
        updatePaint();
    }

    protected void updatePaint() {
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(mInsertableObjectStroke.getColor());
        mPaint.setStrokeWidth(mInsertableObjectStroke.getStrokeWidth());
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setPathEffect(null);
        mPaint.setAlpha(0xFF);
    }

    @Override
    public void onPropertyValeChanged(InsertableObjectBase insertableObject,
                                      int propertyId, Object oldValue, Object newValue,
                                      boolean fromUndoRedo) {
        // TODO Auto-generated method stub
        super.onPropertyValeChanged(insertableObject, propertyId, oldValue,
                newValue, fromUndoRedo);
        switch (propertyId) {
            case InsertableObjectStroke.PROPERTY_ID_STROKE_COLOR:
                mPaint.setColor(mInsertableObjectStroke.getColor());
                break;
            case InsertableObjectStroke.PROPERTY_ID_STROKE_WIDTH:
                mPaint.setStrokeWidth(mInsertableObjectStroke.getStrokeWidth());
            default:
                break;
        }
    }

    // / Process when the motion is touch or pen down
    abstract public void onDown(MotionElement mElement);

    abstract public void onMove(MotionElement mElement);

    abstract public void onUp(MotionElement mElement);

    abstract public void initFloatPoints(float[] fpoints, boolean isWithPressure);

    // / Special draw operation for preview
    public void drawPreview(Canvas canvas) {
        this.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        // event会被下一次事件重用，这里必须生成新的，否则会有问题
        MotionEvent event2 = MotionEvent.obtain(event);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "undo,ACTION_DOWN");
                onDown(createMotionElement(event2));
                sendTouchOperation(event2);
                return true;
            case MotionEvent.ACTION_MOVE:
                onMove(createMotionElement(event2));
                sendTouchOperation(event2);
                return true;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "undo,ACTION_UP");
                onUp(createMotionElement(event2));
                sendTouchOperation(event2);
                mInsertableObjectStroke.setPoints(getPoints());// up的时候，给模型层设置数据
                mInsertableObjectStroke.setInitRectF(getBounds());
                return true;
            default:
                break;
        }
        return super.onTouchEvent(event2);
    }

    protected void sendTouchOperation(MotionEvent motionEvent) {
        // TODO Auto-generated method stub
        if (mInternalDoodle.isShapeRecognition()) {
            SRStrokeTouchOperationOperation operation = new SRStrokeTouchOperationOperation(
                    mInternalDoodle.getFrameCache(),
                    mInternalDoodle.getModelManager(),
                    mInternalDoodle.getVisualManager(), mInsertableObjectStroke);
            operation.setMotionEvent(motionEvent);
            sendOperation(operation);
        } else {
            StrokeTouchOperation operation = new StrokeTouchOperation(
                    mInternalDoodle.getFrameCache(),
                    mInternalDoodle.getModelManager(),
                    mInternalDoodle.getVisualManager(), mInsertableObjectStroke);
            operation.setMotionEvent(motionEvent);
            sendOperation(operation);
        }
    }

    @Override
    public abstract void draw(Canvas canvas);

    abstract public List<StylusPoint> getPoints();

    /**
     * 获得脏区域 对于不分段绘制的画笔来说，该区域等于笔画的整个区域 对于分段绘制的画笔来说,该区域等于正在绘制的分段区域
     *
     * @return
     */
    public Rect getDirtyRect() {
        return mDirtyRect;
    }

    /***
     * 获得整个区域
     *
     * @return
     */
    @Override
    public RectF getBounds() {
        RectF bounds = addStrokeToBounds(getStrictBounds());
        return bounds;
    }

    protected RectF addStrokeToBounds(RectF bounds) {
        float strokeWidth = mPaint.getStrokeWidth();
        RectF result = new RectF(bounds);
        result.left -= strokeWidth;
        result.top -= strokeWidth;
        result.right += strokeWidth;
        result.bottom += strokeWidth;
        return result;
    }

    abstract protected RectF getStrictBounds();

    /**
     * 该stroke是否分段绘制。对于一些算法比较复杂，花费时间较多的Stroke，如毛笔，绘制的时候是分段绘制的
     *
     * @return
     */
    public boolean isSegmentDraw() {
        return false;
    }

    public MotionElement createMotionElement(MotionEvent motionEvent) {
        float[] xArray = new float[] { motionEvent.getX(0) };
        float[] yArray = new float[] { motionEvent.getY(0) };
        MotionElement motionElement = new MotionElement(xArray[0], yArray[0],
                motionEvent.getPressure(), motionEvent.getToolType(0),
                motionEvent.getEventTime());
        return motionElement;
    }

    public static class MotionElement {

        public float x;
        public float y;
        public float pressure;
        public int tooltype;
        public long timestamp;

        public MotionElement(float mx, float my, float mp, int ttype, long mt) {
            x = mx;
            y = my;
            pressure = mp;
            tooltype = ttype;
            timestamp = mt;
        }

    }
}

