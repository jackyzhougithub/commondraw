package com.jacky.commondraw.manager.modelmager;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Handler;
import android.view.MotionEvent;

import com.jacky.commondraw.listeners.IClickedListener;
import com.jacky.commondraw.listeners.IIsertableObjectListener;
import com.jacky.commondraw.listeners.IObjectUnSelectedListener;
import com.jacky.commondraw.listeners.ISelectedChangedListener;
import com.jacky.commondraw.listeners.IStrokeReadyListener;
import com.jacky.commondraw.listeners.ITouchEventListener;
import com.jacky.commondraw.listeners.ITransformChangedListener;
import com.jacky.commondraw.manager.virsualmanager.VisualManagerImpl;
import com.jacky.commondraw.model.InsertableBitmap;
import com.jacky.commondraw.model.InsertableObjectBase;
import com.jacky.commondraw.model.SelectedDrawAllOperation;
import com.jacky.commondraw.model.UnSelectedDrawAllOperation;
import com.jacky.commondraw.model.stroke.InsertableObjectStroke;
import com.jacky.commondraw.shaperecognize.InsertableObjectShape;
import com.jacky.commondraw.views.doodleview.DoodleEnum;
import com.jacky.commondraw.views.doodleview.IInternalDoodle;
import com.jacky.commondraw.views.doodleview.opereation.TransformEndOperation;
import com.jacky.commondraw.views.selectview.ILayer;
import com.jacky.commondraw.views.selectview.ISelectView;
import com.jacky.commondraw.views.selectview.ITransformChanged;
import com.jacky.commondraw.views.selectview.SelectViewEnum;
import com.jacky.commondraw.views.selectview.SelectViewFactory;
import com.jacky.commondraw.visual.VisualElementBase;
import com.jacky.commondraw.visual.brush.VisualStrokeErase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 * 实现Model的管理类
 * Model管理类，具备的功能:
 * 1.提供增删改查数据层的接口；
 * 2.处理并分发相关事件,如touch事件;
 * 3.向VisualModel发通知
 */
