package com.jacky.commondraw.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.DisplayMetrics;

import java.io.File;
import java.io.IOException;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class LoadUtils {
    public enum LoadType{
        Sampled,
        Raw;
    }

    public static class LoadResult{
        public Bitmap bitmap;
        public int rawWidth = 0;
        public int rawHeight = 0;
        public int sampledWidth = 0;
        public int sampledHeight = 0;
        public int sampleSize =1;
        public boolean successed = false;
    }
    private interface ILoadMeth{
        LoadResult loadByPath(String path);
    }

    private class ResolutionLoadMeth implements ILoadMeth{
        public  ResolutionLoadMeth(){}// for reflect
        @Override
        public LoadResult loadByPath(String path) {
            // TODO Auto-generated method stub
            LoadResult loadResult = new LoadResult();
            if (new File(path).exists()) {
                int degree = readPictureDegree(path);
                DisplayMetrics display = mContext.getResources().getDisplayMetrics();
                int reqw = display.widthPixels;
                int reqh = display.heightPixels;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path,options);
                final int rawHeight = options.outHeight;
                final int rawWidth = options.outWidth;
                options.inSampleSize = calculateInSampleSize(options, reqw, reqh);
//				long newCount= rawHeight*rawWidth/options.inSampleSize;
//				if (newCount<getCurrentRuntimeHeapFreeSize()) {
//					options.inJustDecodeBounds = false;
//					loadResult.bitmap=BitmapFactory.decodeFile(path, options);
//					loadResult.rawHeight = rawHeight;
//					loadResult.rawWidth = rawWidth;
//					loadResult.sampledHeight = loadResult.bitmap.getHeight();
//					loadResult.sampledWidth = loadResult.bitmap.getWidth();
//					loadResult.sampleSize = options.inSampleSize;
//					loadResult.successed = true;
//				}
                options.inJustDecodeBounds = false;
                loadResult.bitmap=BitmapFactory.decodeFile(path, options);
                if (degree!=0) {
                    Bitmap tempBitmap = loadResult.bitmap;
                    loadResult.bitmap = rotatBitmap(degree, tempBitmap);
                    loadResult.rawHeight = rawWidth;
                    loadResult.rawWidth = rawHeight;
                    tempBitmap.recycle();
                    tempBitmap = null;
                }else {
                    loadResult.rawHeight = rawHeight;
                    loadResult.rawWidth = rawWidth;
                }
                loadResult.sampledHeight = loadResult.bitmap.getHeight();
                loadResult.sampledWidth = loadResult.bitmap.getWidth();
                loadResult.sampleSize = options.inSampleSize;
                loadResult.successed = true;
            }
            return loadResult;
        }
        int calculateInSampleSize(BitmapFactory.Options options,int reqWidth,int reqHeight){
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;
            reqWidth = reqWidth<1?1:reqWidth;
            reqHeight = reqHeight<1?1:reqHeight;
            if (height>reqHeight&&width>reqWidth) {
                float sx = width/reqWidth;
                float sy = height/reqHeight;
                inSampleSize = sx>sy?(int)sx:(int)sy;
                return inSampleSize ;
            }
            if (height>reqHeight) {
                float sy = height/reqHeight;
                inSampleSize = (int)sy;
                return inSampleSize;
            }
            if (width>reqWidth) {
                float sx = width/reqWidth;
                inSampleSize = (int)sx;
                return inSampleSize;
            }

            return inSampleSize;
        }

    }
    private class RawLoadMeth implements ILoadMeth{
        public RawLoadMeth(){}// for reflect
        @Override
        public LoadResult loadByPath(String path) {
            // TODO Auto-generated method stub
            LoadResult loadResult = new LoadResult();
            File bitmapFile = new File(path);
            if (bitmapFile.exists()&&isMemoryAllow(path)) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                int degree = readPictureDegree(path);
                if (null != bitmap) {
                    if (degree!=0) {
                        Bitmap tempBitmap = bitmap;
                        bitmap = rotatBitmap(degree, tempBitmap);
                        tempBitmap.recycle();
                        tempBitmap = null;
                    }
                    loadResult.sampledHeight=loadResult.rawHeight = bitmap.getHeight();
                    loadResult.sampledWidth=loadResult.rawWidth  = bitmap.getWidth();
                    loadResult.bitmap = bitmap;
                    loadResult.successed = true;
                }
            }
            return loadResult;
        }

    }

    private static LoadUtils sBitmapLoad = null;
    public static  LoadUtils getInstance(Context context){
        if (sBitmapLoad == null) {
            sBitmapLoad = new LoadUtils(context);
        }
        return sBitmapLoad;
    }
    private Context mContext = null;
    private LoadUtils(Context context){
        mContext = context;
    }

    public LoadResult loadBitmapByPath(String path,LoadType type){
        if (null == path || null == type) {
            return null;
        }
        LoadResult loadResult = null;
        ILoadMeth meth = null;
        switch (type) {
            case Sampled:
                meth = new ResolutionLoadMeth();
                break;
            default:
                meth = new RawLoadMeth();
                break;
        }
        loadResult = meth.loadByPath(path);
        return loadResult;
    }
    public LoadResult loadBitmapByPath(String path,int sampleSize){
        LoadResult loadResult = new LoadResult();
        if (new File(path).exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = sampleSize;
            loadResult.bitmap=BitmapFactory.decodeFile(path,options);
            int degree = readPictureDegree(path);
            if (degree!=0) {
                Bitmap temBitmap = loadResult.bitmap;
                loadResult.bitmap=rotatBitmap(degree, temBitmap);
            }
            loadResult.sampledHeight = loadResult.bitmap.getHeight();
            loadResult.sampledWidth = loadResult.bitmap.getWidth();
            loadResult.sampleSize = options.inSampleSize;
            loadResult.successed = true;
        }
        return loadResult;
    }
    private long getCurrentRuntimeHeapFreeSize(){
        Runtime runtime = Runtime.getRuntime();
        return runtime.freeMemory();
    }
    private boolean isMemoryAllow(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);
        long bytesCount = options.outHeight*options.outWidth;
        long bytesFree = getCurrentRuntimeHeapFreeSize();
        if (bytesCount<bytesFree) {
            return true;
        }else {
            return false;
        }
    }

    public static Bitmap zoomBitmap(Bitmap bitmap, float sx, float sy) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        return newbmp;
    }
    static int readPictureDegree(String path) {
        int degree  = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }
    static Bitmap rotatBitmap(int angle , Bitmap bitmap) {
        Matrix matrix = new Matrix();;
        matrix.postRotate(angle);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }
}
