package com.jacky.commondraw.wigets.drawpickers;

import com.jacky.commondraw.R;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class EditorPhoneIdList {
    public  int[] editorDoodleUnityIds = null;
    public int[] editorDoodleUnityColorIds =null;
    public int[] editorDoodleBrushIds=null;
    public EditorPhoneIdList() {
        editorDoodleBrushIds = new int[]{
                R.id.editor_func_d_brush_normal,
                R.id.editor_func_d_brush_scribble,
                R.id.editor_func_d_brush_mark,
                R.id.editor_func_d_brush_sketch,
                // begin wendy
                R.id.editor_func_d_brush_markpen,
                R.id.editor_func_d_brush_writingbrush,
        };
        editorDoodleUnityIds = new int[]{
                R.id.editor_func_d_brush_normal,
                R.id.editor_func_d_brush_scribble,
                R.id.editor_func_d_brush_mark,
                R.id.editor_func_d_brush_sketch,
                R.id.editor_func_d_brush_markpen,
                R.id.editor_func_d_brush_writingbrush,

                R.id.editor_func_color_A,
                R.id.editor_func_color_B,
                R.id.editor_func_color_C,
                R.id.editor_func_color_D,
                R.id.editor_func_color_E,
                R.id.editor_func_color_F,
                R.id.editor_func_color_G,
                R.id.editor_func_color_H,
                R.id.editor_func_color_I,
                R.id.editor_func_color_J,
                R.id.editor_func_color_H,
                R.id.editor_func_color_K,
                R.id.editor_func_color_M,
                R.id.editor_func_color_L
        };

        editorDoodleUnityColorIds  = new int[] {
                R.id.editor_func_color_A,
                R.id.editor_func_color_B,
                R.id.editor_func_color_C,
                R.id.editor_func_color_D,
                R.id.editor_func_color_E,
                R.id.editor_func_color_F,
                R.id.editor_func_color_G,
                R.id.editor_func_color_H,
                R.id.editor_func_color_I,
                R.id.editor_func_color_J,
                R.id.editor_func_color_H,
                R.id.editor_func_color_K,
                R.id.editor_func_color_M,
                R.id.editor_func_color_L
        };
    }
}
