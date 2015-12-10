package com.jacky.commondraw.utils;

import com.jacky.commondraw.model.propertyconfig.PropertyConfigStroke;
import com.jacky.commondraw.model.stroke.InsertableObjectStroke;

import java.util.HashMap;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 * 用来存储和查找stroke相关的propety config 的工具类
 *
 */
public class PropertyConfigStrokeUtils {
    private static int STROKE_TYPE_MIN = InsertableObjectStroke.STROKE_TYPE_ERASER;
    private static int STROKE_TYPE_MAX = InsertableObjectStroke.STROKE_TYPE_AIRBRUSH;
    private static HashMap<Integer, PropertyConfigStroke> sStrokeConfigs;
    static {
        sStrokeConfigs = new HashMap<Integer, PropertyConfigStroke>();
        for (int i = STROKE_TYPE_MIN; i <= STROKE_TYPE_MAX; i++) {
            PropertyConfigStroke config = new PropertyConfigStroke();
            sStrokeConfigs.put(i, config);
        }
    }

    public static PropertyConfigStroke getPropertyConfigStroke(int strokeType) {
        return sStrokeConfigs.get(strokeType);
    }

    /**
     * 设置某支画笔的相关属性
     *
     * @param type
     *            要设置的画笔类型
     * @param color
     *            画笔颜色
     * @param strokeWidth
     *            画笔宽度
     * @param alpha
     *            画笔透明度。0-255
     */
    public static void setStrokeAttrs(int type, int color, float strokeWidth,
                                      int alpha) {
        if (!InsertableObjectStroke.isSupported(type)) {
            throw ErrorUtil.getStrokeTypeNoteSupportedError(type);
        }
        PropertyConfigStroke configStroke = PropertyConfigStrokeUtils
                .getPropertyConfigStroke(type);
        configStroke.setAlpha(alpha);
        configStroke.setColor(color);
        configStroke.setStrokeWidth(strokeWidth);
    }
}
