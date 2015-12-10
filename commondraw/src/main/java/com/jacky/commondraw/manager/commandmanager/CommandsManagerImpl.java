package com.jacky.commondraw.manager.commandmanager;

import android.os.Handler;

import com.jacky.commondraw.listeners.IOnDoAvailabilityChangedListener;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class CommandsManagerImpl implements ICommandsManager {
    public static final String TAG = "CommandsManager";
    private Deque<ICommand> mUndoCommands = new LinkedList<ICommand>();
    private Deque<ICommand> mRedoCommands = new LinkedList<ICommand>();
    // private onDoAvailabilityChangedListener mAvailabilityChangedListener;
    private List<IOnDoAvailabilityChangedListener> mAvailabilityChangedListeners;
    private Handler mHandler = new Handler();

    public CommandsManagerImpl() {
        mAvailabilityChangedListeners = new ArrayList<IOnDoAvailabilityChangedListener>();
    }

    // public onDoAvailabilityChangedListener getAvailabilityChangedListener() {
    // return mAvailabilityChangedListener;
    // }
    //
    // public void setOnAvailabilityChangedListener(
    // onDoAvailabilityChangedListener mAvailabilityChangedListener) {
    // this.mAvailabilityChangedListener = mAvailabilityChangedListener;
    // }
    @Override
    public void addDoAvailabilityChangedListener(
            IOnDoAvailabilityChangedListener listener) {
        mAvailabilityChangedListeners.add(listener);
    }

    @Override
    public void removeDoAvailabilityChangedListener(
            IOnDoAvailabilityChangedListener listener) {
        mAvailabilityChangedListeners.remove(listener);
    }

    /**
     * 获得最后一个undo命令;如果没有，则返回null
     *
     * @return
     */
    @Override
    public ICommand removeUndo() {
        synchronized (mUndoCommands) {
            ICommand command = null;
            if (!mUndoCommands.isEmpty()) {
                command = mUndoCommands.removeLast();
                if (mUndoCommands.isEmpty()) {
                    fireUndoAvailabilityChanged(false);
                }
                addRedo(command);
            }
            return command;
        }
    }

    /**
     * 获得最后一个redo命令;如果没有，则返回null
     *
     * @return
     */
    @Override
    public ICommand removeRedo() {
        synchronized (mRedoCommands) {
            ICommand command = null;
            if (!mRedoCommands.isEmpty()) {
                command = mRedoCommands.removeLast();
                if (mRedoCommands.isEmpty()) {
                    fireRedoAvailabilityChanged(false);
                }
                addUndo(command, true);
            }
            return command;
        }
    }

    /**
     *
     * @param command
     * @param fromRedoStatck
     *            :是否来自redo
     */
    protected void addUndo(ICommand command, boolean fromRedoStatck) {
        synchronized (mUndoCommands) {
            if (command == null)
                return;
            boolean isEmpty = mUndoCommands.isEmpty();
            mUndoCommands.addLast(command);
            if (isEmpty) {
                fireUndoAvailabilityChanged(true);
            }
            if (!fromRedoStatck) {
                clearRedo();
            }
        }
    }

    /**
     * 插入一个undo命令
     *
     * @return
     */
    @Override
    public void addUndo(ICommand command) {
        addUndo(command, false);
    }

    protected void addRedo(ICommand command) {
        synchronized (mRedoCommands) {
            boolean isEmpty = mRedoCommands.isEmpty();
            mRedoCommands.addLast(command);
            if (isEmpty) {
                fireRedoAvailabilityChanged(true);
            }
        }
    }

    protected void fireRedoAvailabilityChanged(final boolean available) {
        for (IOnDoAvailabilityChangedListener listener : mAvailabilityChangedListeners) {
            final IOnDoAvailabilityChangedListener temp = listener;
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    temp.onRedoAvailabilityChanged(available);
                }
            });
        }
    }

    protected void fireUndoAvailabilityChanged(final boolean available) {
        for (IOnDoAvailabilityChangedListener listener : mAvailabilityChangedListeners) {
            final IOnDoAvailabilityChangedListener temp = listener;
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    temp.onUndoAvailabilityChanged(available);
                }
            });
        }
    }

    @Override
    public void clear() {
        clearRedo();
        clearUndo();
    }

    public void clearRedo() {
        if (mRedoCommands.size() > 0) {
            mRedoCommands.clear();
            fireRedoAvailabilityChanged(false);
        }
    }

    public void clearUndo() {
        if (mUndoCommands.size() > 0) {
            mUndoCommands.clear();
            fireUndoAvailabilityChanged(false);
        }
    }

    public int getUndoCount() {
        return mUndoCommands.size();
    }

    @Override
    public void disable() {
        // TODO Auto-generated method stub
        fireUndoAvailabilityChanged(false);
        fireRedoAvailabilityChanged(false);
    }

    @Override
    public void enable() {
        // TODO Auto-generated method stub
        if (!mRedoCommands.isEmpty()) {
            fireRedoAvailabilityChanged(true);
        }
        if (!mUndoCommands.isEmpty()) {
            fireUndoAvailabilityChanged(true);
        }
    }

}
