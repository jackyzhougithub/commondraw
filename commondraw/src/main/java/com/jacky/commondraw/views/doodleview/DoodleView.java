package com.jacky.commondraw.views.doodleview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.jacky.commondraw.listeners.IOnDoAvailabilityChangedListener;
import com.jacky.commondraw.listeners.ISRAvailabilityChangedListener;
import com.jacky.commondraw.listeners.ISelectionModeChangedListener;
import com.jacky.commondraw.manager.commandmanager.CommandsManagerImpl;
import com.jacky.commondraw.manager.commandmanager.ICommand;
import com.jacky.commondraw.manager.commandmanager.ICommandsManager;
import com.jacky.commondraw.manager.modelmager.IModelManager;
import com.jacky.commondraw.manager.modelmager.ModelManager;
import com.jacky.commondraw.manager.virsualmanager.IVisualManager;
import com.jacky.commondraw.manager.virsualmanager.VisualManagerImpl;
import com.jacky.commondraw.model.InsertableObjectBase;
import com.jacky.commondraw.model.propertyconfig.PropertyConfigStroke;
import com.jacky.commondraw.model.stroke.InsertableObjectStroke;
import com.jacky.commondraw.shaperecognize.IShapeRecognizeManager;
import com.jacky.commondraw.shaperecognize.ShapeRecognizeManager;
import com.jacky.commondraw.shaperecognize.ShapeRecognizeOperation;
import com.jacky.commondraw.shaperecognize.ShapeRecognizetionHelper;
import com.jacky.commondraw.utils.ErrorUtil;
import com.jacky.commondraw.utils.PropertyConfigStrokeUtils;
import com.jacky.commondraw.views.doodleview.drawstrategy.RedrawStrategy;
import com.jacky.commondraw.views.doodleview.opereation.DoodleOperation;
import com.jacky.commondraw.views.doodleview.opereation.DrawAllOperation;
import com.jacky.commondraw.visual.brush.VisualStrokeBase;
import com.jacky.commondraw.visual.brush.operation.StrokeTouchOperation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 * 主要的自定义画图的view
 * 实现一个支持可插入{@link InsertableObjectBase} object的view
 */
public class DoodleView  extends SurfaceView implements IInternalDoodle {
    private static final String TAG = "DoodleView";
    /**
     * 是否执行丢点策略。如果为true:在DrawThread中只绘制mDrawOperationList的最后一个操作;false：全部绘制
     */
    // private static final boolean sLosingPointsStrategy = false;
    private static final String INFO_PREFIX = "DoodleView:";
    private Handler mHandler = new Handler();
    private SurfaceHolder mSurfaceHolder;
    private DrawThread mDrawThread;
    private ICommandsManager mCommandsManager = new CommandsManagerImpl();
    /**
     * 背景图片
     */
    private Bitmap mBackgroundBitmap;

    /**
     * 一个阻塞队列，生产者为UI线程，消费者为DrawThread线程。
     * 该LinkedBlockingDeque的容量为无限大，故Ui线程永远不会被阻塞
     */
    private BlockingQueue<DoodleOperation> mDrawBlockingQueue = new LinkedBlockingQueue<DoodleOperation>();

    private FrameCache mFrameCache;

    /**
     * 专门为分段画笔预备的缓存
     */
    private FrameCache mTempFrameCache;

    private DoodleEnum.InputMode mInputMode = DoodleEnum.InputMode.DRAW;
    private DoodleEnum.SelectionMode mSelectionMode = DoodleEnum.SelectionMode.NONE;

    private IModelManager mModelManager;
    private IVisualManager mVisualManager;
    private int mStrokeType = InsertableObjectStroke.STROKE_TYPE_NORMAL;
    /**
     * 操作是否由undo,redo产生的 如果是由undo,redo产生的，那么由其获得的命令并不压入命令栈
     */
    // private boolean mFromUndoRedo = false;

    private ShapeRecognizeManager mShapeRecognizeManager;

    private ShapeRecognizetionHelper mShapeRecognizetionHelper;

    private List<ISelectionModeChangedListener> mSelectionModeChangedListeners;

