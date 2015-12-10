package com.jacky.commondraw.manager.modelmager;

import android.view.MotionEvent;

import com.jacky.commondraw.listeners.IIsertableObjectListener;
import com.jacky.commondraw.listeners.ISelectedChangedListener;
import com.jacky.commondraw.listeners.IStrokeReadyListener;
import com.jacky.commondraw.listeners.ITouchEventListener;
import com.jacky.commondraw.model.InsertableObjectBase;

import java.util.List;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 *
 */
public interface IModelManager {
    List<InsertableObjectBase> getInsertableObjectList();

    void clear();

    /**
     * 清空所有笔画，包括图形识别
     */
    void clearStokes();

    void addInsertableObject(InsertableObjectBase object);

    void addInsertableObject(InsertableObjectBase object,
                                    boolean fromUndoRedo);

    void addInsertableObject(java.util.List<InsertableObjectBase> list);

    void addInsertableObject(List<InsertableObjectBase> list,
                                    boolean fromUndoRedo);

    void removeInsertableObject(InsertableObjectBase object);

    void removeInsertableObject(InsertableObjectBase object,
                                       boolean fromUndoRedo);

    void removeInsertableObject(List<InsertableObjectBase> list);

    void removeInsertableObject(List<InsertableObjectBase> list,
                                       boolean fromUndoRedo);

    void addIsertableObjectListener(IIsertableObjectListener listener);

    void removeIsertableObjectListener(IIsertableObjectListener listener);

    void addSeletedChangedListener(ISelectedChangedListener listener);

    void removeInsertableObjectSeletedListener(
            ISelectedChangedListener listener);

    void addTouchEventListener(ITouchEventListener listener);

    void removeTouchEventListener(ITouchEventListener listener);

    /**
     * 退出选择模式
     */
    void exitSelectionMode();

    /**
     * 进入选择模式
     *
     * @return
     */
     boolean enterSelectionMode();

    /**
     * 该控制器接受处理onTouchEvent
     *
     * @param event
     * @return
     */
    boolean onTouchEvent(MotionEvent event);

    void addStrokeReadyListener(IStrokeReadyListener listener);

    void removeStrokeReadyListener(IStrokeReadyListener listener);

    /**
     * 是否有对象被选中
     *
     * @return
     */
    boolean hasObjectSelected();

    /**
     * 获得当前选中的对象。如果没有，则返回null
     *
     * @return
     */
    InsertableObjectBase getSelectedObject();

    /**
     * 如果当前有{@link InsertableObjectBase} object被选中，则释放掉选中状态
     */
    void unSelected();

    /**
     * 如果当前的{@link ISelectView} 显示，则dismiss掉。 该方法并不会修改一个
     * {@link InsertableObjectBase} object的选中状态
     */
    void dismissSelectView();

}
