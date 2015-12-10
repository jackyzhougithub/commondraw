package com.jacky.commondraw.wigets.drawpickers;

import android.view.View;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public interface IPickerControl {
    /**
     * Description : If the Picker is showing status.
     * @return boolean
     */
    boolean isPickerShowing();
    /**
     *  Show begin the top left window
     * @param parent get the parent window token
     * @param screenXoff
     * @param screenYoff
     * @throws IllegalAccessException
     */
    void showPickerAtLocation(View parent,int screenXoff,int screenYoff) throws IllegalAccessException;
    /**
     * show at the left bottom of parent
     * @param parent
     * @param xoff
     * @param yoff
     * @throws IllegalAccessException
     */
    void showPickerUnderView(View parent,int xoff,int yoff) throws IllegalAccessException;
    /**
     * Description :Show  Picker DropDown the view
     * @param v
     * @throws IllegalAccessException
     */
    void showPicker(View v) throws IllegalAccessException;
    /**
     * Display the content view in a popup window anchored to the bottom-left corner of the anchor view offset by the specified x and y coordinates
     * @param v
     * @param xoff
     * @param yoff
     * @throws IllegalAccessException
     */
    void showPicker(View v,int xoff,int yoff) throws IllegalAccessException;
    /**
     * Description :Dismiss the Picker
     */
    void dismissPicker();
}
