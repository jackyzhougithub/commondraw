package com.jacky.commondraw.shaperecognize;

import com.visionobjects.myscript.engine.Engine;
import com.visionobjects.myscript.engine.EngineObject;
import com.visionobjects.myscript.shape.ShapeBeautifier;
import com.visionobjects.myscript.shape.ShapeCandidate;
import com.visionobjects.myscript.shape.ShapeDecoratedEllipticArc;
import com.visionobjects.myscript.shape.ShapeDecoratedEllipticArcData;
import com.visionobjects.myscript.shape.ShapeDecoratedLine;
import com.visionobjects.myscript.shape.ShapeDecoratedLineData;
import com.visionobjects.myscript.shape.ShapeDocument;
import com.visionobjects.myscript.shape.ShapeEllipticArc;
import com.visionobjects.myscript.shape.ShapeEllipticArcData;
import com.visionobjects.myscript.shape.ShapeErased;
import com.visionobjects.myscript.shape.ShapeInkRange;
import com.visionobjects.myscript.shape.ShapeKnowledge;
import com.visionobjects.myscript.shape.ShapeLine;
import com.visionobjects.myscript.shape.ShapeLineData;
import com.visionobjects.myscript.shape.ShapeModel;
import com.visionobjects.myscript.shape.ShapePrimitive;
import com.visionobjects.myscript.shape.ShapeRecognized;
import com.visionobjects.myscript.shape.ShapeRecognizer;
import com.visionobjects.myscript.shape.ShapeRejected;
import com.visionobjects.myscript.shape.ShapeScratchOut;
import com.visionobjects.myscript.shape.ShapeSegment;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class MyShape {
    private Engine mEngine = null;
    private ShapeRecognizer mShapeRecognizer = null;
    private ShapeBeautifier mShapeBeautifier = null;
    private ShapeDocument mShapeDocument = null;

    public void initShapeRecognizer() {
        mEngine = Engine.create(Cert.getBytes());
        mShapeRecognizer = ShapeRecognizer.create(mEngine);
        final ShapeKnowledge shapeKnowledge = (ShapeKnowledge) EngineObject
                .load(mEngine, CFG.PATH_TO_ASSETS + CFG.SHAPE_KNOWLEDGE_RES);

        System.out.println("ShapeKnowledge resource loaded successfully");
        mShapeRecognizer.attach(shapeKnowledge);

        mShapeBeautifier = ShapeBeautifier.create(mEngine);
        mShapeBeautifier.attach(shapeKnowledge);
        shapeKnowledge.dispose();
    }

    public void deinitShapeRecognizer() {
        if (mShapeBeautifier != null)
            mShapeBeautifier.dispose();
        if (mShapeRecognizer != null)
            mShapeRecognizer.dispose();
        if (mEngine != null)
            mEngine.dispose();
    }

    public void prepareShapeDocument() {
        mShapeDocument = ShapeDocument.create(mEngine);
    }

    public void addStroke(float[] xy) {
        if (xy.length >= 2) {
            if (mShapeDocument != null) {
                mShapeDocument.addStroke(xy, 0, 2, xy, 1, 2, xy.length / 2);
            }
        }
    }

    public void addStroke(float[] x, float[] y) {
        if (mShapeDocument != null) {
            mShapeDocument.addStroke(x, y);
        }
    }

    public void clearStrokes() {
        if (mShapeDocument != null)
            mShapeDocument.clear();
    }

    public void setShapeDocument(ShapeDocument shapeDocument) {
        mShapeDocument.dispose();
        try {
            mShapeDocument = (ShapeDocument) shapeDocument.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ShapeDocument getShapeDocument() {
        return mShapeDocument;
    }

    public ShapeDocument getResultShapeDocument() {
        try {
            System.out.println("getResultShapeDocument start");
            mShapeRecognizer.process(mShapeDocument);
            mShapeBeautifier.process(mShapeDocument);
            System.out.println("getResultShapeDocument end");
            return mShapeDocument;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // return shape list, group by Integer
    public ArrayList<Object> getShapeAndStrok(ShapeDocument shapedocument) {
        System.out.println("getShapeList start");
        final int segmentCount = shapedocument.getSegmentCount();
        ArrayList<Object> shapeList = new ArrayList<Object>();

        final NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);

        for (int i = 0; i < segmentCount; ++i) {
            final ShapeSegment segment = shapedocument.getSegmentAt(i);
            final int candidateCount = segment.getCandidateCount();

            int inkRangeCount = segment.getInkRangeCount();

            if (candidateCount != 0) {
                final ShapeCandidate candidate = segment.getCandidateAt(0);

                if (candidate instanceof ShapeRecognized) {
                    final ShapeModel model = ((ShapeRecognized) candidate)
                            .getModel();

                    final float rs = ((ShapeRecognized) candidate)
                            .getResemblanceScore();
                    final float nrs = ((ShapeRecognized) candidate)
                            .getNormalizedRecognitionScore();

                    nf.setMinimumFractionDigits(4);
                    nf.setMaximumFractionDigits(4);
                    final int primitiveCount = ((ShapeRecognized) candidate)
                            .getPrimitiveCount();

                    nf.setMinimumFractionDigits(2);
                    nf.setMaximumFractionDigits(2);

                    for (int j = 0; j < primitiveCount; ++j) {
                        if (j == 0) {
                            Integer groupPrimitiveCount = primitiveCount;
                            shapeList.add(groupPrimitiveCount);
                        }

                        final ShapePrimitive primitive = ((ShapeRecognized) candidate)
                                .getPrimitiveAt(j);

                        if (primitive instanceof ShapeLine) {
                            final ShapeLineData data = ((ShapeLine) primitive)
                                    .getData();
                            shapeList.add(data);

                        } else if (primitive instanceof ShapeEllipticArc) {
                            final ShapeEllipticArcData data = ((ShapeEllipticArc) primitive)
                                    .getData();
                            shapeList.add(data);

                        } else if (primitive instanceof ShapeDecoratedLine) {
                            final ShapeDecoratedLineData data = ((ShapeDecoratedLine) primitive)
                                    .getData();
                            shapeList.add(data);
                        } else if (primitive instanceof ShapeDecoratedEllipticArc) {
                            final ShapeDecoratedEllipticArcData data = ((ShapeDecoratedEllipticArc) primitive)
                                    .getData();
                            shapeList.add(data);
                        }
                        primitive.dispose();
                    }
                    model.dispose();
                } else if (candidate instanceof ShapeScratchOut) {
                    System.out.println("   . segment " + i + ", scratch out");
                } else if (candidate instanceof ShapeErased) {
                    System.out.println("   . segment " + i + ", erased");
                } else if (candidate instanceof ShapeRejected) {
                    System.out
                            .println("   . segment " + i + ", Shape Rejected");
                }
                candidate.dispose();
            }
            segment.dispose();
        }
        System.out.println("getShapeList end");
        return shapeList;
    }

    public ArrayList<Object> getShapeList(ShapeDocument shapedocument) {
        System.out.println("getShapeList start");
        final int segmentCount = shapedocument.getSegmentCount();
        ArrayList<Object> shapeList = new ArrayList<Object>();

        final NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);

        for (int i = 0; i < segmentCount; ++i) {
            final ShapeSegment segment = shapedocument.getSegmentAt(i);
            final int candidateCount = segment.getCandidateCount();

            int count = segment.getInkRangeCount();
            if (count > 0) {
                ShapeInkRange sir = segment.getInkRangeAt(0);
                float start = sir.getFirstPoint();
                float end = sir.getLastPoint();
                int icount = sir.getStroke();
            }

            if (candidateCount != 0) {
                final ShapeCandidate candidate = segment.getCandidateAt(0);
                if (candidate instanceof ShapeRecognized) {
                    final ShapeModel model = ((ShapeRecognized) candidate)
                            .getModel();

                    final float rs = ((ShapeRecognized) candidate)
                            .getResemblanceScore();
                    final float nrs = ((ShapeRecognized) candidate)
                            .getNormalizedRecognitionScore();

                    nf.setMinimumFractionDigits(4);
                    nf.setMaximumFractionDigits(4);

                    final int primitiveCount = ((ShapeRecognized) candidate)
                            .getPrimitiveCount();

                    nf.setMinimumFractionDigits(2);
                    nf.setMaximumFractionDigits(2);
                    for (int j = 0; j < primitiveCount; ++j) {
                        final ShapePrimitive primitive = ((ShapeRecognized) candidate)
                                .getPrimitiveAt(j);

                        if (primitive instanceof ShapeLine) {
                            final ShapeLineData data = ((ShapeLine) primitive)
                                    .getData();
                            shapeList.add(data);

                        } else if (primitive instanceof ShapeEllipticArc) {
                            final ShapeEllipticArcData data = ((ShapeEllipticArc) primitive)
                                    .getData();
                            shapeList.add(data);

                        } else if (primitive instanceof ShapeDecoratedLine) {
                            final ShapeDecoratedLineData data = ((ShapeDecoratedLine) primitive)
                                    .getData();
                            shapeList.add(data);
                        } else if (primitive instanceof ShapeDecoratedEllipticArc) {
                            final ShapeDecoratedEllipticArcData data = ((ShapeDecoratedEllipticArc) primitive)
                                    .getData();
                            shapeList.add(data);
                        }
                        primitive.dispose();
                    }
                    model.dispose();
                } else if (candidate instanceof ShapeScratchOut) {
                    System.out.println("   . segment " + i + ", scratch out");
                } else if (candidate instanceof ShapeErased) {
                    System.out.println("   . segment " + i + ", erased");
                } else if (candidate instanceof ShapeRejected) {
                    System.out
                            .println("   . segment " + i + ", Shape Rejected");
                }
                candidate.dispose();
            }
            segment.dispose();
        }
        System.out.println("getShapeList end");
        return shapeList;
    }

}
