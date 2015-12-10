package com.jacky.commondraw.wigets.erasepopup;

import android.content.Context;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.jacky.commondraw.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class ErasePopupWindow extends PopupWindow {
    private static HashMap<EraseType, Float> sEraseWidthMap;
    static {
        sEraseWidthMap = new HashMap<ErasePopupWindow.EraseType, Float>();
        sEraseWidthMap.put(EraseType.TYPE1, 10f);
        sEraseWidthMap.put(EraseType.TYPE2, 20f);
        sEraseWidthMap.put(EraseType.TYPE3, 30f);
        sEraseWidthMap.put(EraseType.CLEAR, -1f);
    }

    private Context mContext;
    private List<onEraseChangedListener> mEraseChangedListeners;

    private RadioGroup mEraseRadioGroup;
    private RadioButton mEraseRadioButton1;
    private RadioButton mEraseRadioButton2;
    private RadioButton mEraseRadioButton3;
    private RadioButton mEraseClearRadioButton;
    private View mRootView;

    private EraseType mEraseType = EraseType.TYPE2;

    public ErasePopupWindow(Context context) {
        super(context);
        mContext = context;
        mEraseChangedListeners = new ArrayList<onEraseChangedListener>();

        // LayoutInflater inflater = LayoutInflater.from(context);
        // mRootView = inflater.inflate(R.layout.gb_erase_popupwindow, null);
        mRootView = View.inflate(mContext,
                R.layout.gb_erase_popupwindow, null);

        setContentView(mRootView);

        int width = mContext.getResources()
                .getDimensionPixelSize(R.dimen.gb_erasepopupwindow_width);
        int height = mContext.getResources()
                .getDimensionPixelSize(R.dimen.gb_erasepopupwindow_height);
        setWidth(width);
        setHeight(height);

        finadAllViews();
        initViews();
    }

    public void addOnEraseChangedListener(onEraseChangedListener listener) {
        if (listener != null)
            mEraseChangedListeners.add(listener);
    }

    public void removeOnEraseChangedListener(onEraseChangedListener listener) {
        if (listener != null)
            mEraseChangedListeners.remove(listener);
    }

    public EraseType getEraseType() {
        return mEraseType;
    }

    public void setEraseType(EraseType mEraseType) {
        this.mEraseType = mEraseType;
        switch (mEraseType) {
            case TYPE1:
                mEraseRadioButton1.setChecked(true);
                break;
            case TYPE2:
                mEraseRadioButton2.setChecked(true);
                break;
            case TYPE3:
                mEraseRadioButton3.setChecked(true);
                break;
            case CLEAR:
                mEraseClearRadioButton.setChecked(true);
                break;
            default:
                break;
        }
    }

    /**
     * 通过eraseType获得其对应的宽度值
     *
     * @param width
     * @return 如果没有则返回null
     */
    public static EraseType getEraseTypeByWidth(float width) {
        EraseType eraseType = null;
        if (sEraseWidthMap.containsValue(width)) {
            Set<Map.Entry<EraseType, Float>> set = sEraseWidthMap
                    .entrySet();
            for (Map.Entry<EraseType, Float> entry : set) {
                if (entry.getValue() == width) {
                    eraseType = entry.getKey();
                    break;
                }
            }
        }
        return eraseType;
    }

    /**
     * 通过eraseType获得其对应的宽度值
     *
     * @param eraseType
     * @return 如果没有则返回Float.MIN_VALUE
     */
    public static float getWidthByEraseType(EraseType eraseType) {
        float width = Float.MIN_VALUE;
        if (sEraseWidthMap.containsKey(eraseType)) {
            width = sEraseWidthMap.get(eraseType);
        }
        return width;
    }

    private void finadAllViews() {
        mEraseRadioGroup = (RadioGroup) mRootView
                .findViewById(R.id.eraseRadioGroup);
        mEraseRadioButton1 = (RadioButton) mRootView
                .findViewById(R.id.eraseRadioButton1);
        mEraseRadioButton2 = (RadioButton) mRootView
                .findViewById(R.id.eraseRadioButton2);
        mEraseRadioButton3 = (RadioButton) mRootView
                .findViewById(R.id.eraseRadioButton3);
        mEraseClearRadioButton = (RadioButton) mRootView
                .findViewById(R.id.eraseRadioButtonClear);
    }

    private void initViews() {

        mEraseRadioGroup
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        // TODO Auto-generated method stub
                        if(checkedId ==  R.id.eraseRadioButton1 ){
                            mEraseType = EraseType.TYPE1;
                        }else if(checkedId == R.id.eraseRadioButton2){
                            mEraseType = EraseType.TYPE2;
                        }else if(checkedId == R.id.eraseRadioButton3){
                            mEraseType = EraseType.TYPE3;
                        }

                        fireEraseChangedListener(mEraseType);
                    }
                });
        mEraseClearRadioButton.setOnClickListener(new View.OnClickListener() {// clear
            // erase每次点击，每次发送通知

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                mEraseType = EraseType.CLEAR;
                fireEraseChangedListener(mEraseType);
            }
        });
    }

    private void fireEraseChangedListener(EraseType eraseType) {
        Float width = sEraseWidthMap.get(mEraseType);
        if (width == null) {
            return;
        }
        for (onEraseChangedListener listener : mEraseChangedListeners) {
            listener.onEraseChanged(width);
        }
    }

    public enum EraseType {
        TYPE1, TYPE2, TYPE3, CLEAR
    }

    /**
     * 定义橡皮擦改变时候的回调
     *
     * @author noah
     *
     */
    public static interface onEraseChangedListener {
        // public void onEraseChanged(EraseType eraseType);
        /**
         *
         * @param width
         *            : >= 0表示正常宽度;= -1 : 表示为擦除所有
         */
        public void onEraseChanged(float width);

    }
}

