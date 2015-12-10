package com.jacky.commondraw.wigets.drawpickers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jacky.commondraw.R;
import com.jacky.commondraw.model.stroke.InsertableObjectStroke;
import com.jacky.commondraw.visual.brush.VisualStrokeBase;
import com.jacky.commondraw.wigets.DrawToolAttribute;
import com.jacky.commondraw.wigets.IDrawtoolsChanged;
import com.jacky.commondraw.wigets.IDrawtoolsChanged.AttType;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class DrawToolsPicker extends Listener<IDrawtoolsChanged> {
    public static String LOGV = "DrawToolsPicker";
    private static final float BUTTON_ALPHA_DISABLE = 0.15f;
    private static final float BUTTON_ALPHA_ENABLE = 1.0f;
    // private static final int BUTTON_ALPHA_NEW_DISABLE = (int) (255*0.25);
    // private static final int BUTTON_ALPHA_NEW_ENABLE =255;
    private final int POINT_COUNT = 80;
    private float[] mPoints = new float[POINT_COUNT * 2];
    private Context mContext = null;
    private PopupWindow mPenPickerWindow = null;
    private PickerCtlImpl mPickerCtlImpl = null;

    private SeekBar SeekBar_Alpha = null;
    private TextView Alpha_Text = null;

    private float mStrokeWidth = MetaData.DOODLE_PAINT_WIDTHS[MetaData.DOODLE_DEFAULT_PAINT_WIDTH];
    private int mDoodleToolCode = InsertableObjectStroke.STROKE_TYPE_NORMAL;
    private int mSelectedColorIndex = 5;
    private int mCustomColor = -1;
    private int mDoodleToolAlpha = 0x5F;
    private boolean mIsCustomColorSet = false;
    private boolean mIsPalette = false;
    private float mEraserWidth = MetaData.DOODLE_ERASER_WIDTHS[MetaData.DOODLE_DEFAULT_ERASER_WIDTHS];
    private Paint mDoodlePaint = null;
    private ImageView mPenPreview = null;
    private EditorPhoneIdList mEditorPhoneIdList = null;
    private BrushLibraryAdapter mBrushAdapter = null;
    private BrushCollection mBrushCollection = null;
    private SharedPreferences mSharedPreference = null;
    private ToolInfo mCurrentTool = null;
    private ToolInfo mOldTool = null;

    private static int FLAG_INIT = 0x00000001;
    private int STATUS_FLAG = 0x00000000;
    private int mOffset_Y;

    private final static int[] _DefaultColorCodes = {// copy from EditorActivity
            0xffffffff, 0xffb4b4b4, 0xff5a5a5a, 0xff000000, 0xffe70012, 0xffff9900,
            0xfffff100, 0xff8fc31f, 0xff009944, 0xff00a0e9, 0xff1d2088,
            0xffe5007f };

    /**
     * Description : Create the DrawToolsPicker with Context
     *
     * @param context
     */
    public DrawToolsPicker(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null!");
        }
        mContext = context;
        mSharedPreference = mContext.getSharedPreferences(
                MetaData.PREFERENCE_NAME, Context.MODE_PRIVATE);
        mBrushCollection = new BrushCollection(context);
        mOffset_Y = 0;
    }

    /**
     * Description : Please call this meth before use the IPickerControl which
     * get from the getDrawToolPickerControl.
     *
     * @see 
     */
    public void setlayoutOffsetY(int value){
        mOffset_Y = value;
    }
    public void initDrawToolPicker() {
        if ((STATUS_FLAG & FLAG_INIT) != FLAG_INIT) {
            initUiAndEvent();
        }
    }

    /**
     * Description : Please use the return object(IPickerControl) to control the
     * Picker Window,before you use the IPickerControl object,please call the
     * initDrawToolPicker once.
     *
     * @return IPickerControl
     */
    public IPickerControl getDrawToolPickerControl() {
        if (mPickerCtlImpl == null) {
            mPickerCtlImpl = new PickerCtlImpl();
        }
        return mPickerCtlImpl;
    }

    public void dismissPenPickerWindow(){
        if(mPenPickerWindow != null&& mPenPickerWindow.isShowing()){
            mPenPickerWindow.dismiss();
            mPenPickerWindow = null;
        }
    }

    private void initUiAndEvent() {
        STATUS_FLAG |= FLAG_INIT;
        if (mPenPickerWindow != null && mPenPickerWindow.isShowing()) {
            mPenPickerWindow.dismiss();
            mPenPickerWindow = null;
        }

        mEditorPhoneIdList = new EditorPhoneIdList();
        final Resources res = mContext.getResources();
        float pwidth = res.getDimension(R.dimen.preview_content_width);
        float pheight = res.getDimension(R.dimen.preview_content_height);
        float px = res.getDimension(R.dimen.preview_content_x);
        float py = res.getDimension(R.dimen.preview_content_y);
        genPath((int) pwidth, (int) pheight, (int) px, (int) py);
        // init pen code
        // init paint
        getColorsBrushStrokeFromPreference();

        final List<IDrawtoolsChanged> ls = getAllListeners();
        if (ls != null) {
            for (IDrawtoolsChanged iDrawtoolsChanged : ls) {
                iDrawtoolsChanged.drawToolInit(mCurrentTool.mDrawToolAttribute);
            }
        }

        genPopupWindow();

        final View layoutView = mPenPickerWindow.getContentView();

        // ---------close pen picker
        ImageView brushEditDialogClose = (ImageView) layoutView
                .findViewById(R.id.brush_edit_dialog_close);
        if (brushEditDialogClose != null) {
            brushEditDialogClose.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    mPenPickerWindow.dismiss();
                }

            });
        }
        // ------------------------------

        // ------------brush lib ui
        final Button brushLibraryButton = (Button) layoutView
                .findViewById(R.id.brush_library);
        brushLibraryButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                View brushColorThicknessLayout = layoutView
                        .findViewById(R.id.brush_color_thickness_layout);
                ListView brushLibraryView = (ListView) layoutView
                        .findViewById(R.id.brush_library_view);
                if (brushColorThicknessLayout.getVisibility() == View.VISIBLE) {
                    int height = brushColorThicknessLayout.getHeight();
                    int height1 = height+mOffset_Y;
                    height = height1 >300 ? height1: 400;
                    brushLibraryView.getLayoutParams().height = height ;
                    brushColorThicknessLayout.setVisibility(View.GONE);
                    brushLibraryView.setVisibility(View.VISIBLE);
                    brushLibraryButton.setText(R.string.brush_edit_dialog_back);
                    brushLibraryButton.setSelected(true);

                    mBrushAdapter = new BrushLibraryAdapter(mContext,
                            mBrushCollection);
                    mBrushAdapter.addOuterListener(notifyOuter);
                    brushLibraryView.setAdapter(mBrushAdapter);
                } else {
                    brushColorThicknessLayout.setVisibility(View.VISIBLE);
                    brushLibraryView.setVisibility(View.GONE);
                    brushLibraryButton
                            .setText(R.string.brush_edit_dialog_library);
                    brushLibraryButton.setSelected(false);
                }
            }

        });
        // --------------------------------
        // -----------rainbow color button
        View vv = layoutView.findViewById(R.id.editor_func_color_L);
        if (mIsCustomColorSet) { // smilefish
            ((ImageView) vv).setBackgroundColor(mCustomColor);
        } else {
            // ((ImageView)vv).setBackgroundResource(R.drawable.color_rainbow);
            ((ImageView) vv).setBackgroundDrawable(mContext.getResources(
                    ).getDrawable(R.drawable.color_rainbow));
        }
        int cid = getColorId();
        vv = layoutView.findViewById(cid);
        ((ImageView)vv).setImageDrawable(mContext.getResources().getDrawable(R.drawable.color_frame_p));
        // -------------------------------
        // --------preview image
        mPenPreview = (ImageView) layoutView.findViewById(R.id.penpreview);
        final ImageButton addBrushBtn = (ImageButton) layoutView
                .findViewById(R.id.add_brush);
        addBrushBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Boolean isSuccess = mBrushCollection.addBrush(mStrokeWidth,
                        mDoodleToolCode, mDoodleToolAlpha, mIsPalette,
                        !mIsPalette, mCustomColor, mSelectedColorIndex,
                        mColorPalette_X, mColorPalette_Y);
                if (isSuccess) {
                    ListView brushLibraryView = (ListView) layoutView
                            .findViewById(R.id.brush_library_view);
                    if (brushLibraryView.getVisibility() == View.VISIBLE)
                        mBrushAdapter.notifyDataSetChanged();

                    if (mBrushCollection.isBrushFull()) {
                        // showToast(EditorActivity.this,
                        // R.string.brush_edit_dialog_full);
                        // addMyToast(instantpageService.this.getResources().getString(R.string.brush_edit_dialog_full));
                        addBrushBtn.setVisibility(View.INVISIBLE);
                    } else
                        addMyToast(mContext
                                .getResources()
                                .getString(
                                        R.string.brush_edit_dialog_save_successful)); // smilefish
                } else {
                    // Toast toast = Toast.makeText(EditorActivity.this,
                    // R.string.brush_edit_dialog_save, Toast.LENGTH_SHORT);
                    // toast.setGravity(Gravity.CENTER, 0, 0);
                    // toast.show();
                    addMyToast(mContext.getResources().getString(
                            R.string.brush_edit_dialog_save));
                }
            }

        });

        ImageView colorStrawBtn = (ImageView) layoutView
                .findViewById(R.id.editor_func_color_straw);
        colorStrawBtn.setVisibility(View.GONE);
        colorStrawBtn.setEnabled(true);
        // ------------------------------
        // ---------------color picker
        final ColorPickerViewCustom ColorPicker = (ColorPickerViewCustom) layoutView
                .findViewById(R.id.color_picker_view);
        ColorPicker.SetListener(new ColorPickerViewCustom.ColorChange() {

            @Override
            public void OnColorChange(int Color) {
                PaintSelector.setColor(mDoodlePaint, Color);
                mCustomColor = Color;
                mIsPalette = true;
                mIsCustomColorSet = true; // smilefish

                if (mPenPreview != null)
                    DrawPreview(mPenPreview);

                for (int id : mEditorPhoneIdList.editorDoodleUnityColorIds) {
                    View vv = layoutView.findViewById(id);
                    if (vv != null) {
                        vv.setSelected(false);
                        // ((ImageView)vv).setImageResource(R.drawable.color_frame);
                        ((ImageView)vv).setImageDrawable(mContext.getResources().getDrawable(R.drawable.color_frame_n));
                        if (id == R.id.editor_func_color_L) {
                            // ((ImageView)vv).setImageResource(R.drawable.color_frame_color_focus);
                            ((ImageView) vv)
                                    .setImageDrawable(mContext
                                            .getResources()
                                            .getDrawable(
                                                    R.drawable.color_frame_p));
                            ((ImageView) vv).setBackgroundColor(Color);
                            vv.setSelected(true);
                        }
                    }
                }
                // Carrot: individually set color of every brush
                mOldTool = mCurrentTool;
                mSelectedColorIndex = COLOR_PALETTE_INDEX;
                mColorPalette_X = ColorPicker.getCurX();
                mColorPalette_Y = ColorPicker.getCurY();
                attrs.get(mCurAttrIndex).ColorInfo.Color = Color;
                attrs.get(mCurAttrIndex).ColorInfo.Index = mSelectedColorIndex;
                attrs.get(mCurAttrIndex).ColorInfo.ColorPalette_X = mColorPalette_X;
                attrs.get(mCurAttrIndex).ColorInfo.ColorPalette_Y = mColorPalette_Y;
                mCurrentTool = genToolInfo(attrs.get(mCurAttrIndex));
                onPaintToolChanged(mCurrentTool.mDrawToolAttribute,
                        mOldTool.mDrawToolAttribute,
                        new AttType[] { AttType.COLOR });
                // Carrot: individually set color of every brush
                ImageButton selectColor = (ImageButton) layoutView
                        .findViewById(R.id.select_color);
                selectColor.setBackgroundColor(Color);
                TextView colorName = (TextView) mPenPickerWindow
                        .getContentView().findViewById(R.id.select_color_name);
                colorName.setText(ColorHelper.displayColorName(Color)); // Carol-choose
                // color
                // from
                // panel
                Drawable background = CoverHelper.createGradientColorAndCover(
                        mContext, R.drawable.pen_scrollbar_bg, Color);
                SeekBar_Alpha.setBackgroundDrawable(background);
            }
        });
        // ---------------------------
        // -----------color button-----------
        TextView colorName = (TextView) layoutView
                .findViewById(R.id.select_color_name);
        colorName
                .setText(ColorHelper.displayColorName(attrs.get(mCurAttrIndex).ColorInfo.Color)); // Carol-choose
        // color
        // from
        // grid

        // --------------------------
        // -----------stroke width-----
        SeekBar SeekBar_width = (SeekBar) layoutView
                .findViewById(R.id.seekbar_width);
        float WidthPrecent;
        int WidthProgress;
        WidthPrecent = (attrs.get(mCurAttrIndex).Width - attrs
                .get(mCurAttrIndex).MinWidth)
                / (attrs.get(mCurAttrIndex).MaxWidth - attrs.get(mCurAttrIndex).MinWidth);
        WidthProgress = (int) (WidthPrecent * 100);
        SeekBar_width.setProgress(WidthProgress);

        TextView strokeWidth = (TextView) layoutView
                .findViewById(R.id.stroke_width);
        // strokeWidth.setText(String.format(getResources().getString(R.string.brush_edit_dialog_stroke),
        // (int)mEditorUiUtility.getDoodlePaint().getStrokeWidth()));
        // --- Carrot: temporary add for limitation of brush width ---
        strokeWidth.setText(String.format(mContext.getResources()
                .getString(R.string.brush_edit_dialog_stroke), (int) attrs
                .get(mCurAttrIndex).Width));
        // --- Carrot: temporary add for limitation of brush width ---

        ImageButton selectBrush = (ImageButton) layoutView
                .findViewById(R.id.select_brush);
        TextView selectBrushName = (TextView) layoutView
                .findViewById(R.id.select_brush_name);
        setSelectBrushButtonInfo(selectBrush, selectBrushName);
        selectBrush.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                View brushLayout = (View) layoutView
                        .findViewById(R.id.brush_select_layout);
                if (brushLayout.getVisibility() == View.VISIBLE)
                    brushLayout.setVisibility(View.GONE);
                else
                    brushLayout.setVisibility(View.VISIBLE);
            }

        });

        ImageButton selectColor = (ImageButton) layoutView
                .findViewById(R.id.select_color);
        selectColor
                .setBackgroundColor(attrs.get(mCurAttrIndex).ColorInfo.Color);
        selectColor.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                View colorLayout = (View) layoutView
                        .findViewById(R.id.color_select_layout);
                if (colorLayout.getVisibility() == View.VISIBLE)
                    colorLayout.setVisibility(View.GONE);
                else
                    colorLayout.setVisibility(View.VISIBLE);
            }

        });
        SeekBar_width.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromTouch) {
                mOldTool = mCurrentTool;

                mStrokeWidth = attrs.get(mCurAttrIndex).MinWidth
                        + (float) (progress / 100.0)
                        * (attrs.get(mCurAttrIndex).MaxWidth - attrs
                        .get(mCurAttrIndex).MinWidth);
                attrs.get(mCurAttrIndex).Width = mStrokeWidth;
                mCurrentTool = genToolInfo(attrs.get(mCurAttrIndex));
                // --- Carrot: temporary add for limitation of brush width ---
                PaintSelector.setPaintWidth(mDoodlePaint, mStrokeWidth);
                if (mPenPreview != null) {
                    DrawPreview(mPenPreview);
                }
                TextView strokeWidth = (TextView) layoutView
                        .findViewById(R.id.stroke_width);
                strokeWidth.setText(String.format(
                        mContext.getResources().getString(
                                R.string.brush_edit_dialog_stroke),
                        (int) mStrokeWidth));
                onPaintToolChanged(mCurrentTool.mDrawToolAttribute,
                        mOldTool.mDrawToolAttribute,
                        new AttType[] { AttType.WIDTH });
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // setAutoRecognizerShapeState(true);
            }
        });
        // ---------------------------
        // --------------pen alpha------
        SeekBar_Alpha = (SeekBar) layoutView.findViewById(R.id.seekbar_Alpha);

        Alpha_Text = (TextView) layoutView.findViewById(R.id.alpha_text);

        float AlphaPercent = mDoodleToolAlpha / 255.0f;
        int AlphaValue = (int) (AlphaPercent * 100);
        SeekBar_Alpha.setProgress(AlphaValue);
        TextView alphaPercentage = (TextView) layoutView
                .findViewById(R.id.alpha_percentage);
        alphaPercentage.setText(AlphaValue + "%");

        Drawable background = CoverHelper.createGradientColorAndCover(mContext,
                R.drawable.pen_scrollbar_bg,
                attrs.get(mCurAttrIndex).ColorInfo.Color);
        SeekBar_Alpha.setBackgroundDrawable(background);
        SeekBar_Alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromTouch) {
                if (mDoodleToolCode != InsertableObjectStroke.STROKE_TYPE_MARKER) {
                    return;
                }
                mOldTool = mCurrentTool;
                int Alpha = 255 * progress / 100;
                mDoodleToolAlpha = Alpha;
                PaintSelector.setAlpha(mDoodlePaint, mDoodleToolAlpha);

                TextView alphaPercentage = (TextView) layoutView
                        .findViewById(R.id.alpha_percentage);
                alphaPercentage.setText(progress + "%");
                mCurrentTool = genToolInfo(attrs.get(mCurAttrIndex));
                if (mPenPreview != null)
                    DrawPreview(mPenPreview);
                onPaintToolChanged(mCurrentTool.mDrawToolAttribute,
                        mOldTool.mDrawToolAttribute,
                        new AttType[] { AttType.ALPHA });
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                System.out.println(seekBar.getProgress());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        if (mDoodleToolCode == InsertableObjectStroke.STROKE_TYPE_MARKER) {
            float AlphaPrecent = mDoodleToolAlpha / 255.0f;
            int AlphaProgress = (int) (AlphaPrecent * 100);
            SeekBar_Alpha.setProgress(AlphaProgress);
        } else {
            setSeekBarAlphaEnable(false);
            setAlphaTextEnable(false);
        }

        if (mPenPreview != null) {
            DrawPreview(mPenPreview);
        }
        View view = layoutView.findViewById(getIdFrom(mDoodleToolCode));
        view.setSelected(true);
        // ----------------------------
    }

    private void setSelectBrushButtonInfo(ImageButton selectBrush,
                                          TextView selectBrushName) {
        switch (mDoodleToolCode) {
            case InsertableObjectStroke.STROKE_TYPE_NORMAL:
                // selectBrush.setImageResource(R.drawable.pen_style1);
                // selectBrushName.setText(R.string.brush_edit_dialog_rollerpen);
                selectBrush.setImageDrawable(mContext.getResources()
                        .getDrawable(R.drawable.popup_pen3));
                selectBrushName.setText(mContext.getResources()
                        .getString(R.string.brush_edit_dialog_rollerpen));
                break;
            case InsertableObjectStroke.STROKE_TYPE_PENCIL:
                // selectBrush.setImageResource(R.drawable.pen_style2);
                // selectBrushName.setText(R.string.brush_edit_dialog_pencil);
                selectBrush.setImageDrawable(mContext.getResources()
                        .getDrawable(R.drawable.popup_pen1));
                selectBrushName.setText(mContext.getResources()
                        .getString(R.string.brush_edit_dialog_pencil));
                break;
            case InsertableObjectStroke.STROKE_TYPE_MARKER:
                // selectBrush.setImageResource(R.drawable.pen_style3);
                // selectBrushName.setText(R.string.brush_edit_dialog_marker);
                selectBrush.setImageDrawable(mContext.getResources()
                        .getDrawable(R.drawable.popup_pen4));
                selectBrushName.setText(mContext.getResources()
                        .getString(R.string.brush_edit_dialog_marker));
                break;
            case InsertableObjectStroke.STROKE_TYPE_PEN:
                // selectBrush.setImageResource(R.drawable.pen_style4);
                // selectBrushName.setText(R.string.brush_edit_dialog_pen);
                selectBrush.setImageDrawable(mContext.getResources()
                        .getDrawable(R.drawable.popup_pen2));
                selectBrushName.setText(mContext.getResources()
                        .getString(R.string.brush_edit_dialog_pen));
                break;
            case InsertableObjectStroke.STROKE_TYPE_BRUSH:
                // selectBrush.setImageResource(R.drawable.pen_style5);
                // selectBrushName.setText(R.string.brush_edit_dialog_brush);
                selectBrush.setImageDrawable(mContext.getResources()
                        .getDrawable(R.drawable.popup_pen5));
                selectBrushName.setText(mContext.getResources()
                        .getString(R.string.brush_edit_dialog_brush));
                break;
            case InsertableObjectStroke.STROKE_TYPE_AIRBRUSH:
                // selectBrush.setImageResource(R.drawable.pen_style6);
                // selectBrushName.setText(R.string.brush_edit_dialog_airbrush);
                selectBrush.setImageDrawable(mContext.getResources()
                        .getDrawable(R.drawable.popup_pen6));
                selectBrushName.setText(mContext.getResources()
                        .getString(R.string.brush_edit_dialog_airbrush));
                break;
        }
    }

    private void selectCurrentBrushOnPopup() {
        if (mPenPickerWindow == null || !mPenPickerWindow.isShowing()) {
            return;
        }
        final View parentView = mPenPickerWindow.getContentView();
        final int toolId = getIdFrom(mDoodleToolCode);
        for (int id : mEditorPhoneIdList.editorDoodleBrushIds) {
            View v = parentView.findViewById(id);
            v.setSelected(false);
            if (v.getId() == toolId) {
                v.setSelected(true);
            }
        }
        int Colorid = getColorId();
        for (int i : mEditorPhoneIdList.editorDoodleUnityColorIds) {
            View vv = parentView.findViewById(i);
            if (vv != null) {
                vv.setSelected(false);
                // ((ImageView) vv).setImageResource(R.drawable.color_frame);
                ((ImageView) vv).setImageDrawable(mContext.getResources(
                        ).getDrawable(R.drawable.color_frame_n));
                if (vv.getId() == Colorid) {
                    vv.setSelected(true);
                    // ((ImageView)
                    // vv).setImageResource(R.drawable.color_frame_color_focus);
                    ((ImageView) vv).setImageDrawable(mContext
                            .getResources().getDrawable(
                                    R.drawable.color_frame_p));
                }
            }
        }
        ImageButton selectColor = (ImageButton) parentView
                .findViewById(R.id.select_color);
        selectColor
                .setBackgroundColor(attrs.get(mCurAttrIndex).ColorInfo.Color);
        TextView colorName = (TextView) parentView
                .findViewById(R.id.select_color_name);
        colorName
                .setText(ColorHelper.displayColorName(attrs.get(mCurAttrIndex).ColorInfo.Color)); // Carol-choose
        // color
        // from
        // grid

        Drawable background = CoverHelper.createGradientColorAndCover(mContext,
                R.drawable.pen_scrollbar_bg,
                attrs.get(mCurAttrIndex).ColorInfo.Color);
        SeekBar_Alpha.setBackgroundDrawable(background);
        ImageButton selectBrush = (ImageButton) parentView
                .findViewById(R.id.select_brush);
        TextView selectBrushName = (TextView) parentView
                .findViewById(R.id.select_brush_name);
        setSelectBrushButtonInfo(selectBrush, selectBrushName);
    }

    private int getIdFrom(int brushValue) {
        switch (brushValue) {
            case InsertableObjectStroke.STROKE_TYPE_NORMAL:
                return R.id.editor_func_d_brush_normal;
            case InsertableObjectStroke.STROKE_TYPE_PEN:
                return R.id.editor_func_d_brush_scribble;
            case InsertableObjectStroke.STROKE_TYPE_AIRBRUSH:
                return R.id.editor_func_d_brush_mark;
            case InsertableObjectStroke.STROKE_TYPE_PENCIL:
                return R.id.editor_func_d_brush_sketch;
            // begin wendy
            case InsertableObjectStroke.STROKE_TYPE_MARKER:
                return R.id.editor_func_d_brush_markpen;
            case InsertableObjectStroke.STROKE_TYPE_BRUSH:
                return R.id.editor_func_d_brush_writingbrush;
            // end wendy
        }
        return InsertableObjectStroke.STROKE_TYPE_NORMAL;
    }

    private void getColorsBrushStrokeFromPreference() {
        if (mSharedPreference == null) {
            return;
        }
        mDoodleToolCode = mSharedPreference.getInt(
                MetaData.PREFERENCE_PICKER_DOODLE_BRUSH,
                InsertableObjectStroke.STROKE_TYPE_MARKER);
        float testStroke = 0;
        testStroke = mSharedPreference
                .getFloat(
                        MetaData.PREFERENCE_PICKER_DOODLE_ERASER_WIDTH,
                        MetaData.DOODLE_ERASER_WIDTHS[MetaData.DOODLE_DEFAULT_ERASER_WIDTHS]);
        for (float stroke : MetaData.DOODLE_ERASER_WIDTHS) {
            if (stroke == testStroke) {
                mEraserWidth = testStroke;
                break;
            }
        }
        mStrokeWidth = mSharedPreference
                .getFloat(
                        MetaData.PREFERENCE_PICKER_STROKE,
                        MetaData.DOODLE_PAINT_WIDTHS[MetaData.DOODLE_DEFAULT_PAINT_WIDTH]);
        mSelectedColorIndex = mSharedPreference.getInt(
                MetaData.PREFERENCE_SEL_COLOR_INDEX, 5);
        mCustomColor = mSharedPreference.getInt(
                MetaData.PREFERENCE_PALETTE_COLOR, -1);
        mDoodleToolAlpha = mSharedPreference.getInt(
                MetaData.PREFERENCE_DOODLE_ALPHA, 0x5f);
        mIsCustomColorSet = mSharedPreference.getBoolean(
                MetaData.PREFERENCE_IS_CUSTOM_COLOR_SET, false);
        mIsPalette = mSharedPreference.getBoolean(
                MetaData.PREFERENCE_IS_PALETTE, false);
        if (attrs.size() < 1) {
            InitBrushAttributs();
        }
        attrs.get(0).Width = mSharedPreference.getFloat(
                MetaData.TEMP_NORMAL_WIDTH, 2.1f);
        attrs.get(1).Width = mSharedPreference.getFloat(
                MetaData.TEMP_PEN_WIDTH, 6);
        attrs.get(2).Width = mSharedPreference.getFloat(
                MetaData.TEMP_BRUSH_WIDTH, 25);
        attrs.get(3).Width = mSharedPreference.getFloat(
                MetaData.TEMP_AIRBRUSH_WIDTH, 30.5f);
        attrs.get(4).Width = mSharedPreference.getFloat(
                MetaData.TEMP_PENCIL_WIDTH, 3.5f);
        attrs.get(5).Width = mSharedPreference.getFloat(
                MetaData.TEMP_MARKER_WIDTH, 25);

        // Carrot: individually set color of every brush
        attrs.get(0).ColorInfo.Color = mSharedPreference.getInt(
                MetaData.TEMP_NORMAL_COLOR, 0xff1d2088);
        attrs.get(1).ColorInfo.Color = mSharedPreference.getInt(
                MetaData.TEMP_PEN_COLOR, 0xff8fc31f);
        attrs.get(2).ColorInfo.Color = mSharedPreference.getInt(
                MetaData.TEMP_BRUSH_COLOR, 0xff000000);
        attrs.get(3).ColorInfo.Color = mSharedPreference.getInt(
                MetaData.TEMP_AIRBRUSH_COLOR, 0xff00a0e9);
        attrs.get(4).ColorInfo.Color = mSharedPreference.getInt(
                MetaData.TEMP_PENCIL_COLOR, 0xff000000);
        attrs.get(5).ColorInfo.Color = mSharedPreference.getInt(
                MetaData.TEMP_MARKER_COLOR, 0xff8fc31f);

        attrs.get(0).ColorInfo.Index = mSharedPreference.getInt(
                MetaData.TEMP_NORMAL_COLOR_INDEX, 10);
        attrs.get(1).ColorInfo.Index = mSharedPreference.getInt(
                MetaData.TEMP_PEN_COLOR_INDEX, 7);
        attrs.get(2).ColorInfo.Index = mSharedPreference.getInt(
                MetaData.TEMP_BRUSH_COLOR_INDEX, 3);
        attrs.get(3).ColorInfo.Index = mSharedPreference.getInt(
                MetaData.TEMP_AIRBRUSH_COLOR_INDEX, 9);
        attrs.get(4).ColorInfo.Index = mSharedPreference.getInt(
                MetaData.TEMP_PENCIL_COLOR_INDEX, 3);
        attrs.get(5).ColorInfo.Index = mSharedPreference.getInt(
                MetaData.TEMP_MARKER_COLOR_INDEX, 7);

        attrs.get(0).ColorInfo.ColorPalette_X = mSharedPreference.getInt(
                MetaData.TEMP_NORMAL_COLOR_X, 0);
        attrs.get(1).ColorInfo.ColorPalette_X = mSharedPreference.getInt(
                MetaData.TEMP_PEN_COLOR_X, 0);
        attrs.get(2).ColorInfo.ColorPalette_X = mSharedPreference.getInt(
                MetaData.TEMP_BRUSH_COLOR_X, 0);
        attrs.get(3).ColorInfo.ColorPalette_X = mSharedPreference.getInt(
                MetaData.TEMP_AIRBRUSH_COLOR_X, 0);
        attrs.get(4).ColorInfo.ColorPalette_X = mSharedPreference.getInt(
                MetaData.TEMP_PENCIL_COLOR_X, 0);
        attrs.get(5).ColorInfo.ColorPalette_X = mSharedPreference.getInt(
                MetaData.TEMP_MARKER_COLOR_X, 0);

        attrs.get(0).ColorInfo.ColorPalette_Y = mSharedPreference.getInt(
                MetaData.TEMP_NORMAL_COLOR_Y, 0);
        attrs.get(1).ColorInfo.ColorPalette_Y = mSharedPreference.getInt(
                MetaData.TEMP_PEN_COLOR_Y, 0);
        attrs.get(2).ColorInfo.ColorPalette_Y = mSharedPreference.getInt(
                MetaData.TEMP_BRUSH_COLOR_Y, 0);
        attrs.get(3).ColorInfo.ColorPalette_Y = mSharedPreference.getInt(
                MetaData.TEMP_AIRBRUSH_COLOR_Y, 0);
        attrs.get(4).ColorInfo.ColorPalette_Y = mSharedPreference.getInt(
                MetaData.TEMP_PENCIL_COLOR_Y, 0);
        attrs.get(5).ColorInfo.ColorPalette_Y = mSharedPreference.getInt(
                MetaData.TEMP_MARKER_COLOR_Y, 0);
        SetCurAttrInit(mDoodleToolCode);
        mCurrentTool = genToolInfo(attrs.get(mCurAttrIndex));
        if (mCurrentTool.mDrawToolAttribute.type == InsertableObjectStroke.STROKE_TYPE_MARKER) {
            mCurrentTool.mDrawToolAttribute.alpha = mDoodleToolAlpha;
        }
        mDoodlePaint = new Paint();
        if (mSelectedColorIndex == COLOR_PALETTE_INDEX) {
            PaintSelector.initPaint(mDoodlePaint, mCustomColor, mStrokeWidth);
        } else {
            PaintSelector.initPaint(mDoodlePaint,
                    _DefaultColorCodes[mSelectedColorIndex], mStrokeWidth);
        }
        if (mIsPalette && mIsCustomColorSet) { // smilefish
            mDoodlePaint.setColor(mCustomColor);
        } else {
            mDoodlePaint.setColor(_DefaultColorCodes[mSelectedColorIndex]);
        }
        if (mDoodleToolCode == InsertableObjectStroke.STROKE_TYPE_MARKER) {
            PaintSelector.setAlpha(mDoodlePaint, mDoodleToolAlpha);
        }
    }

    private ToolInfo genToolInfo(BrushAttributes att) {
        ToolInfo ti = new ToolInfo();
        ti.mDrawToolAttribute.color = att.ColorInfo.Color;
        ti.mDrawToolAttribute.type = att.Type;
        ti.mDrawToolAttribute.width = att.Width;
        ti.mDrawToolAttribute.minWidth = att.MinWidth;
        ti.mDrawToolAttribute.maxWidth = att.MaxWidth;
        if (att.Type == InsertableObjectStroke.STROKE_TYPE_MARKER) {
            ti.mDrawToolAttribute.alpha = mDoodleToolAlpha;
        }
        return ti;
    }

    private void setColorBrushStrokeToPreference() {
        if (mSharedPreference == null) {
            return;
        }
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.putInt(MetaData.PREFERENCE_PICKER_DOODLE_BRUSH, mDoodleToolCode);
        editor.putFloat(MetaData.PREFERENCE_PICKER_DOODLE_ERASER_WIDTH,
                mStrokeWidth);
        editor.putFloat(MetaData.PREFERENCE_PICKER_DOODLE_ERASER_WIDTH,
                mEraserWidth);
        editor.putInt(MetaData.PREFERENCE_SEL_COLOR_INDEX, mSelectedColorIndex);
        editor.putInt(MetaData.PREFERENCE_DOODLE_ALPHA, mDoodleToolAlpha);
        editor.putBoolean(MetaData.PREFERENCE_IS_PALETTE, mIsPalette);
        editor.putInt(MetaData.PREFERENCE_PALETTE_COLOR, mCustomColor);
        editor.putBoolean(MetaData.PREFERENCE_IS_CUSTOM_COLOR_SET,
                mIsCustomColorSet);
        if (attrs.size() > 0) {
            editor.putFloat(MetaData.TEMP_PENCIL_WIDTH, attrs.get(4).Width);
            editor.putFloat(MetaData.TEMP_NORMAL_WIDTH, attrs.get(0).Width);
            editor.putFloat(MetaData.TEMP_PEN_WIDTH, attrs.get(1).Width);
            editor.putFloat(MetaData.TEMP_MARKER_WIDTH, attrs.get(5).Width);
            editor.putFloat(MetaData.TEMP_BRUSH_WIDTH, attrs.get(2).Width);
            editor.putFloat(MetaData.TEMP_AIRBRUSH_WIDTH, attrs.get(3).Width);
            // Carrot: individually set color of every brush
            editor.putInt(MetaData.TEMP_PENCIL_COLOR,
                    attrs.get(4).ColorInfo.Color);
            editor.putInt(MetaData.TEMP_NORMAL_COLOR,
                    attrs.get(0).ColorInfo.Color);
            editor.putInt(MetaData.TEMP_PEN_COLOR, attrs.get(1).ColorInfo.Color);
            editor.putInt(MetaData.TEMP_MARKER_COLOR,
                    attrs.get(5).ColorInfo.Color);
            editor.putInt(MetaData.TEMP_BRUSH_COLOR,
                    attrs.get(2).ColorInfo.Color);
            editor.putInt(MetaData.TEMP_AIRBRUSH_COLOR,
                    attrs.get(3).ColorInfo.Color);

            editor.putInt(MetaData.TEMP_PENCIL_COLOR_INDEX,
                    attrs.get(4).ColorInfo.Index);
            editor.putInt(MetaData.TEMP_NORMAL_COLOR_INDEX,
                    attrs.get(0).ColorInfo.Index);
            editor.putInt(MetaData.TEMP_PEN_COLOR_INDEX,
                    attrs.get(1).ColorInfo.Index);
            editor.putInt(MetaData.TEMP_MARKER_COLOR_INDEX,
                    attrs.get(5).ColorInfo.Index);
            editor.putInt(MetaData.TEMP_BRUSH_COLOR_INDEX,
                    attrs.get(2).ColorInfo.Index);
            editor.putInt(MetaData.TEMP_AIRBRUSH_COLOR_INDEX,
                    attrs.get(3).ColorInfo.Index);

            editor.putInt(MetaData.TEMP_PENCIL_COLOR_X,
                    attrs.get(4).ColorInfo.ColorPalette_X);
            editor.putInt(MetaData.TEMP_NORMAL_COLOR_X,
                    attrs.get(0).ColorInfo.ColorPalette_X);
            editor.putInt(MetaData.TEMP_PEN_COLOR_X,
                    attrs.get(1).ColorInfo.ColorPalette_X);
            editor.putInt(MetaData.TEMP_MARKER_COLOR_X,
                    attrs.get(5).ColorInfo.ColorPalette_X);
            editor.putInt(MetaData.TEMP_BRUSH_COLOR_X,
                    attrs.get(2).ColorInfo.ColorPalette_X);
            editor.putInt(MetaData.TEMP_AIRBRUSH_COLOR_X,
                    attrs.get(3).ColorInfo.ColorPalette_X);

            editor.putInt(MetaData.TEMP_PENCIL_COLOR_Y,
                    attrs.get(4).ColorInfo.ColorPalette_Y);
            editor.putInt(MetaData.TEMP_NORMAL_COLOR_Y,
                    attrs.get(0).ColorInfo.ColorPalette_Y);
            editor.putInt(MetaData.TEMP_PEN_COLOR_Y,
                    attrs.get(1).ColorInfo.ColorPalette_Y);
            editor.putInt(MetaData.TEMP_MARKER_COLOR_Y,
                    attrs.get(5).ColorInfo.ColorPalette_Y);
            editor.putInt(MetaData.TEMP_BRUSH_COLOR_Y,
                    attrs.get(2).ColorInfo.ColorPalette_Y);
            editor.putInt(MetaData.TEMP_AIRBRUSH_COLOR_Y,
                    attrs.get(3).ColorInfo.ColorPalette_Y);
            // Carrot: individually set color of every brush
        }

        editor.commit();
    }

    public void setScale(float scale ){
        if(mPenPickerWindow == null) return;
        View contentView = mPenPickerWindow.getContentView();
        contentView.setScaleX(scale);
        contentView.setScaleY(scale);
        mPenPickerWindow.update();
    }
    private void genPopupWindow() {
        // LayoutInflater inflater =
        // (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // View layoutView=inflater.inflate(R.layout.editor_func_popup_penmenu,
        // null);
        View layoutView = View.inflate(mContext,
                R.layout.editor_func_popup_penmenu, null);
        if (mPenPickerWindow != null && mPenPickerWindow.isShowing()) {
            mPenPickerWindow.dismiss();
        }
//		layoutView.setBackgroundColor(Color.BLACK);
        Drawable drawable = mContext.getResources().getDrawable(R.drawable.dropdown_r);
        layoutView.setBackgroundDrawable(drawable);
        layoutView.setScaleX(getScalXThroughScreenWidth());
        mPenPickerWindow = null;
        mPenPickerWindow = new PopupWindow(layoutView,
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, false);
        View subView = null;
        for (int id : mEditorPhoneIdList.editorDoodleUnityIds) {
            subView = layoutView.findViewById(id);
            if (subView != null) {
                subView.setOnClickListener(mDoodleUnityClickListener);
            }
        }
        mPenPickerWindow.setTouchable(true);
        mPenPickerWindow.setClippingEnabled(false);
        mPenPickerWindow.setFocusable(true);
        mPenPickerWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                // TODO Auto-generated method stub
                onWindowDismissHappend();
            }
        });
        mPenPickerWindow.setOutsideTouchable(true);
        mPenPickerWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    private float getScalXThroughScreenWidth(){
        DisplayMetrics dm=mContext.getResources().getDisplayMetrics();
        int wpx=dm.widthPixels;
        float density=dm.density;
        int dpWidth = (int) (wpx/density+0.5);
        if (dpWidth<360) {
            return 0.95f;
        }
        return 1.0f;
    }
    private void genPath(int width, int height, int marginWidth,
                         int marginHeight) {
        int index = 0;

        double sinmax = Math.PI * 2.0;

        double sinx = 0.0;
        while ((sinx < sinmax) && (index < POINT_COUNT * 2 - 1)) {
            double siny = Math.sin(sinx);

            float x = (float) (width / sinmax * sinx);
            float y = (float) (height * (siny + 1) / 2);
            mPoints[index++] = x + marginWidth;
            mPoints[index++] = y + marginHeight;

            sinx += sinmax / POINT_COUNT;
        }
    }

    private View.OnClickListener mDoodleUnityClickListener = new View.OnClickListener() {// jmove

        @Override
        public void onClick(View v) {
            ImageButton selectBrush = null;
            TextView selectBrushName = null;
            selectBrush = (ImageButton) mPenPickerWindow.getContentView()
                    .findViewById(R.id.select_brush);
            selectBrushName = (TextView) mPenPickerWindow.getContentView()
                    .findViewById(R.id.select_brush_name);
            int color = -1, Colorid = -1;
            boolean isColorChanged = false; // smilefish
            if (mIsPalette) {
                if (!mIsCustomColorSet) { // smilefish
                    color = mDoodlePaint.getColor();
                } else
                    color = mCustomColor;
            }
            final int[] defaultColorCodes = BrushLibraryAdapter.mDefaultColorCodes;
            int id = v.getId();
            if (id == R.id.editor_func_d_brush_normal) {
                mDoodleToolCode = InsertableObjectStroke.STROKE_TYPE_NORMAL;
                // selectBrush.setImageResource(R.drawable.pen_style1);
                // selectBrushName.setText(R.string.brush_edit_dialog_rollerpen);
                selectBrush.setImageDrawable(mContext.getResources(
                        ).getDrawable(R.drawable.popup_pen3));
                selectBrushName.setText(mContext.getResources()
                        .getText(R.string.brush_edit_dialog_rollerpen));
                // --- Carrot: temporary add for limitation of brush width ---
                SetCurAttr(mDoodleToolCode);
                UpdatePopupByCurBrush();
                mOldTool = mCurrentTool;
                mCurrentTool = genToolInfo(attrs.get(mCurAttrIndex));
                if (mCurrentTool.mDrawToolAttribute.type != mOldTool.mDrawToolAttribute.type) {
                    onPaintToolChanged(mCurrentTool.mDrawToolAttribute,
                            mOldTool.mDrawToolAttribute,
                            new AttType[] { AttType.TYPE });
                }
                selectCurrentBrushOnPopup();
            } else if (id == R.id.editor_func_d_brush_scribble) {
                mDoodleToolCode = InsertableObjectStroke.STROKE_TYPE_PEN;
                // selectBrush.setImageResource(R.drawable.pen_style4);
                // selectBrushName.setText(R.string.brush_edit_dialog_pen);
                selectBrush.setImageDrawable(mContext.getResources(
                        ).getDrawable(R.drawable.popup_pen2));
                selectBrushName.setText(mContext.getResources()
                        .getText(R.string.brush_edit_dialog_pen));
                // --- Carrot: temporary add for limitation of brush width ---
                SetCurAttr(mDoodleToolCode);
                UpdatePopupByCurBrush();
                mOldTool = mCurrentTool;
                mCurrentTool = genToolInfo(attrs.get(mCurAttrIndex));
                if (mCurrentTool.mDrawToolAttribute.type != mOldTool.mDrawToolAttribute.type) {
                    onPaintToolChanged(mCurrentTool.mDrawToolAttribute,
                            mOldTool.mDrawToolAttribute,
                            new AttType[] { AttType.TYPE });
                }
                selectCurrentBrushOnPopup();
            } else if (id == R.id.editor_func_d_brush_mark) {
                mDoodleToolCode = InsertableObjectStroke.STROKE_TYPE_AIRBRUSH;
                // selectBrush.setImageResource(R.drawable.pen_style6);
                // selectBrushName.setText(R.string.brush_edit_dialog_airbrush);
                selectBrush.setImageDrawable(mContext.getResources(
                        ).getDrawable(R.drawable.popup_pen6));
                selectBrushName.setText(mContext.getResources()
                        .getText(R.string.brush_edit_dialog_airbrush));
                // --- Carrot: temporary add for limitation of brush width ---
                SetCurAttr(mDoodleToolCode);
                UpdatePopupByCurBrush();
                mOldTool = mCurrentTool;
                mCurrentTool = genToolInfo(attrs.get(mCurAttrIndex));
                if (mCurrentTool.mDrawToolAttribute.type != mOldTool.mDrawToolAttribute.type) {
                    onPaintToolChanged(mCurrentTool.mDrawToolAttribute,
                            mOldTool.mDrawToolAttribute,
                            new AttType[] { AttType.TYPE });
                }
                selectCurrentBrushOnPopup();
            } else if (id == R.id.editor_func_d_brush_sketch) {
                mDoodleToolCode = InsertableObjectStroke.STROKE_TYPE_PENCIL;
                // selectBrush.setImageResource(R.drawable.pen_style2);
                // selectBrushName.setText(R.string.brush_edit_dialog_pencil);
                selectBrush.setImageDrawable(mContext.getResources(
                        ).getDrawable(R.drawable.popup_pen1));
                selectBrushName.setText(mContext.getResources()
                        .getText(R.string.brush_edit_dialog_pencil));
                // --- Carrot: temporary add for limitation of brush width ---
                SetCurAttr(mDoodleToolCode);
                UpdatePopupByCurBrush();
                mOldTool = mCurrentTool;
                mCurrentTool = genToolInfo(attrs.get(mCurAttrIndex));
                if (mCurrentTool.mDrawToolAttribute.type != mOldTool.mDrawToolAttribute.type) {
                    onPaintToolChanged(mCurrentTool.mDrawToolAttribute,
                            mOldTool.mDrawToolAttribute,
                            new AttType[] { AttType.TYPE });
                }
                selectCurrentBrushOnPopup();
            } else if (id == R.id.editor_func_d_brush_markpen) {
                mDoodleToolCode = InsertableObjectStroke.STROKE_TYPE_MARKER;
                // selectBrush.setImageResource(R.drawable.pen_style3);
                // selectBrushName.setText(R.string.brush_edit_dialog_marker);
                selectBrush.setImageDrawable(mContext.getResources(
                        ).getDrawable(R.drawable.popup_pen4));
                selectBrushName.setText(mContext.getResources()
                        .getText(R.string.brush_edit_dialog_marker));
                // --- Carrot: temporary add for limitation of brush width ---
                SetCurAttr(mDoodleToolCode);
                UpdatePopupByCurBrush();
                mOldTool = mCurrentTool;
                mCurrentTool = genToolInfo(attrs.get(mCurAttrIndex));
                if (mCurrentTool.mDrawToolAttribute.type != mOldTool.mDrawToolAttribute.type) {
                    onPaintToolChanged(mCurrentTool.mDrawToolAttribute,
                            mOldTool.mDrawToolAttribute,
                            new AttType[] { AttType.TYPE });
                }
                selectCurrentBrushOnPopup();
            } else if (id == R.id.editor_func_d_brush_writingbrush) {
                mDoodleToolCode = InsertableObjectStroke.STROKE_TYPE_BRUSH;
                // selectBrush.setImageResource(R.drawable.pen_style5);
                // selectBrushName.setText(R.string.brush_edit_dialog_brush);
                selectBrush.setImageDrawable(mContext.getResources(
                        ).getDrawable(R.drawable.popup_pen5));
                selectBrushName.setText(mContext.getResources()
                        .getText(R.string.brush_edit_dialog_brush));
                // --- Carrot: temporary add for limitation of brush width ---
                SetCurAttr(mDoodleToolCode);
                UpdatePopupByCurBrush();
                mOldTool = mCurrentTool;
                mCurrentTool = genToolInfo(attrs.get(mCurAttrIndex));
                if (mCurrentTool.mDrawToolAttribute.type != mOldTool.mDrawToolAttribute.type) {
                    onPaintToolChanged(mCurrentTool.mDrawToolAttribute,
                            mOldTool.mDrawToolAttribute,
                            new AttType[] { AttType.TYPE });
                }
                selectCurrentBrushOnPopup();
            } else if (id == R.id.editor_func_color_A) {
                color = defaultColorCodes[0];
                mSelectedColorIndex = 0;
                mIsPalette = false;
                isColorChanged = true; // smilefish
            } else if (id == R.id.editor_func_color_B) {
                color = defaultColorCodes[1];
                mSelectedColorIndex = 1;
                mIsPalette = false;
                isColorChanged = true; // smilefish
            } else if (id == R.id.editor_func_color_C) {
                color = defaultColorCodes[2];
                mSelectedColorIndex = 2;
                mIsPalette = false;
                isColorChanged = true; // smilefish
            } else if (id == R.id.editor_func_color_D) {
                color = defaultColorCodes[3];
                mSelectedColorIndex = 3;
                mIsPalette = false;
                isColorChanged = true; // smilefish
            } else if (id == R.id.editor_func_color_E) {
                color = defaultColorCodes[4];
                mSelectedColorIndex = 4;
                mIsPalette = false;
                isColorChanged = true; // smilefish
            } else if (id == R.id.editor_func_color_F) {
                color = defaultColorCodes[5];
                mSelectedColorIndex = 5;
                mIsPalette = false;
                isColorChanged = true; // smilefish
            } else if (id == R.id.editor_func_color_G) {
                color = defaultColorCodes[6];
                mSelectedColorIndex = 6;
                mIsPalette = false;
                isColorChanged = true; // smilefish
            } else if (id == R.id.editor_func_color_H) {
                color = defaultColorCodes[7];
                mSelectedColorIndex = 7;
                mIsPalette = false;
                isColorChanged = true; // smilefish
            } else if (id == R.id.editor_func_color_I) {
                color = defaultColorCodes[8];
                mSelectedColorIndex = 8;
                mIsPalette = false;
                isColorChanged = true; // smilefish
            } else if (id == R.id.editor_func_color_J) {
                color = defaultColorCodes[9];
                mSelectedColorIndex = 9;
                mIsPalette = false;
                isColorChanged = true; // smilefish
            } else if (id == R.id.editor_func_color_K) {
                color = defaultColorCodes[10];
                mSelectedColorIndex = 10;
                mIsPalette = false;
                isColorChanged = true; // smilefish
            } else if (id == R.id.editor_func_color_M) {
                color = defaultColorCodes[11];
                mSelectedColorIndex = 11;
                mIsPalette = false;
                isColorChanged = true; // smilefish
            } else if (id == R.id.editor_func_color_L) {
                if (!mIsCustomColorSet) { // smilefish
                    color = -1;
                    mIsCustomColorSet = true;
                    ((ImageView) v).setBackgroundColor(-1);
                } else {
                    color = mCustomColor;
                }
                // Carrot: individually set color of every brush
                mSelectedColorIndex = COLOR_PALETTE_INDEX;
                // Carrot: individually set color of every brush
                mIsPalette = true;
                isColorChanged = true; // smilefish
            }
            final View parentView = mPenPickerWindow.getContentView();
            if (isColorChanged) { // smilefish
                PaintSelector.setColor(mDoodlePaint, color);
                // Carrot: individually set color of every brush
                attrs.get(mCurAttrIndex).ColorInfo.Index = mSelectedColorIndex;
                attrs.get(mCurAttrIndex).ColorInfo.Color = color;
                mOldTool = mCurrentTool;
                mCurrentTool = genToolInfo(attrs.get(mCurAttrIndex));
                onPaintToolChanged(mCurrentTool.mDrawToolAttribute,
                        mOldTool.mDrawToolAttribute,
                        new AttType[] { AttType.COLOR });
                TextView colorName = (TextView) parentView
                        .findViewById(R.id.select_color_name);
                colorName.setText(ColorHelper.displayColorName(attrs
                        .get(mCurAttrIndex).ColorInfo.Color));
                ImageButton selectColor = (ImageButton) parentView
                        .findViewById(R.id.select_color);
                selectColor
                        .setBackgroundColor(attrs.get(mCurAttrIndex).ColorInfo.Color);
                Drawable background = CoverHelper.createGradientColorAndCover(mContext,
                        R.drawable.pen_scrollbar_bg,
                        attrs.get(mCurAttrIndex).ColorInfo.Color);
                SeekBar_Alpha.setBackgroundDrawable(background);
                // Carrot: individually set color of every brush
            }

            for (int i : mEditorPhoneIdList.editorDoodleUnityColorIds) {
                View vv = parentView.findViewById(i);
                if (vv != null) {
                    vv.setSelected(false);
                    ((ImageView) vv).setImageResource(R.drawable.color_frame_n);

                    Colorid = getColorId();

                    if (vv.getId() == Colorid) {
                        vv.setSelected(true);
                        ((ImageView) vv)
                                .setImageResource(R.drawable.color_frame_p);
                    }
                }

            }

            setPaintTool(mDoodleToolCode);

            if (mDoodleToolCode != InsertableObjectStroke.STROKE_TYPE_MARKER) {
                if ((SeekBar_Alpha != null) && (SeekBar_Alpha.isEnabled())) {
                    setSeekBarAlphaEnable(false);
                }
                if ((Alpha_Text != null) && (Alpha_Text.isEnabled())) {
                    setAlphaTextEnable(false);
                }
            } else {
                if ((SeekBar_Alpha != null) && (!SeekBar_Alpha.isEnabled())) {
                    float AlphaPrecent = mDoodleToolAlpha / 255.0f;
                    int AlphaProgress = (int) (AlphaPrecent * 100);
                    SeekBar_Alpha.setProgress(AlphaProgress);
                    setSeekBarAlphaEnable(true);
                }
                if ((Alpha_Text != null) && (!Alpha_Text.isEnabled())) {
                    setAlphaTextEnable(true);
                }
            }

            if (mPenPreview != null) {
                DrawPreview(mPenPreview);
            }
        }
    };

    private void setPaintTool(int toolCode) {
        Paint paint = mDoodlePaint;
        switch (toolCode) {
            case InsertableObjectStroke.STROKE_TYPE_NORMAL:
                PaintSelector.setNormal(paint);
                break;
            case InsertableObjectStroke.STROKE_TYPE_AIRBRUSH:
                PaintSelector.setAirBrush(paint);
                break;
            case InsertableObjectStroke.STROKE_TYPE_BRUSH:
                PaintSelector.setBrush(paint);
                break;
            case InsertableObjectStroke.STROKE_TYPE_MARKER:
                PaintSelector.setMarker(paint);
                break;
            case InsertableObjectStroke.STROKE_TYPE_PEN:
                PaintSelector.setPen(paint);
                break;
            case InsertableObjectStroke.STROKE_TYPE_PENCIL:
                PaintSelector.setPencil(paint);
                break;
            default:
                break;
        }
    }

    @SuppressLint("NewApi")
    private void setSeekBarAlphaEnable(boolean enabled) {
        float alpha = enabled ? BUTTON_ALPHA_ENABLE : BUTTON_ALPHA_DISABLE;
        SeekBar_Alpha.setAlpha(alpha);
        SeekBar_Alpha.setEnabled(enabled);
    }

    @SuppressLint("NewApi")
    private void setAlphaTextEnable(boolean enabled) {
        float alpha = enabled ? BUTTON_ALPHA_ENABLE : BUTTON_ALPHA_DISABLE;
        Alpha_Text.setAlpha(alpha);
        Alpha_Text.setEnabled(enabled);
        TextView alphaPercentage = (TextView) mPenPickerWindow.getContentView()
                .findViewById(R.id.alpha_percentage);
        alphaPercentage.setAlpha(alpha);
        alphaPercentage.setEnabled(enabled);
    }

    private void DrawPreview(ImageView v) {
        Paint DoodlePaint = mDoodlePaint;
        final Resources resources = mContext.getResources();
        float width = resources.getDimension(R.dimen.preview_image_width);
        float height = resources.getDimension(R.dimen.preview_image_height);
        Bitmap PreviewBitmap = Bitmap.createBitmap((int) width, (int) height,
                Bitmap.Config.ARGB_8888);
        Canvas PreviewCanvas = new Canvas(PreviewBitmap);
        draw(PreviewCanvas, DoodlePaint, mDoodleToolCode);
        v.setImageBitmap(PreviewBitmap);
    }

    private void draw(Canvas canvas, Paint paint, int toolCode) {
        drawWithAttr(canvas, mCurrentTool.mDrawToolAttribute, toolCode);
        // switch (toolCode) {
        // case InsertableObjectStroke.STROKE_TYPE_NORMAL:
        // drawTool = new DrawToolPath(mContext);
        // break;
        // case InsertableObjectStroke.STROKE_TYPE_AIRBRUSH:
        // drawTool = new DrawToolAirBrush(mContext);
        // break;
        // case InsertableObjectStroke.STROKE_TYPE_PEN:
        // drawTool = new DrawToolPen(mContext);
        // break;
        // case InsertableObjectStroke.STROKE_TYPE_PENCIL:
        // drawTool = new DrawToolPencil(mContext);
        // break;
        // case InsertableObjectStroke.STROKE_TYPE_MARKER:
        // drawTool = new DrawToolMarker(mContext);
        // break;
        // case InsertableObjectStroke.STROKE_TYPE_BRUSH:
        // drawTool = new DrawToolBrush(mContext);
        // break;
        // }

        // if (drawTool!=null) {
        // drawTool.setAttribute(mCurrentTool.mDrawToolAttribute);
        // drawTool.initFloatPoints(mPoints, false);
        // drawTool.drawPreview(canvas);
        // }

    }

    private void drawWithAttr(Canvas canvas, DrawToolAttribute attribute,
                              int toolCode) {
        InsertableObjectStroke stroke = new InsertableObjectStroke(toolCode);
        stroke.setColor(attribute.color);
        stroke.setAlpha(attribute.alpha);
        stroke.setStrokeWidth(attribute.width);
        VisualStrokeBase drawTool = (VisualStrokeBase) stroke
                .createVisualElement(mContext, null);
        if (drawTool != null) {
            drawTool.initFloatPoints(mPoints, false);
            drawTool.drawPreview(canvas);
        }
    }

    private void drawWithPaint(Canvas canvas, Paint paint, int toolCode) {
        DrawToolAttribute attr = new DrawToolAttribute();
        attr.alpha = paint.getAlpha();
        attr.color = paint.getColor();
        attr.width = paint.getStrokeWidth();
        attr.type = toolCode;
        drawWithAttr(canvas, attr, toolCode);

        // DrawTool drawTool = null;
        // switch (toolCode) {
        // case InsertableObjectStroke.STROKE_TYPE_NORMAL:
        // drawTool = new DrawToolPath(mContext);
        // break;
        // case InsertableObjectStroke.STROKE_TYPE_AIRBRUSH:
        // drawTool = new DrawToolAirBrush(mContext);
        // break;
        // case InsertableObjectStroke.STROKE_TYPE_PEN:
        // drawTool = new DrawToolPen(mContext);
        // break;
        // case InsertableObjectStroke.STROKE_TYPE_PENCIL:
        // drawTool = new DrawToolPencil(mContext);
        // break;
        // case InsertableObjectStroke.STROKE_TYPE_MARKER:
        // drawTool = new DrawToolMarker(mContext);
        // break;
        // case InsertableObjectStroke.STROKE_TYPE_BRUSH:
        // drawTool = new DrawToolBrush(mContext);
        // break;
        // }
        //
        // if (drawTool != null) {
        // drawTool.setAttribute(attr);
        // drawTool.initFloatPoints(mPoints, false);
        // drawTool.drawPreview(canvas);
        // }
    }

    private int getColorId()// jmove
    {
        int Colorid = -1;
        if (mIsPalette) {
            Colorid = R.id.editor_func_color_L;
        } else {
            int color = mCurrentTool.mDrawToolAttribute.color;
            switch (color & 0x00FFFFFF) {// copy from EditroActivity
                case 0x00ffffff:
                    Colorid = R.id.editor_func_color_A;
                    break;
                case 0x00b4b4b4:
                    Colorid = R.id.editor_func_color_B;
                    break;
                case 0x005a5a5a:
                    Colorid = R.id.editor_func_color_C;
                    break;
                case 0x00000000:
                    Colorid = R.id.editor_func_color_D;
                    break;
                case 0x00e70012:
                    Colorid = R.id.editor_func_color_E;
                    break;
                case 0x00ff9900:
                    Colorid = R.id.editor_func_color_F;
                    break;
                case 0x00fff100:
                    Colorid = R.id.editor_func_color_G;
                    break;
                case 0x008fc31f:
                    Colorid = R.id.editor_func_color_H;
                    break;
                case 0x00009944:
                    Colorid = R.id.editor_func_color_I;
                    break;
                case 0x0000a0e9:
                    Colorid = R.id.editor_func_color_J;
                    break;
                case 0x001d2088:
                    Colorid = R.id.editor_func_color_K;
                    break;
                case 0x00e5007f:
                    Colorid = R.id.editor_func_color_M;
                    break;
            }
        }
        return Colorid;
    }

    private void onPaintToolChanged(DrawToolAttribute newToolAtt,
                                    DrawToolAttribute oldToolAtt, AttType[] attTypes) {
        Log.v(LOGV, "TYPE CODE:" + attTypes[0].toString());
        final List<IDrawtoolsChanged> ls = getAllListeners();
        if (ls != null) {
            for (IDrawtoolsChanged iDrawtoolsChanged : ls) {
                iDrawtoolsChanged.onDrawToolChanged(newToolAtt, oldToolAtt,
                        attTypes);
            }
        }
    }

    private void onWindowDismissHappend() {
        mBrushCollection.setBrushsToXMl();
        setColorBrushStrokeToPreference();
    }

    private void addMyToast(String string) {
        //Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
    }

    // --- Carrot: temporary add for limitation of brush width ---
    class BrushAttributes {
        public int Index;
        public int Type;
        public float Width = 2;
        public float MaxWidth = 30;
        public float MinWidth = 2;
        public ColorInfo ColorInfo = new ColorInfo();// Carrot: individually set
        // color of every brush

        public BrushAttributes(int id, int type, float width, float min,
                               float max) {
            Index = id;
            Type = type;
            Width = width;
            MaxWidth = max;
            MinWidth = min;
        }

        // Carrot: individually set color of every brush
        public void SetColorInfo(int color, int id, int x, int y) {
            ColorInfo.Color = color;
            ColorInfo.Index = id;
            ColorInfo.ColorPalette_X = x;
            ColorInfo.ColorPalette_Y = y;
        }

        // Carrot: individually set color of every brush

        public BrushAttributes(int id, int type, float width, float min,
                               float max, ColorInfo cinfo) {
            Index = id;
            Type = type;
            Width = width;
            MaxWidth = max;
            MinWidth = min;
            ColorInfo = cinfo;
        }
    }

    // Carrot: individually set color of every brush
    class ColorInfo {
        public int Color = 0xffe70012;
        public int Index = 4;
        public int ColorPalette_X = 0;
        public int ColorPalette_Y = 0;

        public ColorInfo() {

        }

        public ColorInfo(int color, int id, int x, int y) {
            Color = color;
            Index = id;
            ColorPalette_X = x;
            ColorPalette_Y = y;
        }
    }

    // Carrot: individually set color of every brush
    private ArrayList<BrushAttributes> attrs = new ArrayList<BrushAttributes>();
    private int mCurAttrIndex = 0;
    // Carrot: individually set color of every brush
    private int mColorPalette_X = 0;
    private int mColorPalette_Y = 0;
    private static final int COLOR_PALETTE_INDEX = 100;

    // Carrot: individually set color of every brush
    private void InitBrushAttributs() {
        attrs.add(0, new BrushAttributes(0,
                InsertableObjectStroke.STROKE_TYPE_NORMAL, 2.1f, 2, 80,
                new ColorInfo(0xff1d2088, 10, 0, 0)));
        attrs.add(1, new BrushAttributes(1,
                InsertableObjectStroke.STROKE_TYPE_PEN, 6, 2, 80,
                new ColorInfo(0xff8fc31f, 7, 0, 0)));
        attrs.add(2, new BrushAttributes(2,
                InsertableObjectStroke.STROKE_TYPE_BRUSH, 25.5f, 2, 80,
                new ColorInfo(0xff000000, 3, 0, 0)));
        attrs.add(3, new BrushAttributes(3,
                InsertableObjectStroke.STROKE_TYPE_AIRBRUSH, 30.5f, 2, 80,
                new ColorInfo(0xff00a0e9, 9, 0, 0)));
        attrs.add(4, new BrushAttributes(4,
                InsertableObjectStroke.STROKE_TYPE_PENCIL, 3, 2, 80,
                new ColorInfo(0xff000000, 3, 0, 0)));
        attrs.add(5, new BrushAttributes(5,
                InsertableObjectStroke.STROKE_TYPE_MARKER, 64, 2, 80,
                new ColorInfo(0xff8fc31f, 7, 0, 0)));
    }

    private void SetCurAttr(int mToolCode) {
        SetCurAttrIndex(mToolCode);
        SetValueByCurAttr();
        // mEditorUiUtility.setDoodleTool(mDoodleToolCode);
        // mEditorUiUtility.changeScribleStroke(mStrokeWidth);
        PaintSelector.setColor(mDoodlePaint,
                attrs.get(mCurAttrIndex).ColorInfo.Color);
    }

    private void SetCurAttrInit(int mToolCode) {
        SetCurAttrIndex(mToolCode);
        SetValueByCurAttr();
    }

    private void SetValueByCurAttr() {
        mStrokeWidth = attrs.get(mCurAttrIndex).Width;
        // Carrot: individually set color of every brush
        mSelectedColorIndex = attrs.get(mCurAttrIndex).ColorInfo.Index;
        if (mSelectedColorIndex == COLOR_PALETTE_INDEX) {
            mCustomColor = attrs.get(mCurAttrIndex).ColorInfo.Color;
            mColorPalette_X = attrs.get(mCurAttrIndex).ColorInfo.ColorPalette_X;
            mColorPalette_Y = attrs.get(mCurAttrIndex).ColorInfo.ColorPalette_Y;
            mIsPalette = true;
        } else {
            mIsPalette = false;
        }
        // Carrot: individually set color of every brush
    }

    private void SetCurAttrIndex(int mToolCode) {
        switch (mToolCode) {
            case InsertableObjectStroke.STROKE_TYPE_NORMAL:
                mCurAttrIndex = 0;
                break;
            case InsertableObjectStroke.STROKE_TYPE_AIRBRUSH:
                mCurAttrIndex = 3;
                break;
            case InsertableObjectStroke.STROKE_TYPE_PEN:
                mCurAttrIndex = 1;
                break;
            case InsertableObjectStroke.STROKE_TYPE_MARKER:
                mCurAttrIndex = 5;
                break;
            case InsertableObjectStroke.STROKE_TYPE_BRUSH:
                mCurAttrIndex = 2;
                break;
            case InsertableObjectStroke.STROKE_TYPE_PENCIL:
                mCurAttrIndex = 4;
                break;
        }
    }

    /**
     * can't find return -1
     * @param toolcode
     * @return
     */
    private int convertToolCodeToIndex(int toolcode){
        int index=-1;
        switch (toolcode) {
            case InsertableObjectStroke.STROKE_TYPE_NORMAL:
                index = 0;
                break;
            case InsertableObjectStroke.STROKE_TYPE_AIRBRUSH:
                index = 3;
                break;
            case InsertableObjectStroke.STROKE_TYPE_PEN:
                index = 1;
                break;
            case InsertableObjectStroke.STROKE_TYPE_MARKER:
                index = 5;
                break;
            case InsertableObjectStroke.STROKE_TYPE_BRUSH:
                index = 2;
                break;
            case InsertableObjectStroke.STROKE_TYPE_PENCIL:
                index = 4;
                break;
        }
        return index;
    }
    private void UpdatePopupByCurBrush() {
        // width
        float WidthPrecent = (attrs.get(mCurAttrIndex).Width - attrs
                .get(mCurAttrIndex).MinWidth)
                / (attrs.get(mCurAttrIndex).MaxWidth - attrs.get(mCurAttrIndex).MinWidth);
        int WidthProgress = (int) (WidthPrecent * 100);
        final View parentView = mPenPickerWindow.getContentView();
        SeekBar SeekBar_width = (SeekBar) parentView
                .findViewById(R.id.seekbar_width);
        SeekBar_width.setProgress(WidthProgress);
        // Carrot: individually set color of every brush
        if (mIsPalette) {
            for (int id : mEditorPhoneIdList.editorDoodleUnityColorIds) {
                View vv = parentView.findViewById(id);
                if (vv != null) {
                    if (vv.getId() == R.id.editor_func_color_L) {
                        vv.setSelected(true);
                        ((ImageView) vv)
                                .setImageResource(R.drawable.color_frame_p);
                        ((ImageView) vv).setBackgroundColor(mCustomColor);
                    } else {
                        vv.setSelected(false);
                        ((ImageView) vv)
                                .setImageResource(R.drawable.color_frame_n);
                    }
                }
            }
            ColorPickerViewCustom ColorPicker = (ColorPickerViewCustom) parentView
                    .findViewById(R.id.color_picker_view);
            ColorPicker.setColorXY(mColorPalette_X, mColorPalette_Y);
        }
        // Carrot: individually set color of every brush
    }

    // --- Carrot: temporary add for limitation of brush width ---

    private BrushLibraryAdapter.INotifyOuter notifyOuter = new BrushLibraryAdapter.INotifyOuter() {

        @Override
        public void showAddBrushButton() {
            // TODO Auto-generated method stub
            ImageButton addBrushBtn = (ImageButton) mPenPickerWindow
                    .getContentView().findViewById(R.id.add_brush);
            if (addBrushBtn.getVisibility() == View.INVISIBLE)
                addBrushBtn.setVisibility(View.VISIBLE);
        }

        @Override
        public void selectBrush(BrushInfo info) {
            // TODO Auto-generated method stub
            DrawToolsPicker.this.selectBrush(info);
            // if (mPenPickerWindow!=null) {
            // mPenPickerWindow.dismiss();
            // }
        }

        @Override
        public void draw(Canvas canvas, Paint paint, int toolCode) {
            // TODO Auto-generated method stub
            drawWithPaint(canvas, paint, toolCode);
        }
    };

    private void selectBrush(BrushInfo brush) {
        mOldTool = mCurrentTool;
        mDoodleToolCode = brush.getDoodleToolCode();
        mStrokeWidth = brush.getStrokeWidth();
        mSelectedColorIndex = brush.getSelectedColorIndex();
        mIsPalette = brush.getIsPalette();
        // mIsColorMode = brush.getIsColorMode();
        mCustomColor = brush.getCustomColor();
        mDoodleToolAlpha = brush.getDoodleToolAlpha();
        int colorX = brush.getCurrentX();
        int colorY = brush.getCurrentY();

        if (mIsPalette && mIsCustomColorSet) // smilefish
        {
            View vv = mPenPickerWindow.getContentView().findViewById(
                    R.id.editor_func_color_L);
            ((ImageView) vv).setBackgroundColor(mCustomColor);

            // mEditorUiUtility.changeColor(mCustomColor);
            mDoodlePaint.setColor(mCustomColor);
            ColorPickerViewCustom ColorPicker = (ColorPickerViewCustom) mPenPickerWindow
                    .getContentView().findViewById(R.id.color_picker_view);
            ColorPicker.setColorXY(colorX, colorY);
            // Carrot: individually set color of every brush
            SetCurAttrIndex(mDoodleToolCode);
            attrs.get(mCurAttrIndex).ColorInfo.Index = COLOR_PALETTE_INDEX;
            attrs.get(mCurAttrIndex).ColorInfo.Color = mCustomColor;
            attrs.get(mCurAttrIndex).ColorInfo.ColorPalette_X = colorX;
            attrs.get(mCurAttrIndex).ColorInfo.ColorPalette_Y = colorY;
            // Carrot: individually set color of every brush
        } else {
            // mDoodlePaint.setColor(mBrushSelector.getDefaultColorCodes()[mSelectedColorIndex]);
            // Carrot: individually set color of every brush
            SetCurAttrIndex(mDoodleToolCode);
            attrs.get(mCurAttrIndex).ColorInfo.Index = mSelectedColorIndex;
            attrs.get(mCurAttrIndex).ColorInfo.Color = _DefaultColorCodes[mSelectedColorIndex];
            // Carrot: individually set color of every brush
        }

        for (int id : mEditorPhoneIdList.editorDoodleUnityColorIds) {
            View vv = mPenPickerWindow.getContentView().findViewById(id);
            if (vv != null) {
                ((ImageView) vv).setImageResource(R.drawable.color_frame_n);
            }

        }

        // mEditorUiUtility.setDoodleTool(mDoodleToolCode);
        // mEditorUiUtility.changeScribleStroke(mStrokeWidth);

        if (mDoodleToolCode != InsertableObjectStroke.STROKE_TYPE_MARKER) {
            if ((SeekBar_Alpha != null) && (SeekBar_Alpha.isEnabled())) {
                setSeekBarAlphaEnable(false);
            }
            if ((Alpha_Text != null) && (Alpha_Text.isEnabled())) {
                setAlphaTextEnable(false);
            }
        } else {
            if (SeekBar_Alpha != null) {
                float AlphaPrecent = mDoodleToolAlpha / 255.0f;
                int AlphaProgress = (int) (AlphaPrecent * 100);
                SeekBar_Alpha.setProgress(AlphaProgress);
                setSeekBarAlphaEnable(true);
            }
            if ((Alpha_Text != null) && (!Alpha_Text.isEnabled())) {
                setAlphaTextEnable(true);
            }
        }

        SeekBar SeekBar_width = (SeekBar) mPenPickerWindow.getContentView()
                .findViewById(R.id.seekbar_width);

        // float WidthPrecent = (float)mStrokeWidth /(float)30;
        // int WidthProgress = (int)(WidthPrecent * 100);
        // SeekBar_width.setProgress(WidthProgress);

        // --- Carrot: temporary add for limitation of brush width ---
        SetCurAttrIndex(mDoodleToolCode);
        attrs.get(mCurAttrIndex).Width = mStrokeWidth;
        float WidthPrecent = (attrs.get(mCurAttrIndex).Width - attrs
                .get(mCurAttrIndex).MinWidth)
                / (attrs.get(mCurAttrIndex).MaxWidth - attrs.get(mCurAttrIndex).MinWidth);
        int WidthProgress = (int) (WidthPrecent * 100);
        SeekBar_width.setProgress(WidthProgress);
        // --- Carrot: temporary add for limitation of brush width ---
        mCurrentTool = genToolInfo(attrs.get(mCurAttrIndex));
        onPaintToolChanged(mCurrentTool.mDrawToolAttribute,
                mOldTool.mDrawToolAttribute, new AttType[] { AttType.TYPE });
        if (mPenPreview != null)
            DrawPreview(mPenPreview);
        selectCurrentBrushOnPopup();
    }
    /**
     * Get the DrawToolAttribute by though the type,return null if could't found
     * @param tooltype
     * @return
     */
    public DrawToolAttribute getDrawToolAttributeByToolType(int tooltype){
        int index = convertToolCodeToIndex(tooltype);
        if (index == -1||attrs.size()<=index) {
            return null;
        }
        return genToolInfo(attrs.get(index)).mDrawToolAttribute;
    }
    private class ToolInfo {
        public DrawToolAttribute mDrawToolAttribute = new DrawToolAttribute();

    }

    private class PickerCtlImpl implements IPickerControl {

        @Override
        public boolean isPickerShowing() {
            // TODO Auto-generated method stub
            if (mPenPickerWindow == null) {
                return false;
            }
            return mPenPickerWindow.isShowing();
        }

        @Override
        public void showPicker(View v) throws IllegalAccessException {
            // TODO Auto-generated method stub
            if ((STATUS_FLAG & FLAG_INIT) != FLAG_INIT) {
                throw new IllegalAccessException("not init !");
            }
            if (mPenPickerWindow != null && !mPenPickerWindow.isShowing()) {
                mPenPickerWindow.showAsDropDown(v);
            }
        }
        @Override
        public void showPicker(View v, int xoff, int yoff)
                throws IllegalAccessException {
            // TODO Auto-generated method stub
            if ((STATUS_FLAG & FLAG_INIT) != FLAG_INIT) {
                throw new IllegalAccessException("not init !");
            }
            if (mPenPickerWindow != null && !mPenPickerWindow.isShowing()) {
                mPenPickerWindow.showAsDropDown(v,xoff,yoff);
            }
        }
        @Override
        public void dismissPicker() {
            // TODO Auto-generated method stub
            if (mPenPickerWindow != null && mPenPickerWindow.isShowing()) {
                mPenPickerWindow.dismiss();
            }
        }

        @Override
        public void showPickerAtLocation(View parent, int screenXoff,
                                         int screenYoff) throws IllegalAccessException {
            // TODO Auto-generated method stub
            if ((STATUS_FLAG & FLAG_INIT) != FLAG_INIT) {
                throw new IllegalAccessException("not init !");
            }
            mPenPickerWindow.showAtLocation(parent, Gravity.LEFT|Gravity.TOP, screenXoff, screenYoff);
        }

        @Override
        public void showPickerUnderView(View parent, int xoff, int yoff)
                throws IllegalAccessException {
            // TODO Auto-generated method stub
            if ((STATUS_FLAG & FLAG_INIT) != FLAG_INIT) {
                throw new IllegalAccessException("not init !");
            }
            int height = parent.getMeasuredHeight();
            int location[] = new int[2];
            parent.getLocationInWindow(location);
            mPenPickerWindow.showAtLocation(parent, Gravity.LEFT|Gravity.TOP, location[0]+xoff, location[1]+height+yoff);
        }

    }

}