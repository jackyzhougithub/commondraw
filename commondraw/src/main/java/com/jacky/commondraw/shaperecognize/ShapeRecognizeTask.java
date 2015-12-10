package com.jacky.commondraw.shaperecognize;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.jacky.commondraw.model.InsertableObjectBase;
import com.jacky.commondraw.model.propertyconfig.PropertyConfigStroke;
import com.jacky.commondraw.model.stroke.InsertableObjectStroke;
import com.jacky.commondraw.model.stroke.StylusPoint;
import com.jacky.commondraw.views.doodleview.IInternalDoodle;
import com.jacky.commondraw.views.doodleview.opereation.DoodleOperation;
import com.jacky.commondraw.views.doodleview.opereation.DrawAllOperation;
import com.visionobjects.myscript.shape.ShapeDocument;

import java.util.List;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 * 后续不用AsyncTask
 */
public class ShapeRecognizeTask extends AsyncTask<Void, Void, Void> {
    public static final String TAG = "ShapeRecognizeTask";
    private ProgressDialog mProgressDialog;
    private ShapeDocument shapedocument = null;
    private MyShape mShape = null;
    private InsertableObjectStroke mCurrentStroke;
    private IInternalDoodle mInternalDoodle = null;

    public ShapeRecognizeTask(IInternalDoodle internalDoodle,
                              InsertableObjectStroke currentStroke, MyShape asusShape) {
        mInternalDoodle = internalDoodle;
        mCurrentStroke = currentStroke;
        mShape = asusShape;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    public void setDialog(ProgressDialog d) {
        mProgressDialog = d;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        if (mShape != null) {
            try {
                mShape.addStroke(getFloatXY(mCurrentStroke));
                shapedocument = mShape.getResultShapeDocument();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        publishProgress();
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.setProgress(1);
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (shapedocument == null) {
            Log.i(TAG, "shapedocument is null!");
            return;
        }
        try {
            PropertyConfigStroke property = new PropertyConfigStroke();
            property.setAlpha(mCurrentStroke.getAlpha());
            property.setColor(mCurrentStroke.getColor());
            property.setStrokeWidth(mCurrentStroke.getStrokeWidth());
            mInternalDoodle.getShapeRecognizeManager().setConfigProperty(
                    property);
            ShapeResultParser parser = new ShapeResultParser(mShape,
                    shapedocument, property);
            List<InsertableObjectBase> objects = parser.getShapeResult();
            if (objects != null && objects.size() > 0) {
                if (objects.containsAll(ShapeRecognizeManager.mObjectList)
                        && ShapeRecognizeManager.mObjectList
                        .containsAll(objects)) {
                    int shapeDocIndex = -1;
                    sendOperations(objects, shapeDocIndex); // 本次识别引擎无法辨识，系统会返回之前识别的缓存数据，此时
                    // 仅仅重绘上次识别结果
                } else {
                    ShapeDocument mDoc = (ShapeDocument) shapedocument.clone();
                    this.mInternalDoodle.getShapeRecognizeManager()
                            .addShapeDoucument(mDoc);
                    int shapeDocIndex = this.mInternalDoodle
                            .getShapeRecognizeManager().getDocuments().size() - 1;
                    sendOperations(objects, shapeDocIndex);
                    ShapeRecognizeManager.mObjectList.clear();
                    ShapeRecognizeManager.mObjectList.addAll(objects);
                    this.mInternalDoodle.getShapeRecognizeManager()
                            .addConfigProperty(property);
                }
            } else {// 本次识别失败，重绘已经保存的内容
                DoodleOperation operation = new DrawAllOperation(
                        mInternalDoodle.getFrameCache(),
                        mInternalDoodle.getModelManager(),
                        mInternalDoodle.getVisualManager());
                mInternalDoodle.insertOperation(operation);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    float[] getFloatXY(InsertableObjectStroke stroke) {
        List<StylusPoint> points = stroke.getPoints();
        float[] xys = new float[points.size() * 2];
        int i = 0;
        for (StylusPoint point : points) {
            xys[i] = point.x;
            xys[i + 1] = point.y;
            i += 2;
        }
        return xys;
    }

    void sendOperations(List<InsertableObjectBase> list, int shapeDocIndex) {
        if (mInternalDoodle != null) {
            DoodleOperation operation = new ShapeRecognizeOperation(
                    mInternalDoodle, list, shapeDocIndex);
            mInternalDoodle.insertOperation(operation);
        }
    }

}