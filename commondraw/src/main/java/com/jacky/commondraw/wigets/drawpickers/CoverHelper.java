package com.jacky.commondraw.wigets.drawpickers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class CoverHelper {
    public static Bitmap DefaultCover = null;

    public static void initDefaultCoverBitmap(int color, int line,Resources res){
//		if(DefaultCover!=null){
//			DefaultCover.recycle();
//			DefaultCover = null;
//		}
//		DefaultCover = getDefaultCoverBitmap(color, line,res);
    }


    public static Drawable createGradientColorAndCover(Context context,int coveredResID,int CoverColor){
        //final Resources res=context.getResources();
        final Resources res = context.getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, coveredResID);
        if (bitmap==null) {
            return null;
        }
        final int length=bitmap.getWidth();
        final int height=bitmap.getHeight();
        int[] shape=new int[length*height];
        int[] colors=new int[length*height];
        bitmap.getPixels(shape, 0, length, 0, 0, length, height);
        int color=CoverColor;
        for (int i=0 ;i<length*height;i++) {
            float percent=((float)i%length/length)*0xff;
            int alpha=((int)percent<<6*4);
            alpha&=shape[i] & 0xFF000000;
            colors[i]=(alpha)|(color&0x00FFFFFF);
        }
        Bitmap newbitmap =  Bitmap.createBitmap(length, height, Bitmap.Config.ARGB_8888);
        newbitmap.setPixels(colors, 0, length, 0, 0, length, height);
        Bitmap fooBitmap=Bitmap.createBitmap(length, height, Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(fooBitmap);
        Paint paint=new Paint();
        canvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.drawBitmap(newbitmap, 0, 0, paint);
        newbitmap.recycle();
        bitmap.recycle();
        return new BitmapDrawable(res, fooBitmap);
    }
}
