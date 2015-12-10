package com.jacky.commondraw.listeners;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 * 定义图形识别可用性改变时候的回调。当满足以下条件的时候，图形识别可用:
 * 1.系统支持图形识别；
 * 2.DoodleView的InputMode为Draw;
 * 3.DoodleView的SelectionState为None。
 * 这个时候，调用startShapeRecognition，返回值为true
 */
public interface ISRAvailabilityChangedListener {
    void onSRAvailabilityChanged(boolean available);
}
