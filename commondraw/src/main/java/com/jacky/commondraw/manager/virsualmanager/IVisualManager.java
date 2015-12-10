package com.jacky.commondraw.manager.virsualmanager;

import com.jacky.commondraw.listeners.IIsertableObjectListener;
import com.jacky.commondraw.listeners.ITouchEventListener;
import com.jacky.commondraw.model.InsertableObjectBase;
import com.jacky.commondraw.visual.VisualElementBase;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 */
public interface IVisualManager extends IIsertableObjectListener,
        ITouchEventListener {
    /**
     * 通过insertableObject获得其对应的可视化元素
     *
     * @return
     */
    public VisualElementBase getVisualElement(
            InsertableObjectBase insertableObject);

    /**
     * 获得当前可视化元素的个数
     *
     * @return
     */
    int getVisuleElementCount();

    /**
     * 获得当前可视化元素所占据的内存
     *
     * @return 所占据的内存大小。单位:byte
     */
    int getVisuleElementMemory();
}