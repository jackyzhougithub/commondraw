package com.jacky.commondraw.views.doodleview;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 */
public class DoodleEnum {
    /**
     * 定义doodleview的输入模式 DoodleView总是处于InputMode之一
     *
     */
    public enum InputMode {
        DRAW, ERASE
    }
    /**
     * 定义一个枚举。来记录DoodleView现在所处的选中状态 。
     * <p>
     * 在NONE状态下，用户可以使用绘制，橡皮擦功能； 在SELECTION状态下，用户可以单机选中一个
     * {@link InserableObjectBase} object, 并且对该对象进行旋转，平移，缩放等操作(如果该对象支持)，如果是
     * {@link InsertableBitmap} object, 还可以进行裁剪等操作。
     * 两种状态的切换:NONE->SELECTION，User通过长按一个 {@link InserableObjectBase}
     * object，进入SELECTION； 或者developer调用 {@link DoodelView#enterSelectionMode}
     * ,进入SELECTION状态 SELECTION->NONE，developer调用
     * {@link DoodelView#exitSelectionMode}进入NONE状态; 两种状态切换成功之后，会发送通知
     * {@link SelectionModeChangedListener}。 在 {@link SelectionMode#SELECTION}
     * 状态下，用户单击即可选中一个 {@link InserableObjectBase} object
     */
    public enum SelectionMode {
        NONE, SELECTION
    }
}