    /**
     *
     * @param context
     * @param attrs
     */
    public DoodleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        setZOrderOnTop(true);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);// 设置为透明

        mSurfaceHolder.addCallback(mSurfaceCallback);

        mModelManager = new ModelManager(this);
        mVisualManager = new VisualManagerImpl(getContext(), this);
        mModelManager.addIsertableObjectListener(mVisualManager);
        mModelManager.addTouchEventListener(mVisualManager);
        mShapeRecognizeManager = new ShapeRecognizeManager(this, getContext());
        mShapeRecognizetionHelper = new ShapeRecognizetionHelper(this,
                mShapeRecognizeManager);

        mModelManager.addStrokeReadyListener(mShapeRecognizeManager);

        mSelectionModeChangedListeners = new ArrayList<ISelectionModeChangedListener>();

    }

    private SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            if (mDrawThread != null) {// 线程好像不能start两次，这里直接释放掉已有线程
                mDrawThread.mLoop = false;
                mDrawThread = null;
            }
            if (mFrameCache != null) {
                mFrameCache.recycle();
                mFrameCache = null;
            }
            if (mTempFrameCache != null) {
                mTempFrameCache.recycle();
                mTempFrameCache = null;
            }
            if (mShapeRecognizetionHelper.isShapeRecognition()) {// 反初始化时机这里？
                // mShapeRecognizetionHelper.clearAsusShape(true);
                // mShapeRecognizetionHelper.deInitAsusShape();
                mShapeRecognizetionHelper.saveShapeResult(true);
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            Log.i(TAG, INFO_PREFIX + "width" + getWidth());
            Log.i(TAG, INFO_PREFIX + "height" + getHeight());
            if (mFrameCache == null) {
                mFrameCache = new FrameCache(getWidth(), getHeight());
                // mFrameCache.getCanvas().drawColor(Color.TRANSPARENT);
            }
            resetSegmentFrameCache();
            if (mDrawThread != null) {
                mDrawThread.mLoop = false;
                mDrawThread = null;
            }
            mDrawThread = new DrawThread();
            mDrawThread.mLoop = true;
            mDrawThread.start();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            // TODO Auto-generated method stub
            if (mFrameCache != null) {
                mFrameCache.recycle();
                mFrameCache = null;
            }
            mFrameCache = new FrameCache(getWidth(), getHeight());
            if (mTempFrameCache != null) {
                mTempFrameCache.recycle();
                mTempFrameCache = null;
            }
            resetSegmentFrameCache();
            DrawAllOperation operation = new DrawAllOperation(mFrameCache,
                    mModelManager, mVisualManager);
            insertOperation(operation);// 第一次重绘所有的
            // mCommandsManager.enable();
        }
    };

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        exitSelectionMode();// 退出，防止内存泄露
        mShapeRecognizetionHelper.deInitShape();
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub

        boolean multiPoint = event.getPointerCount() > 1;
        if (multiPoint)
            return false;
        return mModelManager.onTouchEvent(event);// 事件通过mModelManager进一步分发
    }

    /**
     * 绘图线程。DoodleView绘制图形的过程将在该线程内完成
     *
     * @author noah
     */
    class DrawThread extends Thread {
        // private DrawStatus mDrawStatus;
        /**
         * 每一次,从mDrawBlockingQueue中取所有的DrawStatus,放入mDrawStatusList
         */
        private List<DoodleOperation> mDoodleOperationList = new LinkedList<DoodleOperation>();
        private boolean mLoop = false;

        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (mLoop) {
                fetchData();
                if (mDoodleOperationList.size() <= 0)
                    continue;
                drawOperation();
            }
        }

        /**
         * 绘制操作。遍历绘制每一个操作
         */
        private void drawOperation() {
            for (DoodleOperation operation : mDoodleOperationList) {
                /**
                 * 笔画我们在主ui线程去绘制，原因是:在工作线程绘制会导致延迟现象（当然，我们可以在工作线程中加入丢点策略去解决）；
                 * 在主ui线程中没有延迟现象，是因为主ui线程会自动丢点
                 */
                if (operation instanceof StrokeTouchOperation
                        || operation instanceof ShapeRecognizeOperation) {
                    final DoodleOperation temp = operation;
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            handleDrawOperation(temp, true);
                        }
                    });
                } else {
                    handleDrawOperation(operation, true);
                }
            }
        }

        /**
         * 处理DrawOperation,核心两步:drawOperation.draw，以及undo,redo，入栈
         *
         * @param doodleOperation
         * @param draw
         *            是否绘制
         */
        private void handleDrawOperation(DoodleOperation doodleOperation,
                                         boolean draw) {
            if (!draw) {
                return;
            }
            Canvas canvas = null;
            try {
                Rect dirty = doodleOperation.computerDirty();
                if (dirty != null)
                    Log.i(TAG, "DoodleView,computerDirty：" + dirty.toString());
                canvas = mSurfaceHolder.lockCanvas(dirty);
                synchronized (mSurfaceHolder) {
                    doodleOperation.draw(canvas);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    try {
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e2) {
                        // TODO: handle exception
                        e2.printStackTrace();
                    }
                }
            }
            ICommand command = doodleOperation.createCommand();
            if (command != null) {
                mCommandsManager.addUndo(command);
            }
        }

        protected void fetchData() {
            mDoodleOperationList.clear();
            try {// 如果mDrawBlockingQueue没有数据则阻塞当前线程
                // mDrawStatus = mDrawBlockingQueue.take();
                Log.i(TAG, "segment,mDrawBlockingQueue.size:"
                        + mDrawBlockingQueue.size());
                mDoodleOperationList.add(mDrawBlockingQueue.take());
                mDrawBlockingQueue.drainTo(mDoodleOperationList);
                Log.i(TAG, "segment,mDoodleOperationList.size:"
                        + mDoodleOperationList.size());
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置当前画笔的类型。 不允许设置为InsertableObjectStroke.STROKE_TYPE_ERASER；
     * 如果想要设置为InsertableObjectStroke.STROKE_TYPE_ERASER，
     *
     * @param type
     */
    public void setStrokeType(int type) {
        if (!InsertableObjectStroke.isSupported(type)
                || type == InsertableObjectStroke.STROKE_TYPE_ERASER) {
            throw ErrorUtil.getStrokeTypeNoteSupportedError(type);
        }
        mStrokeType = type;
        /**
         * 画笔属性改变的时候，需要保存一下图像识别的结果；否则会出现之前绘制的识别绘制不正常的问题
         */
        mShapeRecognizetionHelper.saveShapeResult(true);
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                resetSegmentFrameCache();// 此时，DoodleView可能还没有初始化好，所以要Post一下
            }
        });
    }

    private void resetSegmentFrameCache() {
        InsertableObjectStroke stroke = new InsertableObjectStroke(mStrokeType);
        VisualStrokeBase visualStroke = (VisualStrokeBase) stroke
                .createVisualElement(getContext(), this);
        if (visualStroke.isSegmentDraw()) {
            mTempFrameCache = new FrameCache(getWidth(), getHeight());
        } else {
            if (mTempFrameCache != null)
                mTempFrameCache.recycle();// 清空分段画笔缓存，节省内存
            mTempFrameCache = null;
        }
    }

    /**
     * 设置某支画笔的相关属性
     *
     * @param type
     *            要设置的画笔类型
     * @param color
     *            画笔颜色
     * @param strokeWidth
     *            画笔宽度
     * @param alpha
     *            画笔透明度。0-255
     */
    public void setStrokeAttrs(int type, int color, float strokeWidth, int alpha) {
        if (!InsertableObjectStroke.isSupported(type)) {
            throw ErrorUtil.getStrokeTypeNoteSupportedError(type);
        }
        PropertyConfigStrokeUtils.setStrokeAttrs(type, color, strokeWidth,
                alpha);
        /**
         * 画笔属性改变的时候，需要保存一下图像识别的结果；否则会出现之前绘制的识别绘制不正常的问题
         */
        if (type != InsertableObjectStroke.STROKE_TYPE_ERASER
                && type == mStrokeType) {
            mShapeRecognizetionHelper.saveShapeResult(true);
        }
    }

    /**
     * 设置橡皮擦的宽度
     *
     * @param width
     */
    public void setEraseWidth(float width) {
        PropertyConfigStrokeUtils.setStrokeAttrs(
                InsertableObjectStroke.STROKE_TYPE_ERASER, 0, width, 0);
    }

    /**
     * 获得橡皮擦的宽度
     */
    public float getEraseWidth() {
        PropertyConfigStroke configStroke = PropertyConfigStrokeUtils
                .getPropertyConfigStroke(InsertableObjectStroke.STROKE_TYPE_ERASER);
        return configStroke.getStrokeWidth();
    }

    /**
     * undo，撤销上一次操作
     */
    public void undo() {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                ICommand command = mCommandsManager.removeUndo();
                if (command == null)
                    return;
                mModelManager.exitSelectionMode();
                command.undo();
            }
        });
    }

    /**
     * redo，重做上一次操作
     */
    public void redo() {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                ICommand command = mCommandsManager.removeRedo();
                if (command == null)
                    return;
                mModelManager.exitSelectionMode();
                command.redo();
            }
        });
    }

    /**
     * 清空所有(包括图片等)。该操作不可逆
     */
    public void clear() {
        mModelManager.exitSelectionMode();
        // mShapeRecognizetionHelper.clearAsusShape(false);
        mShapeRecognizetionHelper.saveShapeResult(false);
        mModelManager.clear();
        mCommandsManager.clear();
    }

    /**
     * 清空所有的笔画(包括图形识别出来的)，该操作不可逆 该操作同时会清空undo,redo的statck
     */
    public void clearStrokes() {
        mModelManager.exitSelectionMode();
        // mShapeRecognizetionHelper.clearAsusShape(false);
        mShapeRecognizetionHelper.saveShapeResult(false);
        mModelManager.clearStokes();
        mCommandsManager.clear();
    }



    /**
     * 设置当前的输入模式
     *
     * @param mInputMode
     */
    public void setInputMode(DoodleEnum.InputMode inputMode) {
        this.mInputMode = inputMode;
        mShapeRecognizetionHelper.resetSRAvailable();
    }

    /**
     * 设置背景
     *
     * @param bitmap
     */
    public void setBackgroundBitmap(Bitmap bitmap) {
        if (bitmap == mBackgroundBitmap)
            return;
        mBackgroundBitmap = bitmap;
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(),
                bitmap);
        // setBackground(bitmapDrawable);
        setBackgroundDrawable(bitmapDrawable);
        // bitmap.recycle();
    }

    /**
     * 生成整个DoodleView的Bitmap
     *
     * @param drawBg
     *            :是否绘制背景
     * @return 生成的DoobleView的Bitmap,大小等于DoodleView的大小
     */
    public Bitmap newWholeBitmap(boolean drawBg) {
        FrameCache frameCache = new FrameCache(getWidth(), getHeight());
        // 重新绘制所有笔画
        List<InsertableObjectBase> list = new ArrayList<InsertableObjectBase>();// 复制一份新数据，否则会出异常
        list.addAll(mModelManager.getInsertableObjectList());
        RedrawStrategy drawStrategy = new RedrawStrategy(null, frameCache, null,
                list, mVisualManager);
        drawStrategy.draw();

        Drawable drawable = getBackground();
        if (drawBg && drawable != null && drawable.isVisible()) {// 绘制背景
            FrameCache backgroundCache = new FrameCache(getWidth(), getHeight());
            drawable.setBounds(0, 0, getWidth(), getHeight());
            drawable.draw(backgroundCache.getCanvas());

            Rect rect = new Rect(0, 0, getWidth(), getHeight());
            Paint paint = new Paint();
            paint.setDither(true);
            paint.setAntiAlias(true);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
            frameCache.getCanvas().drawBitmap(backgroundCache.getBitmap(),
                    rect, rect, paint);
            backgroundCache.recycle();
        }

        return frameCache.getBitmap();
    }

    /**
     * 生成整个insertableObject对应的bitmap
     *
     * @param insertableObject
     * @return
     */
    public Bitmap newBitmap(InsertableObjectBase insertableObject) {
        if (insertableObject == null)
            return null;
        Bitmap wholeBitmap = newWholeBitmap(false);
        if (wholeBitmap == null)
            return null;
        RectF rectF = InsertableObjectBase
                .getTransformedRectF(insertableObject);

        return createBitmap(wholeBitmap, rectF);
    }

    /**
     * 生成绘制区域的bitmap
     *
     * @return
     */
    public Bitmap newBitmap() {
        List<InsertableObjectBase> list = new ArrayList<InsertableObjectBase>();// 复制一份新数据，否则会出异常
        list.addAll(mModelManager.getInsertableObjectList());
        if (list.size() <= 0)
            return null;
        Bitmap wholeBitmap = newWholeBitmap(false);
        RectF rectF = new RectF();
        for (InsertableObjectBase insertableObjectBase : list) {
            RectF rectF2 = InsertableObjectBase
                    .getTransformedRectF(insertableObjectBase);
            if (rectF2 != null) {
                rectF.union(rectF2);
            }
        }
        return createBitmap(wholeBitmap, rectF);
    }

    private Bitmap createBitmap(Bitmap wholeBitmap, RectF rectF) {
        int x = (int) rectF.left;
        int y = (int) rectF.top;
        int width = (int) (rectF.right - rectF.left);
        int height = (int) (rectF.bottom - rectF.top);
        if (x < 0) {
            width += x;
            x = 0;
        }
        if (x + width > wholeBitmap.getWidth()) {
            width = wholeBitmap.getWidth() - x;
        }
        if (y < 0) {
            height += y;
            y = 0;
        }
        if (y + height > wholeBitmap.getHeight()) {
            height = wholeBitmap.getHeight() - y;
        }
        Bitmap bitmap = Bitmap.createBitmap(wholeBitmap, x, y, width, height);
        return bitmap;
    }

    /**
     * 设置undo,redo可用性改变时候的回调
     *
     * @param mAvailabilityChangedListener
     */
    public void addOnDoAvailabilityChangedListener(
            IOnDoAvailabilityChangedListener mAvailabilityChangedListener) {
        mCommandsManager
                .addDoAvailabilityChangedListener(mAvailabilityChangedListener);
    }

    public void removeOnDoAvailabilityChangedListener(
            IOnDoAvailabilityChangedListener mAvailabilityChangedListener) {
        mCommandsManager
                .removeDoAvailabilityChangedListener(mAvailabilityChangedListener);
    }

    /*********************************************
     * IManagerFetcher接口实现开始
     ********************************************/
    @Override
    public IModelManager getModelManager() {
        // TODO Auto-generated method stub
        return mModelManager;
    }

    @Override
    public IVisualManager getVisualManager() {
        // TODO Auto-generated method stub
        return mVisualManager;
    }

    @Override
    public ICommandsManager getCommandsManager() {
        return mCommandsManager;
    }

    @Override
    public DoodleEnum.InputMode getInputMode() {
        return mInputMode;
    }

    @Override
    public FrameCache getFrameCache() {
        // TODO Auto-generated method stub
        return mFrameCache;
    }

    /*********************************************
     * IManagerFetcher接口实现结束
     ********************************************/

    /*********************************************
     * IInternaleDoodle接口实现开始
     ********************************************/
    @Override
    public FrameCache getTempFrameCache() {
        // TODO Auto-generated method stub
        return mTempFrameCache;
    }

    @Override
    public void insertOperation(DoodleOperation operation) {
        try {
            mDrawBlockingQueue.put(operation);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            Log.i(TAG, INFO_PREFIX
                    + "mDrawBlockingDeque插入数据失败，不应该发生；mDrawBlockingDeque.size:"
                    + mDrawBlockingQueue.size());
        }
    }

    @Override
    public int getStrokeType() {
        // TODO Auto-generated method stub
        return mStrokeType;
    }

    @Override
    public DoodleEnum.SelectionMode getSelectionMode() {
        // TODO Auto-generated method stub
        return mSelectionMode;
    }

    @Override
    public DoodleView getDoodleView() {
        // TODO Auto-generated method stub
        return this;
    }

    @Override
    public boolean isShapeRecognition() {
        // TODO Auto-generated method stub
        return mShapeRecognizetionHelper.isShapeRecognition();
    }

    @Override
    public IShapeRecognizeManager getShapeRecognizeManager() {
        // TODO Auto-generated method stub
        return mShapeRecognizeManager;
    }

    @Override
    public void addSelectionModeChangedListener(
            ISelectionModeChangedListener listener) {
        // TODO Auto-generated method stub
        mSelectionModeChangedListeners.add(listener);
    }

    @Override
    public void removeSelectionModeChangedListener(
            ISelectionModeChangedListener listener) {
        // TODO Auto-generated method stub
        mSelectionModeChangedListeners.remove(listener);
    }

    /*********************************************
     * IInternaleDoodle接口实现结束
     ********************************************/

    /*********************************************
     * IModelManager.ISelectedChangedListener接口实现开始
     ********************************************/
    // @Override
    // public void onSelected(InsertableObjectBase insertableObject) {
    // // TODO Auto-generated method stub
    // setSelectionMode(SelectionMode.SELECTION);
    // // mCommandsManager.disable();// 选中状态禁用undo,redo
    // }
    //
    // @Override
    // public void onUnSelected(InsertableObjectBase insertableObject) {
    // // TODO Auto-generated method stub
    // setSelectionMode(SelectionMode.NONE);
    // // mCommandsManager.enable();
    // }

    /*************************************************************
     * IModelManager.ISelectedChangedListener接口实现结束
     ************************************************************/

    // /***
    // * 向DoodleView添加一个数据对象 由于整个Asus Draw框架是数据驱动ui的，添加一个数据对象之后，会自动加载其对应的visual
    // * element
    // *
    // * @param insertableObject
    // */
    // public void addInsertableObject(InsertableObjectBase insertableObject) {
    // mModelManager.addInsertableObject(insertableObject);
    // }

    /**
     * 返回false的情况如下:1.系统不支持图形识别;2.DoodleView
     * 当前InputMode为EARSE;3.当前的InternalInputMode为SELECTING
     *
     * @return：如果成功，则返回true；失败返回false。
     */
    public boolean startShapeRecognition() {
        return mShapeRecognizetionHelper.startShapeRecognition();
    }

    public void stopShapeRecognition() {
        mShapeRecognizetionHelper.stopShapeRecognition();
    }

    public void addSRAvailabilityChangedListener(
            ISRAvailabilityChangedListener listener) {
        mShapeRecognizetionHelper.addSRAvailabilityChangedListener(listener);
    }

    public void removeSRAvailabilityChangedListener(
            ISRAvailabilityChangedListener listener) {
        mShapeRecognizetionHelper.removeSRAvailabilityChangedListener(listener);
    }

    public void setSelectionMode(DoodleEnum.SelectionMode selectionMode) {
        if (mSelectionMode == selectionMode)
            return;
        DoodleEnum.SelectionMode oldValue = mSelectionMode;
        mSelectionMode = selectionMode;
        mShapeRecognizetionHelper.resetSRAvailable();
        fireSelectionModeChanged(oldValue, mSelectionMode);
    }

    protected void fireSelectionModeChanged(DoodleEnum.SelectionMode oldValue,
                                           DoodleEnum.SelectionMode newValue) {
        for (ISelectionModeChangedListener listener : mSelectionModeChangedListeners) {
            listener.onSelectionModeChanged(oldValue, newValue);
        }
    }

    /**
     * 退出选中模式
     */
    public void exitSelectionMode() {
        // TODO Auto-generated method stub
        mModelManager.exitSelectionMode();
    }

    /**
     * 进入选中模式。进入选中模式并不总是会成功，如果成功，则会发送{@link SelectionModeChangedListener}通知
     *
     * @return
     */
    public boolean enterSelectionMode() {
        return mModelManager.enterSelectionMode();
    }

    /**
     * 系统是否支持图形识别
     *
     * @return
     */
    public boolean canShapeRecognition() {
        return mShapeRecognizetionHelper.canShapeRecognition();
    }


}
