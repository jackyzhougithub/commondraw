package com.jacky.commondraw.manager.modelmager;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import java.util.LinkedList;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 * 该类通过对motionEvent的装饰，进一步实现long click功能
 */
public class LongClickDetector {
    public static final int LONG_CLICK_TIMER = ViewConfiguration
            .getLongPressTimeout();// 1500;
    private static final float TOUCH_TOLERANCE = 40;
    private Path mPath = new Path();
    private CountDown mLongClickCountDown = new CountDown(LONG_CLICK_TIMER,
            LONG_CLICK_TIMER);
    private LinkedList<OnLongClickListener> mLongClickListeners = new LinkedList<OnLongClickListener>();
    private MotionEvent mDownEvent = null;

    public void onTouch(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownEvent = MotionEvent.obtain(event);
                mLongClickCountDown.start();
                mPath.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(x, y);
                if (computerPathLength(mPath) > TOUCH_TOLERANCE) {
                    mLongClickCountDown.cancel();
                }
                break;
            case MotionEvent.ACTION_UP:
                mLongClickCountDown.cancel();
                mPath.reset();
                break;
        }
    }

    public static float computerPathLength(Path path) {
        if (path == null)
            return 0;
        PathMeasure pathMeasure = new PathMeasure(path, false);
        return pathMeasure.getLength();
    }

    public void addLongClickListener(OnLongClickListener listener) {
        mLongClickListeners.add(listener);
    }

    public void removeLongClickListener(OnLongClickListener listener) {
        if (mLongClickListeners.contains(listener))
            mLongClickListeners.remove(listener);
    }

    private class CountDown extends CountDownTimer {
        public CountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            if (mLongClickListeners != null) {
                for (OnLongClickListener listener : mLongClickListeners) {
                    listener.onLongClick(mDownEvent);
                }
            }
            mPath.reset();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // TODO Auto-generated method stub
        }
    }

    public  interface OnLongClickListener {
         void onLongClick(MotionEvent event);
    }

}
