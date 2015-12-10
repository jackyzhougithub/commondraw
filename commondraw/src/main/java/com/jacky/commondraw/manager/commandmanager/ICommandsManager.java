package com.jacky.commondraw.manager.commandmanager;

import com.jacky.commondraw.listeners.IOnDoAvailabilityChangedListener;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 * 定义命令管理器的接口
 * 标准undo redo 做法
 */
public interface ICommandsManager {
    void addDoAvailabilityChangedListener(
            IOnDoAvailabilityChangedListener listener);

    void removeDoAvailabilityChangedListener(
            IOnDoAvailabilityChangedListener listener);

    /**
     * 获得最后一个undo命令;如果没有，则返回null
     *
     * @return
     */
    ICommand removeUndo();

    /**
     * 获得最后一个redo命令;如果没有，则返回null
     *
     * @return
     */
    ICommand removeRedo();

    /**
     * 禁用undo,redo
     */
    void disable();

    /**
     * 重新使用undo,redo
     */
    void enable();

    /**
     * 插入一个undo命令
     *
     * @return
     */
    void addUndo(ICommand command);

    void clear();

    void clearUndo();

    void clearRedo();

}
