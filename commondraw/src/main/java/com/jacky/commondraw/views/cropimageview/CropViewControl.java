package com.jacky.commondraw.views.cropimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.jacky.commondraw.listeners.IOutSideTouchListener;
import com.jacky.commondraw.model.InsertableBitmap;
import com.jacky.commondraw.utils.FileUtils;
import com.jacky.commondraw.views.doodleview.DoodleView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class CropViewControl {
    public enum CropMode {
        NONE, FREE, AUTO
    }

    public class CropResultInfo {
        public String path = null;
        public Matrix posMatrix = null;
        public int height = 0;
        public int width = 0;
    }

    private Context mContext = null;
    private View mOverView = null;
    private CropImageView mCropImageView = null;
    private InsertableBitmap mDataBitmap = null;
    private WindowManager mWindowManager = null;
    private AutoCropImage mAutoCropImage = null;
    private FreeCropImage mFreeCropImage = null;
    private ICropImage mCropImage = null;
    private CropMode mCropMode = CropMode.NONE;
    private IOutSideTouchListener mOutSideTouchListener = null;
    private WindowManager.LayoutParams mLayoutParams = null;
    private Bitmap mSrcBitmap = null;
    private boolean mInited = false;
    private Rect mVisualRect = null;// position in mOverView

    public CropViewControl(Context context, View overView,
                           InsertableBitmap data, Bitmap srcBitmap) {
        mContext = context;
        mOverView = overView;
        mDataBitmap = data;
        mSrcBitmap = srcBitmap;
        mWindowManager = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        initLayoutParams();
    }

    private void initLayoutParams() {
        mLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSPARENT);
        mLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    public InsertableBitmap getBindData() {
        return mDataBitmap;
    }

    /**
     * Get the new cropped image path and new poistion info(in matrix)
     *
     * @return CropResultInfo
     */
    public CropResultInfo getCropedImageInfo() {
        CropResultInfo resultInfo = null;
        if (mInited && mCropMode != CropMode.NONE) {
            final ICropImage cropImage = mCropImage;
            Bitmap srcBitmap = getCropSrcBitmap(false);// get the source bitmap
            // to crop
            // storeBitmap(srcBitmap);
            if (srcBitmap != null) {
                resultInfo = new CropResultInfo();
                Bitmap bitmap = Bitmap.createBitmap(srcBitmap.getWidth(),
                        srcBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                canvas.drawColor(Color.TRANSPARENT);
                Rect region = cropImage.clipImageRegion(canvas);
                if (region != null) {
                    resultInfo.posMatrix = new Matrix();
                    resultInfo.posMatrix.setTranslate(mOverView.getScrollX(),
                            mOverView.getScrollY());
                    resultInfo.posMatrix.preTranslate(mVisualRect.left
                            + region.left, mVisualRect.top + region.top);
                    canvas.drawBitmap(srcBitmap, new Matrix(), new Paint(
                            Paint.ANTI_ALIAS_FLAG));
                    Bitmap resultBitmap = Bitmap.createBitmap(
                            bitmap,
                            region.left,
                            region.top,
                            region.width() < bitmap.getWidth() ? region.width()
                                    : bitmap.getWidth(),
                            region.height() < bitmap.getHeight() ? region
                                    .height() : bitmap.getHeight());

                    resultInfo.path = storeBitmap(resultBitmap);
                    resultInfo.height = resultBitmap.getHeight();
                    resultInfo.width = resultBitmap.getWidth();
                    resultBitmap.recycle();
                    resultBitmap = null;
                } else {
                    resultInfo = null;
                }
                srcBitmap.recycle();
                srcBitmap = null;
                bitmap.recycle();
                bitmap = null;
            }
        }
        return resultInfo;
    }

    private String storeBitmap(Bitmap resultBitmap) {
        // TODO Auto-generated method stub
        File parent = new File(FileUtils.CROP_IMAGES_DIR);
        if (!parent.exists()) {
            parent.mkdirs();
        }
        File bpFile = new File(FileUtils.CROP_IMAGES_DIR
                + System.currentTimeMillis() + ".png");
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(bpFile);
            resultBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }
        return bpFile.getAbsolutePath();
    }

    public void setOutSideTouchListener(IOutSideTouchListener listener) {
        mOutSideTouchListener = listener;
        if (mCropImageView != null) {
            mCropImageView.setOutSideListener(listener);
        }
    }

    /**
     * Choice the crop mode thought the CropMode; Please set CropMode.NONE when
     * quit out or never use the crop view!
     *
     * @param mode
     */
    public void setCropMode(CropMode mode) {
        mCropMode = mode;
        if (mode == CropMode.FREE) {
            ensureShowCropImage();
            mCropImage = mFreeCropImage;
        } else if (mode == CropMode.AUTO) {
            ensureShowCropImage();
            mCropImage = mAutoCropImage;
        } else {
            mCropImage = null;
            dimissCropImage();
        }
        if (mCropImageView != null) {
            mCropImageView.setCropImage(mCropImage);
            mCropImageView.refresh();
        }
    }

    public CropMode getCurrentCropMode() {
        return mCropMode;
    }

    /**
     * After call this meth,the object must not be used;
     */
    public void releaseResource() {
        if (mSrcBitmap != null && !mSrcBitmap.isRecycled()) {
            mSrcBitmap.recycle();
            mSrcBitmap = null;
        }
        setCropMode(CropMode.NONE);
        mInited = false;
    }

    private void ensureShowCropImage() {
        if (mCropImageView == null && !mInited) {
            mInited = false;
            mCropImageView = new CropImageView(mContext);
            Bitmap bitmap = getCropSrcBitmap(true);
            if (bitmap != null && mVisualRect != null) {
                mCropImageView.setBitmap(bitmap);
                mCropImageView
                        .setCropInitedListener(new CropImageView.ICropImageViewInited() {

                            @Override
                            public void onInited(CropImageView view) {
                                // TODO Auto-generated method stub
                                mInited = true;
                                mAutoCropImage = new AutoCropImage(view
                                        .getBitmapRenderRect(), view);
                                mFreeCropImage = new FreeCropImage(view
                                        .getBitmapRenderRect(), view, view
                                        .getContext());
                                setCropMode(mCropMode);
                                view.setOutSideListener(mOutSideTouchListener);
                            }
                        });
                int[] ps = new int[2];
                mOverView.getLocationOnScreen(ps);
                mLayoutParams.x = ps[0] + mVisualRect.left;
                mLayoutParams.y = ps[1] + mVisualRect.top;
                mWindowManager.addView(mCropImageView, mLayoutParams);
            } else {
                mCropImageView = null;
            }
        }
    }

    // We need to get the rect that contain the bitmap ,and it must be visiable
    // ,that the intersection between the
    // overview visual rect and the rect cotain the bitmap.Then we move the
    // bitmap rect to (0,0),and draw it on our canvas
    private Bitmap getCropSrcBitmap(boolean onlyFrame) {
        Rect visualRect = new Rect();
        mOverView.getLocalVisibleRect(visualRect);
        Rect clipRect = new Rect(visualRect);

        Matrix matrix = new Matrix(mDataBitmap.getMatrix());
        matrix.postTranslate(-mOverView.getScrollX(), -mOverView.getScrollY());
        RectF bitmapRectF = mDataBitmap.getInitRectF();
        // matrix.mapRect(bitmapRectF);// get the RectF that contain all bitmap
        // the init rect four angle
        float ps[] = new float[] { bitmapRectF.left, bitmapRectF.top,
                bitmapRectF.right, bitmapRectF.top, bitmapRectF.right,
                bitmapRectF.bottom, bitmapRectF.left, bitmapRectF.bottom };
        matrix.mapPoints(ps);// convert to new position
        Path bitmapOutlinePath = new Path();// the path will record the new rect
        bitmapOutlinePath.moveTo(ps[0], ps[1]);
        bitmapOutlinePath.lineTo(ps[2], ps[3]);
        bitmapOutlinePath.lineTo(ps[4], ps[5]);
        bitmapOutlinePath.lineTo(ps[6], ps[7]);
        bitmapOutlinePath.lineTo(ps[0], ps[1]);

        bitmapOutlinePath.computeBounds(bitmapRectF, false);// get the smallest
        // rect that can
        // contain the path
        Rect bitmapRect = new Rect((int) bitmapRectF.left,
                (int) bitmapRectF.top, (int) (bitmapRectF.right + 0.5f),
                (int) (bitmapRectF.bottom + 0.5f));

        if (clipRect.intersect(bitmapRect)) {// this meth will make the clipRect
            // ,which is the intersect between
            // bitmapRect and parent visual
            // rect
            Region clipRegion = new Region(clipRect);
            Region pathRegion = new Region();
            pathRegion.setPath(bitmapOutlinePath, clipRegion);
            Rect visualSmallestRect = pathRegion.getBounds();// we will get the
            // smallest Rect
            // that contain the
            // bitmap which is
            // on screen and
            // visiable
            if (!visualSmallestRect.isEmpty()) {
                mVisualRect = new Rect(visualSmallestRect);// init for window
                // position in
                // mOverView
                Bitmap canvasBitmap = Bitmap.createBitmap(
                        visualSmallestRect.width(),
                        visualSmallestRect.height(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(canvasBitmap);
                canvas.drawColor(Color.TRANSPARENT);
                if (!onlyFrame) {
                    matrix.postTranslate(-visualSmallestRect.left,
                            -visualSmallestRect.top);// draw src bitmap on
                    // visualSmallestRect
                    canvas.drawBitmap(mSrcBitmap, matrix, new Paint(
                            Paint.ANTI_ALIAS_FLAG));
                }
                return canvasBitmap;
            }
        }
        return null;
    }

    private void dimissCropImage() {
        mInited = false;
        mVisualRect = null;
        if (mCropImageView != null) {
            mWindowManager.removeView(mCropImageView);
            mCropImageView = null;
        }
    }

    /**
     * 完成图片剪辑
     */
    public void finishCrop(final DoodleView doodleView) {
        if (getCurrentCropMode() != CropMode.NONE) {
            new Handler().post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method
                    // stub
                    final InsertableBitmap data = getBindData();
                    if (getCurrentCropMode() != CropMode.NONE) {
                        CropViewControl.CropResultInfo resultInfo = getCropedImageInfo();
                        if (resultInfo != null) {
                            InsertableBitmap newData = new InsertableBitmap();
                            newData.setHeight(resultInfo.height);
                            newData.setWidth(resultInfo.width);
                            newData.setBitmapSampleSize(1);
                            newData.setAttachFilePath(resultInfo.path);
                            newData.setInitRectF(new RectF(0f, 0f,
                                    resultInfo.width, resultInfo.height));
                            newData.getMatrix().set(resultInfo.posMatrix);
                            doodleView.getModelManager()
                                    .removeInsertableObject(data, true);// fromUndoRedo:设置为true，让该步骤不产生Icommand
                            doodleView.getModelManager().addInsertableObject(
                                    newData, true);// fromUndoRedo:设置为true，让该步骤不产生Icommand
                            CropEndOperation operation = new CropEndOperation(
                                    doodleView.getFrameCache(), doodleView
                                    .getModelManager(), doodleView
                                    .getVisualManager(), data, newData);
                            doodleView.insertOperation(operation);
                        }
                    }
                    releaseResource();
                }
            });
        }
    }
}
