package com.jacky.commondraw.wigets.drawpickers;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.jacky.commondraw.R;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class ColorPickerViewCustom extends View {
    private Paint mPaint;

    private int mCurrentX = 0, mCurrentY = 0;
    private int mCurrentColor, mDefaultColor;
    private float mCurrentHue = 0;
    private final int[] mHueBarColors = new int[258];
    private int[] mMainColors = new int[36000];
    private ColorChange ColorListener = null;
    public int mcolor;
    public boolean IsFirst = true;
    public static int times=0;
    private int mColorPickerWidth = 0, mColorPickerHeight = 0;
    private SharedPreferences mSharedPreference = null;
    private Bitmap bitmap = null;
    private Bitmap mColorBitmap = null; //smilefish
    private int DPI =0;
    public ColorPickerViewCustom(Context c, AttributeSet attrSet) {
        super(c, attrSet);

//        mColorPickerWidth = getResources().getInteger(R.integer.color_picker_width);
//        mColorPickerHeight = getResources().getInteger(R.integer.color_picker_height);
//        DPI=getResources().getDisplayMetrics().densityDpi;
        mColorPickerWidth = c.getResources().getInteger(R.integer.color_picker_width);
        mColorPickerHeight = c.getResources().getInteger(R.integer.color_picker_height);
        DPI=c.getResources().getDisplayMetrics().densityDpi;
        // END: Shane_Wang 2012-9-25 ????10:18:49

        int defaultColor = 0xFF3299CC;
        mDefaultColor = defaultColor;

        int color = 0xFF3299CC;
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        mCurrentHue = hsv[0];
        updateMainColors();
        mCurrentColor = color;
        int index = 0;
        for (int k = 0; k < 6; k++) {
            for (float i = 0; i < 256; i += 256 / 42) {
                switch (k) {
                    case 0:
                        mHueBarColors[index] = Color.rgb(255, 0, (int) i);
                        break;
                    case 1:
                        mHueBarColors[index] = Color.rgb(255 - (int) i, 0, 255);
                        break;
                    case 2:
                        mHueBarColors[index] = Color.rgb(0, (int) i, 255);
                        break;
                    case 3:
                        mHueBarColors[index] = Color.rgb(0, 255, 255 - (int) i);
                        break;
                    case 4:
                        mHueBarColors[index] = Color.rgb((int) i, 255, 0);
                        break;
                    case 5:
                        mHueBarColors[index] = Color.rgb(255, 255 - (int) i, 0);
                        break;
                }
                index++;
            }
        }

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(12);

        bitmap = creatColorPickerBitmap();

        mSharedPreference = c.getSharedPreferences(MetaData.PREFERENCE_NAME, Context.MODE_PRIVATE);
        mCurrentX = mSharedPreference.getInt(MetaData.PREFERENCE_PALETTE_COLORX, 0) ;
        mCurrentY = mSharedPreference.getInt(MetaData.PREFERENCE_PALETTE_COLORY, 0) ;

        Drawable d =c.getResources().getDrawable(R.drawable.color_selector);
        mColorBitmap = ((BitmapDrawable)d).getBitmap();
    }


    private void updateMainColors() {
        int index = 0;
        float v;
        float y;
        for (int t = 1; t < 361; t++ ) {

            y = (t-1) ;
            for (int x = 1; x < 101; x++) {


                int color = HSVtoRGB(y, (float)1, (float)1);
                int red = Color.red(color);
                int green = Color.green(color);
                int blue = Color.blue(color);

                if(x<50)
                {
                    red = red + (255 -red) * (50 - x) / 50;
                    green = green + (255 -green) * (50 - x) / 50;
                    blue = blue + (255 -blue) * (50 - x) / 50;
                    mMainColors[index] = Color.rgb(red, green, blue);

                }
                else
                {
                    red = (100-x) * red / 50;
                    green = (100 -x) * green /50;
                    blue = (100 -x) * blue /50;
                    mMainColors[index] = Color.rgb(red, green, blue);
                }

                //mMainColors[index] = HslToRgb(y,100,100);
                index++;
            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, new Paint());

        Matrix matrix = new Matrix();
        int width = mColorBitmap.getWidth() / 2;
        int height = mColorBitmap.getHeight() / 2;
        matrix.setTranslate(mCurrentX - width, mCurrentY - height);
        canvas.drawBitmap(mColorBitmap, matrix, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((mColorPickerWidth * DPI / 160), (mColorPickerHeight * DPI / 160));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();


        if (x > 0 && x < (mColorPickerWidth * DPI / 160) && y > 0 && y < (mColorPickerHeight * DPI / 160)) {
            mCurrentX = (int) x;
            mCurrentY = (int) y;
            int transX = mCurrentX * 360 / (mColorPickerWidth * DPI / 160);
            int transY = mCurrentY * 100 / (mColorPickerHeight * DPI / 160);
            int index = transY + transX * 100;
            if (index > 0 && index < mMainColors.length) {
                mCurrentColor = mMainColors[index];
                invalidate();
            }
        }

        mSharedPreference.edit().putInt(MetaData.PREFERENCE_PALETTE_COLORX, mCurrentX).commit();
        mSharedPreference.edit().putInt(MetaData.PREFERENCE_PALETTE_COLORY, mCurrentY).commit();
        if(ColorListener != null)
            ColorListener.OnColorChange(mCurrentColor);
        return true;
    }

    int HSVtoRGB(float h /* 0~360 degrees */, float s /* 0 ~ 1.0 */, float v /** 0* ~* 1.0*/) {
        float f, p, q, t;
        if (s == 0) { // achromatic (grey)
            //return makeColor(v, v, v);
        }


        h /= 60; // sector 0 to 5
        int i = (int) Math.floor(h);
        f = (h - i); // factorial part of h
        p = (v * (1 - s));
        q = (v * (1 - s * f));
        t = (v * (1 - s * (1 - f)));
        switch (i) {
            case 0:
                return makeColor(v, t, p);
            case 1:
                return makeColor(q, v, p);
            case 2:
                return makeColor(p, v, t);
            case 3:
                return makeColor(p, q, v);
            case 4:
                return makeColor(t, p, v);
            default: // case 5:
                return makeColor(v, p, q);
        }
    }

    public int makeColor(float r, float g, float b) {


        int R = (int) (r * 255);
        int G = (int) (g * 255);
        int B = (int) (b * 255);

        if(R>100 && G>200 &&B>200)
            this.times++;
        return Color.rgb(R, G, B);

    }

    //begin smilefish
    public int getInitColor()
    {
        mCurrentColor = mMainColors[0];
        return mCurrentColor;
    }
    //end smilefish

    public void SetListener(ColorChange colorChange) {
        ColorListener = colorChange;
    }

    public static interface ColorChange {
        void OnColorChange(int Color);
    }

    private Bitmap creatColorPickerBitmap()
    {
        Bitmap colorBitmap = Bitmap.createBitmap((mColorPickerWidth * DPI / 160),
                (mColorPickerHeight * DPI / 160), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(colorBitmap);
        for (int x = 0; x < (mColorPickerWidth * DPI / 160); x++) {
            int[] colors = new int[10];
            int j = x * 360 / (mColorPickerWidth * DPI / 160) ;
            int y = 100 * j;
            for (int i = 0; i < 10; i++, y = y + 10)
                colors[i] = mMainColors[y];

            Shader shader = new LinearGradient(0, 0, 0, (mColorPickerHeight * DPI / 160), colors, null,
                    Shader.TileMode.CLAMP);
            mPaint.setShader(shader);
            canvas.drawLine(x, 0, x, (mColorPickerHeight * DPI / 160), mPaint);
        }
        mPaint.setShader(null);
        return colorBitmap;
    }

    //Begin smilefish
    public void setColorXY(int x, int y)
    {
        mCurrentX = x;
        mCurrentY = y;
        this.invalidate();
    }
    //End smilefish

    // Carrot: individually set color of every brush
    public int getCurX(){
        return mCurrentX;
    }

    public int getCurY(){
        return mCurrentY;
    }
    // Carrot: individually set color of every brush

}
