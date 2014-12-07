package com.jzap.chordrecognizer_r;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

/**
 * Created by Justin on 11/30/2014.
 */
public class GraphView extends View {

    private static final String TAG = "GraphView";

// Constructors
    public GraphView(android.content.Context context) {
        super(context);
        Log.i(TAG, "GraphView created");
    }
// End Constructors

    @Override
    protected void onDraw(android.graphics.Canvas canvas) {
        super.onDraw(canvas);
        Log.i(TAG, "onDraw()");
        Paint p = new Paint();
        p.setColor(0x0F1798fe);
        p.setStrokeWidth(10);
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        int canvasPortion = canvasWidth/12;

        int[] colors = {0xFF33b5e6, 0xFFaa66cd, 0xFFffbb34, 0xFF98cb00, 0xFFff4443};
        int[] colors2 = {0x1F33b5e6, 0x1Faa66cd, 0x1Fffbb34, 0x1F98cb00, 0x1Fff4443};

        float rand;

        for (int i = 0; i < 12; i++) {
            p.setColor(colors[i%5]);
            rand = (float) Math.random()*300;
            canvas.drawLine(canvasPortion*i+50,(900 + rand), canvasPortion*i+50, canvasHeight, p);
            p.setColor(colors2[i%5]);
            canvas.drawCircle(canvasPortion*i+50, (850 + rand), 35, p);
            p.setColor(colors[i%5]);
            canvas.drawCircle(canvasPortion*i+50, (850 + rand), 20, p);
        }

    }



}
