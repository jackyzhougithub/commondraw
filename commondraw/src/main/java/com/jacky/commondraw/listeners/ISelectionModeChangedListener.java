package com.jacky.commondraw.listeners;

import com.jacky.commondraw.views.doodleview.DoodleEnum;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 */
public interface ISelectionModeChangedListener {
    void onSelectionModeChanged(DoodleEnum.SelectionMode oldValue,
                                       DoodleEnum.SelectionMode newValue);
}
