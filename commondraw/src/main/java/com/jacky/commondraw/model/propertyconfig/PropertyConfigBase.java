package com.jacky.commondraw.model.propertyconfig;

import java.util.HashMap;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 * 配置基类
 */
public class PropertyConfigBase {
    protected HashMap<String, Object> mExtraProperties = null;

    public PropertyConfigBase() {
        mExtraProperties = new HashMap<String, Object>();
    }

    public HashMap<String, Object> getExtraProperties() {
        return mExtraProperties;
    }

    /**
     * 添加一个key。如果该key已经存在，则替换原来的值。
     *
     * @param key
     * @param object
     */
    public void addOrModifyExtraProperty(String key, Object object) {
        Object oldValue = mExtraProperties.get(key);
        if (oldValue == null) {
            mExtraProperties.put(key, object);
        } else {
            mExtraProperties.put(key, object);
        }
    }

    public void remoceExtraProperty(String key) {
        Object oldValue = mExtraProperties.get(key);
        if (oldValue != null) {
            mExtraProperties.remove(key);
        }
    }

    public Object getExtraPropertyValue(String key) {
        return mExtraProperties.get(key);
    }
}
