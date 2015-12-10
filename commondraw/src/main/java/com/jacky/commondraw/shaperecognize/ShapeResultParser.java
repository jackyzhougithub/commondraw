package com.jacky.commondraw.shaperecognize;

import android.util.Log;

import com.jacky.commondraw.model.InsertableObjectBase;
import com.jacky.commondraw.model.propertyconfig.PropertyConfigStroke;
import com.jacky.commondraw.model.stroke.InsertableObjectStroke;
import com.jacky.commondraw.model.stroke.StylusPoint;
import com.visionobjects.myscript.shape.DecorationType;
import com.visionobjects.myscript.shape.ShapeDecoratedEllipticArcData;
import com.visionobjects.myscript.shape.ShapeDecoratedLineData;
import com.visionobjects.myscript.shape.ShapeDocument;
import com.visionobjects.myscript.shape.ShapeEllipticArcData;
import com.visionobjects.myscript.shape.ShapeLineData;
import com.visionobjects.myscript.shape.ShapePointData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class ShapeResultParser {
    public static final String TAG = "ShapeResultParser";
    private static final int ARROW_DEGREE = 15;
    private static final int ARROW_LINE_LENGTH_FACTOR = 15;
    private ShapeDocument mShapedocument = null;
    private MyShape mShape = null;
    private List<InsertableObjectBase> mObjectList;
    private PropertyConfigStroke mProperty;

    public ShapeResultParser(MyShape asusShape, ShapeDocument shapedocument,
                             PropertyConfigStroke property) {
        this.mShape = asusShape;
        this.mShapedocument = shapedocument;
        this.mObjectList = new ArrayList<InsertableObjectBase>();
        this.mProperty = property;
    }

    public List<InsertableObjectBase> getShapeResult() {
        getRecognizedShapeList();
        return this.mObjectList;
    }

    private void getRecognizedShapeList() {
        if (mShape == null || mShapedocument == null)
            return;
        ArrayList<Object> shapeList = mShape
                .getShapeAndStrok(mShapedocument);
        Log.i(TAG, "shapeList count = " + Integer.toString(shapeList.size()));
        if (shapeList != null && shapeList.size() != 0) {
            doDrawShapeToBitmap(shapeList);
        } else {
            // show error info
            Log.e(TAG, "shape recognize error!");
        }
    }

    List<StylusPoint> getStylusPoints(float[] points) {
        List<StylusPoint> list = new ArrayList<StylusPoint>();
        for (int i = 0, j = 0; i < points.length / 2; i++) {
            StylusPoint point = new StylusPoint();
            point.x = points[j];
            point.y = points[j + 1];
            j += 2;
            list.add(point);
        }
        return list;
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

    InsertableObjectShape doDrawArrow(float[] line, float ang, float size) {
        float tempx = line[2] - line[0];
        float tempy = line[3] - line[1];
        MyPoint p1 = new MyPoint(tempx, tempy, size);
        float[] dst1 = p1.getRotateResMyPoint(ang);
        float[] pointsArrowLine = new float[4];
        pointsArrowLine[0] = line[0];
        pointsArrowLine[1] = line[1];
        pointsArrowLine[2] = (line[0] + dst1[0]);
        pointsArrowLine[3] = (line[1] + dst1[1]);

        InsertableObjectShape stroke = InsertableObjectShape
                .newInsertableObjectShape(mProperty);
        stroke.setPoints(getStylusPoints(pointsArrowLine));
        return stroke;
    }

    List<InsertableObjectShape> doDrawArcArrow(Boolean direct,
                                               float realStartAng, float[] info, float ang, double size) {
        MyPoint startPoint;
        MyPoint tangentPoint;

        if (realStartAng == 90) {
            startPoint = new MyPoint(0, info[3]);
            tangentPoint = new MyPoint(-1, info[3]);
        } else if (realStartAng == 270) {
            startPoint = new MyPoint(0, -info[3]);
            tangentPoint = new MyPoint(1, -info[3]);
        } else {
            double radAng = realStartAng / 180 * Math.PI;
            double tanValue = Math.tan(radAng);
            double dx = (1 / Math.sqrt(1.0 / (info[2] * info[2])
                    + Math.pow(tanValue, 2) / (info[3] * info[3])));
            double dy = (dx * tanValue);

            float x = (float) dx;
            float y = (float) dy;

            double sinValue = Math.sin(radAng);
            double cosValue = Math.cos(radAng);

            float tangentPointy = 1;
            if (sinValue > 0 && y < 0) {
                y = -y;
            } else if (sinValue < 0 && y > 0) {
                y = -y;
            }

            if (cosValue < 0) {
                x = -x;
                if (direct) {
                    tangentPointy = y - 1;
                } else {
                    tangentPointy = y + 1;
                }
            } else {
                if (direct) {
                    tangentPointy = y + 1;
                } else {
                    tangentPointy = y - 1;
                }
            }

            startPoint = new MyPoint(x, y);
            tangentPoint = new MyPoint((1 - y * tangentPointy
                    / (info[3] * info[3]))
                    * (info[2] * info[2]) / x, tangentPointy);
        }

        float[] startPointAfterRotate = startPoint.getRotateResMyPoint(info[6]);
        float[] tangentPointAfterRotate = tangentPoint
                .getRotateResMyPoint(info[6]);

        float[] pointsEnd = new float[4];
        pointsEnd[0] = (short) (startPointAfterRotate[0] + 0.5 + (short) info[0]);
        pointsEnd[1] = (short) (startPointAfterRotate[1] + 0.5 + (short) info[1]);
        pointsEnd[2] = (short) (pointsEnd[0] + ((tangentPointAfterRotate[0] - startPointAfterRotate[0]) * 100));
        pointsEnd[3] = (short) (pointsEnd[1] + ((tangentPointAfterRotate[1] - startPointAfterRotate[1]) * 100));

        doDrawArrow(pointsEnd, ang, (float) size);
        List<InsertableObjectShape> arcArrows = new ArrayList<InsertableObjectShape>();
        InsertableObjectShape leftArrowStroke = doDrawArrow(pointsEnd, ang,
                (float) size);
        arcArrows.add(leftArrowStroke);

        InsertableObjectShape rightArrowStroke = doDrawArrow(pointsEnd, -ang,
                (float) size);
        arcArrows.add(rightArrowStroke);
        return arcArrows;
    }

    void doDrawShapeToBitmap(ArrayList<Object> shapeList) {
        for (Object shape : shapeList) {
            if (shape instanceof ShapeLineData) {
                ShapeLineData shapedata = (ShapeLineData) shape;
                final ShapePointData p1_ = shapedata.getP1();
                final ShapePointData p2_ = shapedata.getP2();

                float[] points = new float[4];
                points[0] = p1_.getX();
                points[1] = p1_.getY();
                points[2] = p2_.getX();
                points[3] = p2_.getY();

                InsertableObjectShape stroke = InsertableObjectShape
                        .newInsertableObjectShape(mProperty);
                stroke.setPoints(getStylusPoints(points));
                mObjectList.add(stroke);
                Log.i(TAG, "shape instanceof ShapeLineData");
            } else if (shape instanceof ShapeEllipticArcData) {
                final ShapeEllipticArcData data = (ShapeEllipticArcData) shape;
                InsertableObjectShape stroke = InsertableObjectShape
                        .newInsertableObjectShape(mProperty);
                stroke.setArcData(data);
                mObjectList.add(stroke);
                Log.i(TAG, "shape instanceof  ShapeEllipticArcData");
            } else if (shape instanceof ShapeDecoratedLineData) {

                ShapeDecoratedLineData shapeDecoratedLineData = (ShapeDecoratedLineData) shape;
                final ShapePointData p1_ = shapeDecoratedLineData.getLine()
                        .getP1();
                final ShapePointData p2_ = shapeDecoratedLineData.getLine()
                        .getP2();

                float[] points = new float[4];
                points[0] = p1_.getX();
                points[1] = p1_.getY();
                points[2] = p2_.getX();
                points[3] = p2_.getY();

                InsertableObjectShape stroke = InsertableObjectShape
                        .newInsertableObjectShape(mProperty);
                stroke.setPoints(getStylusPoints(points));
                mObjectList.add(stroke);

                if (shapeDecoratedLineData.getP1Decoration() == DecorationType.ARROW_HEAD) {
                    InsertableObjectShape leftArrowStroke = doDrawArrow(points,
                            ARROW_DEGREE,
                            (float) Math.sqrt(mProperty.getStrokeWidth())
                                    * ARROW_LINE_LENGTH_FACTOR);
                    mObjectList.add(leftArrowStroke);

                    InsertableObjectShape rightArrowStroke = doDrawArrow(
                            points, -ARROW_DEGREE,
                            (float) Math.sqrt(mProperty.getStrokeWidth())
                                    * ARROW_LINE_LENGTH_FACTOR);
                    mObjectList.add(rightArrowStroke);
                }

                if (shapeDecoratedLineData.getP2Decoration() == DecorationType.ARROW_HEAD) {
                    float[] pointsEnd = new float[4];
                    pointsEnd[0] = points[2];
                    pointsEnd[1] = points[3];
                    pointsEnd[2] = points[0];
                    pointsEnd[3] = points[1];

                    InsertableObjectShape leftArrowStroke = doDrawArrow(
                            pointsEnd, ARROW_DEGREE,
                            (float) Math.sqrt(mProperty.getStrokeWidth())
                                    * ARROW_LINE_LENGTH_FACTOR);
                    mObjectList.add(leftArrowStroke);

                    InsertableObjectShape rightArrowStroke = doDrawArrow(
                            pointsEnd, -ARROW_DEGREE,
                            (float) Math.sqrt(mProperty.getStrokeWidth())
                                    * ARROW_LINE_LENGTH_FACTOR);
                    mObjectList.add(rightArrowStroke);

                }
                Log.i(TAG, "shape instanceof ShapeDecoratedLineData");

            } else if (shape instanceof ShapeDecoratedEllipticArcData) {
                final ShapeDecoratedEllipticArcData data = (ShapeDecoratedEllipticArcData) shape;

                final ShapeEllipticArcData arcData = data.getArc();
                InsertableObjectShape stroke = InsertableObjectShape
                        .newInsertableObjectShape(mProperty);
                stroke.setArcData(arcData);
                mObjectList.add(stroke);

                final ShapePointData center = data.getArc().getCenter();
                short maxR = (short) data.getArc().getMaxRadius();
                short minR = (short) data.getArc().getMinRadius();
                float[] info = new float[7];
                info[0] = (short) center.getX();
                info[1] = (short) center.getY();
                info[2] = maxR;
                info[3] = minR;

                info[4] = (float) Math.toDegrees(data.getArc().getStartAngle());
                info[5] = (float) Math.toDegrees(data.getArc().getSweepAngle());
                info[6] = (float) Math
                        .toDegrees(data.getArc().getOrientation());
                if (data.getFirstDecoration() == DecorationType.ARROW_HEAD) {
                    List<InsertableObjectShape> arcArrows = doDrawArcArrow(data
                                    .getArc().getSweepAngle() < 0 ? false : true,
                            (float) Math.toDegrees(data.getArc()
                                    .getStartAngle()), info, ARROW_DEGREE,
                            Math.sqrt(mProperty.getStrokeWidth())
                                    * ARROW_LINE_LENGTH_FACTOR);
                    for (InsertableObjectShape arcArrow : arcArrows) {
                        mObjectList.add(arcArrow);
                    }
                }

                if (data.getLastDecoration() == DecorationType.ARROW_HEAD) {
                    List<InsertableObjectShape> arcArrows = doDrawArcArrow(data
                                    .getArc().getSweepAngle() < 0 ? true : false,
                            (float) Math.toDegrees(data.getArc()
                                    .getSweepAngle()
                                    + data.getArc().getStartAngle()), info,
                            ARROW_DEGREE, Math.sqrt(mProperty.getStrokeWidth())
                                    * ARROW_LINE_LENGTH_FACTOR);
                    for (InsertableObjectShape arcArrow : arcArrows) {
                        mObjectList.add(arcArrow);
                    }
                }
                Log.i(TAG, "shape instanceof ShapeDecoratedEllipticArcData");

            } else if (shape instanceof Integer) {
                Log.i(TAG, "shape instanceof Integer");
            }
        }
    }
}
