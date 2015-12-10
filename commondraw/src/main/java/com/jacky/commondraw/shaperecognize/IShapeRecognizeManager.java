package com.jacky.commondraw.shaperecognize;

import com.jacky.commondraw.model.InsertableObjectBase;
import com.jacky.commondraw.model.propertyconfig.PropertyConfigStroke;
import com.visionobjects.myscript.shape.ShapeDocument;

import java.util.List;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 * 图形识别接口
 * 第三方图形识别库
 */
public interface IShapeRecognizeManager {
    /**
     * 判斷是否支持VO識別
     *
     * @return
     */
    public boolean canDoVO();

    /**
     * 初始化識別引擎
     *
     * @return
     */
    public void initShape();

    /**
     * 反初始化識別引擎
     *
     * @return
     */
    public void deInitShape();

    /**
     * 清空識別引擎緩存數據，接下來的識別將不會與之前的識別產生關聯行為
     *
     * @return
     */
    public void clearAsusShape();

    /**
     * 是否保存識別結果，bSave為true時保存當前識別結果；bSave為false時清空所有識別數據
     *
     * @return
     */
    public void saveShapeResult(boolean bSave);

    /**
     * 返回识别结果
     *
     * @return
     */
    public List<InsertableObjectBase> getShapeResult();

    public void setShapeDoucument(ShapeDocument document);

    public void addShapeDoucument(ShapeDocument document);

    public List<ShapeDocument> getDocuments();

    public void setConfigProperty(PropertyConfigStroke property);

    public void addConfigProperty(PropertyConfigStroke property);

    public List<PropertyConfigStroke> getConfigProperties();
}
