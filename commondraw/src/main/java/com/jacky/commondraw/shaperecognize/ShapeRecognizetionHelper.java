package com.jacky.commondraw.shaperecognize;

import com.jacky.commondraw.listeners.ISRAvailabilityChangedListener;
import com.jacky.commondraw.views.doodleview.DoodleEnum;
import com.jacky.commondraw.views.doodleview.DoodleView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class ShapeRecognizetionHelper {
    private List<ISRAvailabilityChangedListener> mSRAvailabilityChangedListeners;
    private DoodleView mDoodleView;
    private boolean mSRAvailable = false;
    private IShapeRecognizeManager mShapeRecognizeManager;

    /**
     * 当前是不是处于图形识别状态
     */
    private boolean mIsShapeRecognition = false;
    /**
     * mShapeRecognizeManager是否已经初始化
     */
    private boolean mManagerInited = false;

    private boolean mHasEngineClass = false;

    private boolean mCanInit = false;

    public ShapeRecognizetionHelper(DoodleView doodleView,
                                    IShapeRecognizeManager shapeRecognizeManager) {
        mDoodleView = doodleView;
        mShapeRecognizeManager = shapeRecognizeManager;
        mSRAvailabilityChangedListeners = new ArrayList<ISRAvailabilityChangedListener>();
        /**
         * 需要先判断Engine class是否存在，否则会抛出java.lang.NoClassDefFoundError:
         * com.visionobjects.myscript.engine.Engine。 Error是捕获不了的
         */
        mHasEngineClass = hasEngineClass();
        initShape();
    }

    /**
     * 判断com.visionobjects.myscript.engine.Engine是否存在
     *
     * @return
     */
    private boolean hasEngineClass() {
        boolean has = false;
        try {
            Class.forName("com.visionobjects.myscript.engine.Engine");
            has = true;
        } catch (Exception e) {
            // TODO: handle exception
            has = false;
        }
        return has;
    }

    public boolean canShapeRecognition() {
        return mShapeRecognizeManager.canDoVO() && mHasEngineClass && mCanInit;
    }

    public void addSRAvailabilityChangedListener(
            ISRAvailabilityChangedListener listener) {
        if (listener != null)
            mSRAvailabilityChangedListeners.add(listener);
    }

    public void removeSRAvailabilityChangedListener(
            ISRAvailabilityChangedListener listener) {
        if (listener != null)
            mSRAvailabilityChangedListeners.remove(listener);
    }

    private void fireSRAvailabilityChanged(boolean available) {
        for (ISRAvailabilityChangedListener listener : mSRAvailabilityChangedListeners) {
            listener.onSRAvailabilityChanged(available);
        }
    }

    private void setSRAvailable(Boolean available) {
        if (!mHasEngineClass)
            return;
        mSRAvailable = available;
        if (!available) {
            // clearAsusShape(true);
            // deInitAsusShape();
            mShapeRecognizeManager.saveShapeResult(true);
        } else {
            initShape();
            mShapeRecognizeManager.clearAsusShape();
        }
        fireSRAvailabilityChanged(available);
    }

    public boolean getSRAvailable() {
        return mSRAvailable;
    }

    public void resetSRAvailable() {
        if (!canShapeRecognition()) {
            setSRAvailable(false);
            return;
        }
        if (mDoodleView.getInputMode() == DoodleEnum.InputMode.ERASE) {
            setSRAvailable(false);
            return;
        }
        if (mDoodleView.getSelectionMode() == DoodleEnum.SelectionMode.SELECTION) {
            setSRAvailable(false);
            return;
        }
        setSRAvailable(true);
    }

    /**
     * 返回false的情况如下:1.系统不支持图形识别;2.DoodleView
     * 当前InputMode为EARSE;3.当前的InternalInputMode为SELECTING
     *
     * @return：如果成功，则返回true；失败返回false。
     */
    public boolean startShapeRecognition() {
        if (!mHasEngineClass)
            return false;
        if (mIsShapeRecognition)
            return true;
        initShape();
        mShapeRecognizeManager.clearAsusShape();
        if (mManagerInited) {
            mIsShapeRecognition = true;
        }
        return mIsShapeRecognition;
    }

    public void stopShapeRecognition() {
        if (!mIsShapeRecognition)
            return;
        saveShapeResult(true);
        // deInitAsusShape();
        mIsShapeRecognition = false;
    }

    public boolean isShapeRecognition() {
        return mIsShapeRecognition;
    }

    /**
     * initAsusShape,deInitAsusShape需要成对调用
     */
    public void initShape() {
        // TODO Auto-generated method stub
        if (!mHasEngineClass)
            return;
        try {
            if (mManagerInited)
                return;
            if (!mShapeRecognizeManager.canDoVO()) {
                mManagerInited = false;
                return;
            }
            mShapeRecognizeManager.initShape();
            mManagerInited = true;
            mCanInit = true;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            mCanInit = false;
        }
    }

    public void deInitShape() {
        if (!mHasEngineClass)
            return;
        // TODO Auto-generated method stub
        if (!mManagerInited)
            return;
        if (!canShapeRecognition()) {
            mManagerInited = false;
            return;
        }
        mShapeRecognizeManager.deInitShape();
        mManagerInited = false;
    }

    // void clearAsusShape(boolean save) {
    // // TODO Auto-generated method stub
    // if (!mManagerInited)
    // return;
    // mShapeRecognizeManager.clearAsusShape(save);
    // }
    public void saveShapeResult(boolean bSave) {
        if (!mHasEngineClass)
            return;
        if (!mManagerInited)
            return;
        mShapeRecognizeManager.saveShapeResult(bSave);
    }

    // void saveShapeList(){
    // if (!mManagerInited)
    // return;
    // mShapeRecognizeManager.saveShapeList();
    // }
}

