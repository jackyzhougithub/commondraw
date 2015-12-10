package com.jacky.commondraw.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 * 对应一个DoodleView中所有的数据。
 */
public class Page {
    protected List<InsertableObjectBase> mInsertableObjectList = null;

    public Page(){
        mInsertableObjectList = new ArrayList<InsertableObjectBase>();
    }

    public void addInsertableObject(InsertableObjectBase object){
        mInsertableObjectList.add(object);
    }

    public void deleteInsertableObject(InsertableObjectBase object){
        mInsertableObjectList.remove(object);
    }
}
