package com.jacky.commondraw.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class FileUtils {
    public static final String DIR = Environment.getExternalStorageDirectory()
            + "/ADraw/";
    public static final String CROP_IMAGES_DIR = DIR + "crop_images/";
    public static final String GALLERY_FILE_EXTENSION = "png";// jpg不透明

    /**
     * 删除目录
     *
     */
    public static void deleteTemp() {
        try {
            deleteFolderFile(CROP_IMAGES_DIR, true);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * 删除指定目录下文件及目录
     *
     * @param deleteThisPath
     * @param filepath
     * @return
     */
    public static void deleteFolderFile(String filePath, boolean deleteThisPath)
            throws IOException {
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);

            if (file.isDirectory()) {// 处理目录
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFolderFile(files[i].getAbsolutePath(), true);
                }
            }
            if (deleteThisPath) {
                if (!file.isDirectory()) {// 如果是文件，删除
                    file.delete();
                } else {// 目录
                    if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                        file.delete();
                    }
                }
            }
        }
    }

    public static String getRealFilePath(Context context, String path) {
        String filePath = null;
        try {
            if (path != null && path.contains("content://")) {
                Cursor cursor = context.getContentResolver().query(
                        Uri.parse(path), null, null, null, null);
                if (cursor.getCount() == 0) {
                    cursor.close();
                    return null;
                }
                cursor.moveToFirst();
                filePath = cursor.getString(cursor.getColumnIndex("_data"));
                cursor.close();
            } else {
                filePath = path;
            }
            if (filePath != null) {
                filePath = filePath.replace("file://", "");
            }
        } catch (Exception e) {
        }
        return filePath;
    }

    /*
     * 将bitmap保存成一个文件
     *
     * @author noah
     *
     * @return 返回复制成功的文件地址
     */
    public static String copyFromBitmap(Bitmap bitmap) {
        String path = createFileName(CROP_IMAGES_DIR);
        FileOutputStream fo = null;
        try {
            createDir(CROP_IMAGES_DIR);
            File file = new File(path);
            fo = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fo);
            fo.flush();
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return path;
    }

    private static void createDir(String path) throws IOException {
        File temp = new File(path);
        if (!temp.exists()) {
            temp.mkdirs();
        }
        File noMediaFile = new File(CROP_IMAGES_DIR, ".nomedia");
        noMediaFile.createNewFile();
    }

    /**
     * 创建一个文件名称
     *
     * @return
     */
    private static String createFileName(String dir) {
        String path = dir + System.currentTimeMillis() + "."
                + GALLERY_FILE_EXTENSION;
        return path;
    }
}
