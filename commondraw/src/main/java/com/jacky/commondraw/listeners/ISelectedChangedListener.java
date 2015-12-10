package com.jacky.commondraw.listeners;

import com.jacky.commondraw.model.InsertableObjectBase;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 * 有对象被选中,或选中结束时候的回调
 */
public interface ISelectedChangedListener {
    void onSelected(InsertableObjectBase insertableObject);
    void onUnSelected(InsertableObjectBase insertableObject);
}
