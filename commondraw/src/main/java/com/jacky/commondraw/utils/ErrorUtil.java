package com.jacky.commondraw.utils;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 */
public class ErrorUtil {
    public static RuntimeException getStrokeTypeNoteSupportedError(int type) {
        return new RuntimeException("stroke type = " + type
                + ", is not supported");
    }
}
