package com.jacky.commondraw.visual.brush;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.jacky.commondraw.model.InsertableObjectBase;
import com.jacky.commondraw.views.doodleview.IInternalDoodle;
import com.jacky.commondraw.visual.VisualStrokeSpot;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 * 钢笔。
 */
public class VisualStrokePen extends VisualStrokeSpot {

    public VisualStrokePen(Context context, IInternalDoodle internalDoodle,
                           InsertableObjectBase object) {
        super(context, internalDoodle, object);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void drawLine(Canvas canvas, double x0,double y0,double w0,double x1,double y1,double w1,Paint paint){
        double curDis = Math.hypot(x0-x1, y0-y1);
        int steps = 1;
        if(paint.getStrokeWidth() < 6){
            steps = 1+(int)(curDis/2);
        }else if(paint.getStrokeWidth() > 60){
            steps = 1+(int)(curDis/4);
        }else{
            steps = 1+(int)(curDis/3);
        }
        double deltaX=(x1-x0)/steps;
        double deltaY=(y1-y0)/steps;
        double deltaW=(w1-w0)/steps;
        double x=x0;
        double y=y0;
        double w=w0;

        for(int i=0;i<steps;i++){
            RectF oval = new RectF();
            oval.set((float)(x-w/4.0f), (float)(y-w/2.0f), (float)(x+w/4.0f), (float)(y+w/2.0f));
            canvas.drawOval(oval, paint);

            x+=deltaX;
            y+=deltaY;
            w+=deltaW;
        }
    }

}