public class ModelManager implements IModelManager,
        LongClickDetector.OnLongClickListener {
    public static final String TAG = "ModelManager";

    private List<InsertableObjectBase> mInsertableObjects;
    private List<ISelectedChangedListener> mSeletedListeners;
    private List<IIsertableObjectListener> mIsertableObjectListeners;
    private List<ITouchEventListener> mTouchEventListeners;
    private List<IStrokeReadyListener> mStrokeReadyListeners;
    private IInternalDoodle mIInternalDoodle;

    private LongClickDetector mLongClickDetector = null;
    /***
     * 当前正在操作的InsertableObjectBase
     */
    private InsertableObjectBase mActingInsertableObject;
    /**
     * 被选中对象的初始Matrix
     */
    private Matrix mSelectedOldMatrix = new Matrix();

    private ISelectView<InsertableObjectBase> mSelectView;

    private Handler mHandler = new Handler();

    private boolean mIsObjectSelected = false;

    // private ISelectView<InsertableObjectBase> mSelectView;

    public ModelManager(IInternalDoodle internalDoodle) {
        mIInternalDoodle = internalDoodle;
        mInsertableObjects = new ArrayList<InsertableObjectBase>();
        mSeletedListeners = new ArrayList<ISelectedChangedListener>();
        mIsertableObjectListeners = new ArrayList<IIsertableObjectListener>();
        mTouchEventListeners = new ArrayList<ITouchEventListener>();
        mStrokeReadyListeners = new ArrayList<IStrokeReadyListener>();

        mLongClickDetector = new LongClickDetector();
        mLongClickDetector.addLongClickListener(this);
    }

    /*********************************************************************
     * IModelManager实现开始
     *********************************************************************/
    // @Override
    // public InsertableObjectBase getActingInsertableObject() {
    // // TODO Auto-generated method stub
    // return mActingInsertableObject;
    // }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (mIInternalDoodle.getSelectionMode() == DoodleEnum.SelectionMode.SELECTION) {
            return handleSelectionMode(event);
        } else {
            return handleNoneMode(event);
        }
    }

    private boolean handleNoneMode(MotionEvent event) {
        mLongClickDetector.onTouch(MotionEvent.obtain(event));
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                int strokeType = InsertableObjectStroke.STROKE_TYPE_NORMAL;
                if (mIInternalDoodle.getInputMode() == DoodleEnum.InputMode.DRAW) {
                    strokeType = mIInternalDoodle.getStrokeType();
                } else if (mIInternalDoodle.getInputMode() == DoodleEnum.InputMode.ERASE) {
                    strokeType = InsertableObjectStroke.STROKE_TYPE_ERASER;
                }
                InsertableObjectStroke stroke = InsertableObjectStroke
                        .newInsertableObjectStroke(strokeType);
                mActingInsertableObject = stroke;
                // addInsertableObject(stroke);
                break;
            default:
                break;
        }

        boolean b = false;
        for (ITouchEventListener listener : mTouchEventListeners) {
            if (listener.onTouchEvent(event, mActingInsertableObject)) {
                b = true;
                break;
            }
        }

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_UP:
                if (mActingInsertableObject != null) {
                    if (!(mActingInsertableObject instanceof InsertableObjectStroke))
                        break;
                    InsertableObjectStroke stroke = (InsertableObjectStroke) mActingInsertableObject;
                    if (stroke.getStrokeType() == InsertableObjectStroke.STROKE_TYPE_ERASER) {// 橡皮擦不参与图形识别
                        VisualElementBase visualElement = mIInternalDoodle
                                .getVisualManager().getVisualElement(
                                        mActingInsertableObject);
                        VisualStrokeErase visualStrokeEarse = (VisualStrokeErase) visualElement;
                        if (visualStrokeEarse.intersects()) {
                            addInsertableObjectInternal(mActingInsertableObject,
                                    false, false);
                        }
                    } else {

                        if (mIInternalDoodle.isShapeRecognition()) {// 图像识别
                            fireStrokeReady(stroke);
                            for (IIsertableObjectListener listener : mIsertableObjectListeners) {
                                if (!(listener instanceof VisualManagerImpl)) {
                                    listener.onRecognizeAdd();
                                }
                            }
                        } else {
                            addInsertableObjectInternal(mActingInsertableObject,
                                    false, false);
                        }
                    }
                }
                mActingInsertableObject = null;
                break;
            default:
                break;
        }
        return b;
    }

    private boolean handleSelectionMode(MotionEvent event) {
        mLongClickDetector.onTouch(MotionEvent.obtain(event));
        return true;
    }

    @Override
    public void addInsertableObject(InsertableObjectBase object) {
        // TODO Auto-generated method stub
        addInsertableObject(object, false);
    }

    public void addInsertableObject(InsertableObjectBase object,
                                    boolean fromUndoRedo) {
        if (object == null)
            return;
        addInsertableObjectInternal(object, true, fromUndoRedo);
    }

    protected void addInsertableObjectInternal(InsertableObjectBase object,
                                               boolean notifyVisualManager, boolean fromUndoRedo) {
        if (object == null)
            return;
        mActingInsertableObject = object;
        if (!mInsertableObjects.add(object)) {
            mActingInsertableObject = null;
            return;
        }
        List<InsertableObjectBase> list = new ArrayList<InsertableObjectBase>();
        list.add(object);
        fireInsertableObjectAdded(list, notifyVisualManager, fromUndoRedo);
    }

    @Override
    public void removeInsertableObject(InsertableObjectBase object) {
        // TODO Auto-generated method stub
        removeInsertableObject(object, false);
    }

    @Override
    public void removeInsertableObject(InsertableObjectBase object,
                                       boolean fromUndoRedo) {
        // TODO Auto-generated method stub
        if (object != null) {
            if (mInsertableObjects.remove(object)) {
                List<InsertableObjectBase> list = new ArrayList<InsertableObjectBase>();
                list.add(object);
                fireInsertableObjectDeleted(list, fromUndoRedo);
            }
        }
    }

    @Override
    public void addInsertableObject(List<InsertableObjectBase> list) {
        // TODO Auto-generated method stub
        addInsertableObject(list, false);
    }

    @Override
    public void addInsertableObject(List<InsertableObjectBase> list,
                                    boolean fromUndoRedo) {
        // TODO Auto-generated method stub
        if (list != null) {
            if (mInsertableObjects.addAll(list)) {
                fireInsertableObjectAdded(list, true, fromUndoRedo);
            }
        }
    }

    @Override
    public void removeInsertableObject(List<InsertableObjectBase> list) {
        // TODO Auto-generated method stub
        removeInsertableObject(list, false);
    }

    @Override
    public void removeInsertableObject(List<InsertableObjectBase> list,
                                       boolean fromUndoRedo) {
        // TODO Auto-generated method stub
        if (list != null) {
            if (mInsertableObjects.removeAll(list)) {
                fireInsertableObjectDeleted(list, fromUndoRedo);
            }
        }
    }

    private void fireStrokeReady(InsertableObjectStroke stroke) {
        for (IStrokeReadyListener listener : mStrokeReadyListeners) {
            listener.onStrokeReady(stroke);
        }
    }

    private void fireInsertableObjectAdded(List<InsertableObjectBase> list,
                                           boolean notifyVisualManager, boolean fromUndoRedo) {
        if (notifyVisualManager) {
            for (IIsertableObjectListener listener : mIsertableObjectListeners) {
                listener.onAdded(list, fromUndoRedo);
            }
        } else {
            for (IIsertableObjectListener listener : mIsertableObjectListeners) {
                if (!(listener instanceof VisualManagerImpl)) {
                    listener.onAdded(list, fromUndoRedo);
                }
            }
        }
    }

    private void fireInsertableObjectDeleted(List<InsertableObjectBase> list,
                                             boolean fromUndoRedo) {
        for (IIsertableObjectListener listener : mIsertableObjectListeners) {
            listener.onRemoved(list, fromUndoRedo);
        }
    }

    private void fireClear() {
        for (IIsertableObjectListener listener : mIsertableObjectListeners) {
            listener.onClear();
        }
    }

    private void fireClearStrokes() {
        for (IIsertableObjectListener listener : mIsertableObjectListeners) {
            listener.onClearStrokes();
        }
    }

    private void fireSelected(InsertableObjectBase insertableObject) {
        mIsObjectSelected = true;
        for (ISelectedChangedListener listener : mSeletedListeners) {
            listener.onSelected(insertableObject);
        }
    }

    private void fireUnSelected(InsertableObjectBase insertableObject) {
        mIsObjectSelected = false;
        for (ISelectedChangedListener listener : mSeletedListeners) {
            listener.onUnSelected(insertableObject);
        }
    }

    public boolean hasObjectSelected() {
        return mIsObjectSelected;
    }

    public InsertableObjectBase getSelectedObject() {
        if (!mIsObjectSelected) {
            return null;
        }
        return mActingInsertableObject;
    }

    @Override
    public void addIsertableObjectListener(IIsertableObjectListener listener) {
        // TODO Auto-generated method stub
        mIsertableObjectListeners.add(listener);
    }

    @Override
    public void removeIsertableObjectListener(IIsertableObjectListener listener) {
        // TODO Auto-generated method stub
        mIsertableObjectListeners.remove(listener);
    }

    @Override
    public void addSeletedChangedListener(ISelectedChangedListener listener) {
        // TODO Auto-generated method stub
        mSeletedListeners.add(listener);
    }

    @Override
    public void removeInsertableObjectSeletedListener(
            ISelectedChangedListener listener) {
        // TODO Auto-generated method stub
        mSeletedListeners.remove(listener);
    }

    @Override
    public void addTouchEventListener(ITouchEventListener listener) {
        // TODO Auto-generated method stub
        mTouchEventListeners.add(listener);
    }

    @Override
    public void removeTouchEventListener(ITouchEventListener listener) {
        // TODO Auto-generated method stub
        mTouchEventListeners.remove(listener);
    }

    public void addStrokeReadyListener(IStrokeReadyListener listener) {
        if (listener != null)
            mStrokeReadyListeners.add(listener);
    }

    public void removeStrokeReadyListener(IStrokeReadyListener listener) {
        if (listener != null)
            mStrokeReadyListeners.remove(listener);
    }

    /*********************************************************************
     * IModelManager实现结束
     *********************************************************************/

    @Override
    public List<InsertableObjectBase> getInsertableObjectList() {
        // TODO Auto-generated method stub
        return mInsertableObjects;
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
        mInsertableObjects.clear();
        fireClear();
    }

    @Override
    public void clearStokes() {
        // TODO Auto-generated method stub
        List<InsertableObjectBase> removedList = new ArrayList<InsertableObjectBase>();
        for (InsertableObjectBase insertableObjectBase : mInsertableObjects) {
            if (insertableObjectBase instanceof InsertableObjectStroke
                    || insertableObjectBase instanceof InsertableObjectShape) {
                removedList.add(insertableObjectBase);
            }
        }
        for (InsertableObjectBase insertableObjectBase : removedList) {
            mInsertableObjects.remove(insertableObjectBase);
        }
        fireClearStrokes();
    }

    /*********************************************************
     * LongClickDetector.OnLongClickListener实现开始
     *********************************************************/

    @Override
    public void onLongClick(MotionEvent event) {
        // TODO Auto-generated method stub
        InsertableObjectBase touchedObject = getTouchedObject(event.getX(),
                event.getY());
        if (touchedObject != null) {
            exitSelectionMode();
        }
        mActingInsertableObject = touchedObject;
        onSelectView(touchedObject);
    }

    protected void onSelectView(InsertableObjectBase object) {
        if (object != null) {
            mSelectedOldMatrix = new Matrix(mActingInsertableObject.getMatrix());
            // 重新绘制所有的，将选中元素绘制在最前面
            SelectedDrawAllOperation operation = new SelectedDrawAllOperation(
                    mIInternalDoodle.getFrameCache(),
                    mIInternalDoodle.getModelManager(),
                    mIInternalDoodle.getVisualManager(),
                    mActingInsertableObject);
            mIInternalDoodle.insertOperation(operation);
            fireSelected(object);
            mIInternalDoodle.setSelectionMode(DoodleEnum.SelectionMode.SELECTION);
            showSelectView();
        }
    }

    private void showSelectView() {
        mSelectView = SelectViewFactory.createSelectView(mIInternalDoodle
                        .getDoodleView().getContext(),
                mIInternalDoodle.getDoodleView(), mActingInsertableObject);
        mSelectView.setOnDeleteListener(new IClickedListener() {

            @Override
            public void onClicked() {
                // TODO Auto-generated method stub
                dismissSelectView();
                if (mActingInsertableObject != null)
                    removeInsertableObject(mActingInsertableObject);
                InsertableObjectBase temp = mActingInsertableObject;
                mActingInsertableObject = null;
                fireUnSelected(temp);
            }
        });
        mSelectView.setTransformChangedListener(new ITransformChanged() {

            @Override
            public void onScaled(Matrix matrix) {
                // TODO Auto-generated method stub
                if (mActingInsertableObject != null) {
                    ITransformChangedListener listener = mActingInsertableObject
                            .createTransformChangedListener();
                    if (listener != null) {
                        listener.onScaled(mActingInsertableObject, matrix);
                    }
                }
            }

            @Override
            public void onTranslate(Matrix matrix) {
                // TODO Auto-generated method stub
                if (mActingInsertableObject != null) {
                    ITransformChangedListener listener = mActingInsertableObject
                            .createTransformChangedListener();
                    if (listener != null) {
                        listener.onTranslate(mActingInsertableObject, matrix);
                    }
                }
            }

            @Override
            public void onRotate(Matrix matrix) {
                // TODO Auto-generated method stub
                if (mActingInsertableObject != null) {
                    ITransformChangedListener listener = mActingInsertableObject
                            .createTransformChangedListener();
                    if (listener != null) {
                        listener.onRotate(mActingInsertableObject, matrix);
                    }
                }
            }

            private Matrix mRecordMatrix = null;

            @Override
            public void onAction(SelectViewEnum.ActionType t, ILayer layer) {
                // TODO Auto-generated method stub
                if (t == SelectViewEnum.ActionType.begin && mActingInsertableObject != null) {
                    mRecordMatrix = new Matrix(mActingInsertableObject
                            .getMatrix());
                }
                if (t == SelectViewEnum.ActionType.end && mActingInsertableObject != null) {
                    if (mActingInsertableObject instanceof InsertableBitmap
                            && mRecordMatrix != null) {
                        TransformEndOperation operation = new TransformEndOperation(
                                mIInternalDoodle.getFrameCache(),
                                mIInternalDoodle.getModelManager(),
                                mIInternalDoodle.getVisualManager(),
                                mActingInsertableObject, mRecordMatrix);
                        mIInternalDoodle.insertOperation(operation);
                    }
                }
            }
        });
        mSelectView
                .setUnSelectedListener(new IObjectUnSelectedListener<InsertableObjectBase>() {

                    @Override
                    public void objectUnSelected(InsertableObjectBase o) {
                        // TODO Auto-generated method stub
                        dismissSelectView();
                        unSelected();
                    }
                });
        mSelectView.showSelectView();
    }

    public void unSelected() {
        InsertableObjectBase temp = mActingInsertableObject;
        if (temp == null || !mIsObjectSelected)
            return;
        mActingInsertableObject = null;
        fireUnSelected(temp);
        Matrix redoMatrix = new Matrix(temp.getMatrix());
        /**
         * 重新绘制所有的，恢复原来的顺序
         */
        UnSelectedDrawAllOperation operation = new UnSelectedDrawAllOperation(
                mIInternalDoodle.getFrameCache(),
                mIInternalDoodle.getModelManager(),
                mIInternalDoodle.getVisualManager(), temp, mSelectedOldMatrix,
                redoMatrix);
        mIInternalDoodle.insertOperation(operation);
    }

    private InsertableObjectBase getTouchedObject(float x, float y) {
        List<InsertableObjectBase> list = new ArrayList<InsertableObjectBase>(
                mInsertableObjects);
        for (int i = list.size() - 1; i >= 0; i--) {
            InsertableObjectBase insertableObject = list.get(i);
            if (!insertableObject.isSelectable())
                continue;
            RectF rectF = insertableObject.getInitRectF();
            if (rectF == null)
                continue;
            RectF rectF2 = new RectF(rectF);
            if (rectF != null && insertableObject.getMatrix() != null) {
                insertableObject.getMatrix().mapRect(rectF2, rectF);
            }
            if (rectF2.contains(x, y)) {
                return insertableObject;
            }
        }
        return null;
    }

    /*********************************************************
     * LongClickDetector.OnLongClickListener实现结束
     *********************************************************/

    @Override
    public void exitSelectionMode() {
        // TODO Auto-generated method stub
        if (mIInternalDoodle.getSelectionMode() != DoodleEnum.SelectionMode.SELECTION)
            return;

        dismissSelectView();
        unSelected();
        mIInternalDoodle.setSelectionMode(DoodleEnum.SelectionMode.NONE);
    }

    public void dismissSelectView() {
        if (mSelectView != null && mSelectView.isSelectViewShowing()) {
            mSelectView.dismissSelectView();
        }
    }

    @Override
    public boolean enterSelectionMode() {
        if (mIInternalDoodle.getSelectionMode() == DoodleEnum.SelectionMode.SELECTION)
            return true;
        if (mActingInsertableObject == null)// 重要判断
            return false;
        if (!mActingInsertableObject.isSelectable())
            return false;
        final InsertableObjectBase temp = mActingInsertableObject;
        /**
         * 比如从Gallery添加图片的时候，这里会先insert一个SelectedDrawAllOperation，
         * 然后doodleview，insert一个DrawAllOperation。
         * 这样的插入顺序会导致显示不正常。强制延迟50ms，使得DrawAllOperation先插入
         * ，SelectedDrawAllOperation后插入
         */
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                onSelectView(temp);
            }
        }, 50);
        return true;
    }
}