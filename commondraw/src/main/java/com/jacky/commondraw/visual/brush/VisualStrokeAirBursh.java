package com.jacky.commondraw.visual.brush;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.jacky.commondraw.model.InsertableObjectBase;
import com.jacky.commondraw.views.doodleview.IInternalDoodle;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 * 喷枪笔
 */
public class VisualStrokeAirBursh extends VisualStrokePath {
    private float mCurrentStrokeWidth = 0;
    private BlurMaskFilter mBlurMaskFilter = null;

    public VisualStrokeAirBursh(Context context,
                                IInternalDoodle internalDoodle, InsertableObjectBase object) {
        super(context, internalDoodle, object);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void draw(Canvas canvas) {
        if (canvas == null)
            return;

        Paint paint = new Paint(mPaint);
        float strokeWidth = paint.getStrokeWidth();

        if (strokeWidth != mCurrentStrokeWidth) {
            float blurRadius = strokeWidth * 0.2f;
            mBlurMaskFilter = new BlurMaskFilter(blurRadius,
                    BlurMaskFilter.Blur.NORMAL);
            mCurrentStrokeWidth = strokeWidth;
        }

        paint.setMaskFilter(mBlurMaskFilter);
        paint.setStrokeWidth(strokeWidth);

        canvas.drawPath(mPath, paint);
    }
}

