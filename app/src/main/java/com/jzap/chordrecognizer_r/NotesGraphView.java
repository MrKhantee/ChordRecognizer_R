package com.jzap.chordrecognizer_r;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Justin on 12/6/2014.
 */
public class NotesGraphView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "NotesGraphView";

    SurfaceHolder mSurfaceHolder;

    public NotesGraphView (android.content.Context context) {
        super(context);
        setZOrderOnTop(true);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
    }



// SurfaceHolder.Callback implementations
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.i(TAG, "surfaceCreated");
        //--
        new Thread(new Runnable() {
            @Override
            public void run() {

                Canvas canvas = null;

                Paint p = new Paint();
                p.setStrokeWidth(10);



                float rand;

                int[] colors = {0xFF33b5e6, 0xFFaa66cd, 0xFFffbb34, 0xFF98cb00, 0xFFff4443};
                int[] colors2 = {0x1F33b5e6, 0x1Faa66cd, 0x1Fffbb34, 0x1F98cb00, 0x1Fff4443};



                for (int j = 0; j < 500; j++) {
                    canvas = mSurfaceHolder.lockCanvas();

                    int canvasWidth = canvas.getWidth();
                    int canvasHeight = canvas.getHeight();
                    int canvasPortion = canvasWidth/12;
                    canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                   // mSurfaceHolder.unlockCanvasAndPost(canvas);
                    //mSurfaceHolder.lockCanvas();
                    for (int i = 0; i < 12; i++) {
                        p.setColor(colors[i%5]);
                        rand = (float) Math.random()*900;
                        canvas.drawLine(canvasPortion*i+50,(900 + rand), canvasPortion*i+50, canvasHeight, p);
                        p.setColor(colors2[i%5]);
                        canvas.drawCircle(canvasPortion*i+50, (850 + rand), 35, p);
                        p.setColor(colors[i%5]);
                        canvas.drawCircle(canvasPortion*i+50, (850 + rand), 20, p);
                    }
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        //--
        Log.i(TAG, "DONE");
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
// End SurfaceHolder.Callback implementations



}
