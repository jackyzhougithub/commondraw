package com.jacky.commondraw.shaperecognize;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class CFG {
    /**
     * Path to the MyScript Builder directory.
     */
    public static final String MSB_DIR = "/sdcard";

    /**
     * MyScript Engine version.
     */
    // public static final Version ENGINE_VERSION = new Version(5, 0, 0);
    // //Carol-'Version' class is removed from VO_lib

    /**
     * Path to the MyScript Builder resource directory.
     */
    public static final String RESOURCE_DIR = "/system/usr/xt9/VO/resources";// "file:///android_asset/";//MSB_DIR
    // +
    // "/resources";//darwin

    /**
     * Set this, e.g.: "en_GB" for English (United Kingdom).
     */
    public static final String LANG = "zh_CN";

    /**
     * Set this, e.g.: . "_jisx0208" for Japanese JIS X 0208 . _gb18030" for
     * Chinese GB 18030 . etc.
     */
    public static final String CODE_SET_SUFFIX = "_gb2312";// null;

    /**
     * Set this to "iso", "hpr" or "cur".
     */
    public static final String HWSTYLE = "cur.lite";

    public static final String SHAPE_KNOWLEDGE_RES = "/shape/shk-standard.res";// darwin

    public static final String EQUATION_AK_RES = RESOURCE_DIR
            + "/equation/equation-ak.res";

    public static final String EQUATION_GRM_RES = RESOURCE_DIR
            + "/equation/equation-grm-standard.res";

    public static final String DATA_DIR = MSB_DIR + "/edk/examples/data";

    public static final String OUT_DIR = "/sdcard/";

    public static String PATH_TO_ASSETS = null;
    public static Context mContext;

    public static String PATH_SYSTEM_LIB = "/system/lib/";
    public static String PATH_AP_LIB = "/data/data/com.example.asusdrawdemo/lib/";
    private static boolean bCanDoVO = false;
    private static boolean bCheckedRun = false;
    static final String AsusDrawRes_PackageName = "com.asus.drawres";

    public static void setCanDoVO(boolean bol) {
        bCanDoVO = bol;
    }

    public static void setContext(Context context) {
        mContext = context;
        PATH_AP_LIB = "/data/data/" + mContext.getPackageName() + "/lib";
    }

    public static boolean getCanDoVO() {
        if (bCheckedRun) {
            return bCanDoVO;
        } else {
            return checkVOResourcesExist();
        }
    }

    public static boolean checkFileExist(String path) {
        File file = new File(path);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    public static boolean checkSoExist() {
        boolean returnValue = false;
        returnValue = checkFileExist(PATH_SYSTEM_LIB + "libMyScriptEngine.so");
        if (returnValue == false) {
            return false;
        }
        returnValue = checkFileExist(PATH_AP_LIB + "libMyScriptShape.so")
                || checkFileExist(PATH_SYSTEM_LIB + "libMyScriptShape.so");
        if (returnValue == false) {
            return false;
        }
        return true;
    }

    public static boolean checkResourcesExist() {
        boolean returnValue = false;
        returnValue = checkFileExist(PATH_TO_ASSETS + SHAPE_KNOWLEDGE_RES);
        if (returnValue == false) {
            setPath(mContext.getDir("Data", 0).getAbsolutePath());
            copyResources(mContext);
            if (!checkFileExist(PATH_TO_ASSETS + SHAPE_KNOWLEDGE_RES)) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkVOResourcesExist() {
        setCanDoVO(checkResourcesExist() && checkSoExist());
        bCheckedRun = true;
        return bCanDoVO;
    }

    public static void setPath(String path) {
        if (PATH_TO_ASSETS == null) {
            PATH_TO_ASSETS = path;
        }
    }

    /**
     * Simulate a merge resource
     *
     * @param context
     *            the application context
     * @param source
     *            the resource file to merge
     * @param destination
     *            the merged file destination
     * @param language
     *            the resource language
     * @param suffix
     *            ak or lk file suffix
     */
    private static void simulateUnsplitResource(AssetManager manager,
                                                File source, File destination) throws IOException {
        OutputStream os = new FileOutputStream(destination);
        InputStream is = manager.open(source.getPath());
        destination.createNewFile();
        int read = 0;
        byte[] bytes = new byte[1024];
        while ((read = is.read(bytes)) != -1)
            os.write(bytes, 0, read);
        is.close();
        os.close();
    }

    // move res to the place
    public static void copyResources(Context context) {
        try {
            Context asusDrawResContext = context.createPackageContext(
                    AsusDrawRes_PackageName, Context.CONTEXT_INCLUDE_CODE
                            | Context.CONTEXT_IGNORE_SECURITY);
            // list asset resources
            AssetManager manager = asusDrawResContext.getAssets();
            String[] reses = manager.list("resources");

            // data folder
            File path = context.getDir("Data", 0);
            File partial = new File(path.getAbsolutePath() + "/");

            for (String res : reses) {
                File complete = new File(partial.getAbsolutePath(), res);
                // be sure the destination folder exist
                complete.mkdirs();

                String[] details = manager.list("resources/" + res);
                for (String detail : details) {
                    File srcFile = new File("resources/" + res, detail);
                    File destinationFile = new File(complete, detail);
                    simulateUnsplitResource(manager, srcFile, destinationFile);
                }
            }
        } catch (Exception e) {
            Log.e("copyResources", e.getMessage());
        }
    }

}
