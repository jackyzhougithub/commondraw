package com.jacky.commondraw.visual.brush;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;

import com.jacky.commondraw.R;
import com.jacky.commondraw.model.InsertableObjectBase;
import com.jacky.commondraw.views.doodleview.IInternalDoodle;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 * 铅笔。
 */
public class VisualStrokePencil extends VisualStrokePath {
    Bitmap mTexture = null;
    private Paint mTexturePaint;

    public VisualStrokePencil(Context context, IInternalDoodle internalDoodle,
                              InsertableObjectBase object) {
        super(context, internalDoodle, object);
        // TODO Auto-generated constructor stub
        initTexture();
        setTexture(mTexture);
    }

    private void initTexture() {
        mTexture = BitmapFactory.decodeResource(
                mContext.getResources(),
                R.drawable.pencil);
    }

    private void setTexture(Bitmap texture) {
        Canvas canvas = new Canvas();
        Bitmap result = Bitmap.createBitmap(texture.getWidth(),
                texture.getHeight(), Bitmap.Config.ARGB_8888);
        result.eraseColor(Color.rgb(Color.red(mPaint.getColor()),
                Color.green(mPaint.getColor()), Color.blue(mPaint.getColor())));
        canvas.setBitmap(result);

        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(texture, 0, 0, paint);

        mTexturePaint = new Paint(mPaint);
        BitmapShader fillBMPshader = new BitmapShader(result,
                Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        mTexturePaint.setStyle(Paint.Style.STROKE);
        mTexturePaint.setStrokeCap(Paint.Cap.ROUND);
        mTexturePaint.setStrokeJoin(Paint.Join.ROUND);
        mTexturePaint.setMaskFilter(null);
        mTexturePaint.setXfermode(null);
        mTexturePaint.setPathEffect(null);

        mTexturePaint.setAlpha(0xFF);
        mTexturePaint.setShader(fillBMPshader);
    }

    @Override
    public void draw(Canvas canvas) {
        if (canvas == null)
            return;
        canvas.drawPath(mPath, mTexturePaint);
    }
}

