package com.jacky.commondraw.wigets.drawpickers;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class Listener<T> {
    private List<T> outerListeners=new LinkedList<T>();
    /**
     *Description : Add the Type t that you want to listen
     * @param t
     */
    public void add(T t){
        outerListeners.add(t);
    }
    protected List<T> getAllListeners(){
        return outerListeners;
    }
    /**
     * Description : Remove the Type t listener
     * @param t
     */
    public void removeListener(T t){
        if (t!=null) {
            outerListeners.remove(t);
        }
    }
}
