package com.jacky.commondraw.wigets;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 * 画笔属性
 */
public class DrawToolAttribute {
    /**
     * 画笔类型。数值为@DrawTool中画笔的类型
     */
    public int type;
    /**
     * 画笔宽度。
     */
    public float width;
    /**
     * 画笔颜色
     */
    public int color;
    /**
     * 画笔透明度。其值为0-255
     */
    public int alpha;

    public float minWidth;

    public float maxWidth;
}
