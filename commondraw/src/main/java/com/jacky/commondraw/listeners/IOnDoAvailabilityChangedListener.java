package com.jacky.commondraw.listeners;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 * 定义Undo,Redo可用性改变时候的接口
 */
public interface IOnDoAvailabilityChangedListener {
    void onUndoAvailabilityChanged(boolean available);

    void onRedoAvailabilityChanged(boolean available);
}
