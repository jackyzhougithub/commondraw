package com.jacky.commondraw.listeners;

import com.jacky.commondraw.model.stroke.InsertableObjectStroke;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 * 定义Stroke准备好时候的回调，此时可以调用图形识别引擎进行图形识别了
 */
public interface IStrokeReadyListener {
    void onStrokeReady(InsertableObjectStroke insertableObjectStroke);
}
