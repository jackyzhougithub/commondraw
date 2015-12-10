package com.jacky.commondraw.views.selectview;

import android.graphics.Point;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public abstract class AnchorLayer implements ILayer {
    private IAnchorable mAnchorable = null;
    private Point mSrcAchorPoint = null;
    private Point mAchorPoint = null;
    public AnchorLayer(IAnchorable anchorable,Point anchor){
        mSrcAchorPoint = anchor;
        mAchorPoint = new Point(anchor);
        mAnchorable = anchorable;
    }
    public Point getAnchorPoint(){
        if (mAnchorable!=null) {
            mAchorPoint = mAnchorable.anchor(mSrcAchorPoint);
            return mAchorPoint;
        }else {
            return mSrcAchorPoint;
        }
    }
}