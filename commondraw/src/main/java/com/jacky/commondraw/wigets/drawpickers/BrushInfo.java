package com.jacky.commondraw.wigets.drawpickers;

import com.jacky.commondraw.model.stroke.InsertableObjectStroke;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class BrushInfo {
    private float mStrokeWidth = MetaData.DOODLE_PAINT_WIDTHS[MetaData.DOODLE_DEFAULT_PAINT_WIDTH];
    private int mDoodleToolCode = InsertableObjectStroke.STROKE_TYPE_NORMAL;
    private int mDoodleToolAlpha = 0x5F;
    private boolean mIsPalette = false;
    private boolean mIsColorMode = false;
    private int mCustomColor = -1;
    private int mSelectedColorIndex = 0;
    private int mCurrentX = 0;
    private int mCurrentY = 0;

    public BrushInfo(float width, int toolCode, int alpha, boolean isPalette, boolean isColorMode,
                     int color, int index, int x, int y)
    {
        mStrokeWidth = width;
        mDoodleToolCode = toolCode;
        mDoodleToolAlpha = alpha;
        mIsPalette = isPalette;
        mIsColorMode = isColorMode;
        mCustomColor = color;
        mSelectedColorIndex = index;
        mCurrentX = x;
        mCurrentY = y;
    }

    public float getStrokeWidth()
    {
        return mStrokeWidth;
    }

    public int getDoodleToolCode()
    {
        return mDoodleToolCode;
    }

    public int getDoodleToolAlpha()
    {
        return mDoodleToolAlpha;
    }

    public boolean getIsPalette()
    {
        return mIsPalette;
    }

    public boolean getIsColorMode()
    {
        return mIsColorMode;
    }

    public int getCustomColor()
    {
        return mCustomColor;
    }

    public int getSelectedColorIndex()
    {
        return mSelectedColorIndex;
    }

    public int getCurrentX()
    {
        return mCurrentX;
    }

    public int getCurrentY()
    {
        return mCurrentY;
    }
}
