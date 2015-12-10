package com.jacky.commondraw.views.cropimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.jacky.commondraw.listeners.IOutSideTouchListener;
import com.jacky.commondraw.views.selectview.LayerView;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class CropImageView extends LayerView implements ICropImage.IRefresh {
    public static interface ICropImageViewInited{
        void onInited(CropImageView view);
    }
    private Bitmap mBitmap = null;
    private Paint mDrawPaint = null;

    public CropImageView(Context context) {
        super(context);
    }

    public CropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CropImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        mMeasureInit = false;
        mDrawPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        requestLayout();
        invalidate();
    }
    /**
     * The bitmap will be scaled for view width or height
     * @return
     */
    public Bitmap getBitmap(){
        return mBitmap;
    }
    /**
     *
     * @return Rect , relative to the view
     */
    public Rect getBitmapRenderRect(){
        Rect rect = null;
        if (mMeasureInit) {
            int left = getPaddingLeft();
            int top = getPaddingTop();
            rect = new Rect(left, top, left+mBitmap.getWidth(), top+mBitmap.getHeight());
        }
        return rect;
    }
    private boolean mMeasureInit = false;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mBitmap == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            if (!mMeasureInit) {
//				final float maxWidth = MeasureSpec.getSize(widthMeasureSpec)-getPaddingLeft()-getPaddingRight();
//				final float maxHeight = MeasureSpec.getSize(heightMeasureSpec)-getPaddingBottom()-getPaddingTop();
//				final float srcWidth = mBitmap.getWidth();
//				final float srcHeight = mBitmap.getHeight();
//				if (srcHeight > maxHeight || srcWidth > maxWidth) {
//					float scal = (srcHeight / maxHeight) > (srcWidth / maxWidth) ? srcHeight
//							/ maxHeight
//							: srcWidth / maxWidth;
//					final Bitmap bitmap = BitmapLoad.zoomBitmap(mBitmap, scal,
//							scal);
//					if (bitmap!=null) {
//						mBitmap = bitmap;
//					}
//				}
                mMeasureInit = true;
                new Handler().post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if (mInitedListener!=null) {
                            mInitedListener.onInited(CropImageView.this);
                        }
                    }
                });
            }

            setMeasuredDimension(View.MeasureSpec.makeMeasureSpec(
                            mBitmap.getWidth(), View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.makeMeasureSpec(mBitmap.getHeight(),
                            View.MeasureSpec.AT_MOST));
        }
    }
    @Override
    public void refresh() {
        // TODO Auto-generated method stub
        invalidate();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (event.getActionMasked()==MotionEvent.ACTION_OUTSIDE) {
            if (mOutSideTouchListener!=null) {
                mOutSideTouchListener.onOutSideTouch();
            }
        }else {
            if (mCropImage != null) {
                return mCropImage.onEventAction(this, event);
            }
        }
        return super.onTouchEvent(event);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        if (mBitmap!=null&&mMeasureInit) {
            canvas.drawBitmap(mBitmap, getMatrix(), mDrawPaint);
            if (mCropImage != null) {
                mCropImage.onDrawSelf(canvas);
            }
        }
    }

    private ICropImage mCropImage = null;

    public void setCropImage(ICropImage cropImage) {
        mCropImage = cropImage;
    }

    private ICropImageViewInited mInitedListener = null;

    public void setCropInitedListener(ICropImageViewInited listener) {
        mInitedListener = listener;
    }

    private IOutSideTouchListener mOutSideTouchListener = null;

    public void setOutSideListener(IOutSideTouchListener listener){
        mOutSideTouchListener = listener;
    }
}

