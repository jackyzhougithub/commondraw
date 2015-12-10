package com.jacky.commondraw.wigets;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public interface IDrawtoolsChanged {
    public enum AttType{
        NONE,
        TYPE,
        WIDTH,
        COLOR,
        ALPHA
    }
    /**
     * Description :This will call after the initDrawToolPicker
     * @see com.asus.draw.DrawToolsPicker#initDrawToolPicker
     * @param drawToolAtt
     */
    void drawToolInit(DrawToolAttribute drawToolAtt);
    /**
     * Description :This will call when the draw tool' attribute changed
     * @param newToolAtt the new draw tool attribute
     * @param oldToolAtt the old draw tool attribute
     * @param attTypesChanged what was changed between old and new draw tool
     */
    void onDrawToolChanged(DrawToolAttribute newToolAtt,DrawToolAttribute oldToolAtt,AttType[] attTypesChanged);
}
