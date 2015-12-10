package com.jacky.commondraw.views.doodleview.opereation;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.jacky.commondraw.manager.commandmanager.ICommand;
import com.jacky.commondraw.manager.modelmager.IModelManager;
import com.jacky.commondraw.manager.virsualmanager.IVisualManager;
import com.jacky.commondraw.views.doodleview.FrameCache;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 */
public abstract class DoodleOperation {
    public static final String TAG = "DoodleOperation";
    protected IModelManager mModelManager;
    protected IVisualManager mVisualManager;
    // protected List<DrawTool> mAllDrawTools;
    protected FrameCache mFrameCache;

    protected boolean mIsCreatingCommand = true;

    // public DoodleOperation(SurfaceHolder surfaceHolder, FrameCache
    // frameCache) {
    // mSurfaceHolder = surfaceHolder;
    // mFrameCache = frameCache;
    // }

    // public DoodleOperation(SurfaceHolder surfaceHolder, FrameCache
    // frameCache,
    // List<DrawTool> allDrawTools, CommandsManager commandsManager) {
    // mSurfaceHolder = surfaceHolder;
    // mFrameCache = frameCache;
    // mAllDrawTools = allDrawTools;
    // mCommandsManager = commandsManager;
    // }

    public DoodleOperation(FrameCache frameCache, IModelManager modelManager,
                           IVisualManager visualManager) {
        mFrameCache = frameCache;
        mModelManager = modelManager;
        mVisualManager = visualManager;
    }

    /**
     * 绘制过程。绘制策略由DrawStrategy类完成；最终绘制还是通过VisualElementBase类来完成的
     */
    public void draw(Canvas canvas) {
        onDraw(canvas);
    }

    // /**
    // * 绘制之前
    // */
    // public abstract void beforeDraw();
    //
    // /**
    // * 绘制之后
    // */
    // public abstract void afterDraw();

    /**
     *
     * @param canvas
     *            在其上绘制的canvas
     */
    protected abstract void onDraw(Canvas canvas);

    /**
     * 获取Dirty区域
     *
     * @return
     */
    public abstract Rect computerDirty();

    // /**
    // * 创建该IDrawStatus的绘制策略
    // */
    // protected abstract DrawStrategy createDrawStrategy(Canvas canvas,
    // FrameCache frameCache);

    public abstract ICommand onCreateCommand();

    /**
     * 创建该DoodleOperation的ICommand。并不是所有的DoodleOperation都需要返回command，不需要的就返回null
     * 该方法会首先判断{@link DoodleOperation#setCreatingCommand(boolean)}
     * 方法穿过来的值，如果值为false,则直接返回null, 否则返回
     * {@link DoodleOperation#onCreateCommand()} 的返回值
     */
    public ICommand createCommand() {
        if (!mIsCreatingCommand)
            return null;
        return onCreateCommand();
    }

    public boolean isCreatingCommand() {
        return mIsCreatingCommand;
    }

    /**
     * 是否创建ICommand。
     *
     * @param creating
     *            :是否创建。 该变量将在{@link DoodleOperation#onCreateCommand()}
     *            里面使用到，如果creating为false，则createCommand默认直接返回null。 该值默认为true
     */
    public void setCreatingCommand(boolean creating) {
        mIsCreatingCommand = creating;
    }
}
