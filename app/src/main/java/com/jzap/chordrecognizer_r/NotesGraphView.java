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
    private boolean mEndRunnable;

    // TODO : Reformat these names

    public static final int[] colors = {0xFF33b5e6, 0xFFaa66cd, 0xFFffbb34, 0xFF98cb00, 0xFFff4443};
    public static final int[] colors2 = {0x1F33b5e6, 0x1Faa66cd, 0x1Fffbb34, 0x1F98cb00, 0x1Fff4443};

    public static final int[] colors3 = {0xFFededed, 0xFFededed, 0xFFededed, 0xFFededed, 0xFFededed};
    public static final int[] colors4 = {0x3Fededed, 0x3Fededed, 0x3Fededed, 0x3Fededed, 0x3Fededed};

    // TODO : Replace this with AudioAnalysis
    private double[] mPCP;
    private AudioAnalysis mAudioAnalysis;


    SurfaceHolder mSurfaceHolder;

    // TODO : Make variable dynamic to screen size
    int virtualCanvasOriginY = 900;
    int virtualCanvasMaxHeight;

    public NotesGraphView (android.content.Context context) {
        super(context);
        mEndRunnable = false;
        setZOrderOnTop(true);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
    }

    synchronized public void setmPCP(double[] PCP) {
        mPCP = PCP;
    }

    synchronized public void setmAudioAnalysis(AudioAnalysis audioAnalysis) {
        mAudioAnalysis = audioAnalysis;
        setmPCP(mAudioAnalysis.getPCP());
    }

// SurfaceHolder.Callback implementations

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.i(TAG, "surfaceCreated");

        // TODO : Consider killing thread when record button is shut off, and restarting when turned back on (May save battery, may be good practice...)
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!mEndRunnable) {
                    // Log.i(TAG, "Running...");
                    if (ProcessAudio.getmNewPCP()) {

                       // Log.i(TAG, "New PCP");
                        Canvas canvas = null;

                        Paint p = new Paint();
                        p.setStrokeWidth(10);

                        canvas = mSurfaceHolder.lockCanvas();

                        // TODO : get all of this in the constructor, and use member variables

                        int canvasWidth = canvas.getWidth();
                        int canvasHeight = canvas.getHeight();
                        int canvasPortion = canvasWidth/12;

                        virtualCanvasMaxHeight = canvasHeight - virtualCanvasOriginY;

                        //cleanPCP();

                        canvas.drawColor(0, PorterDuff.Mode.CLEAR);

                        for (int i = 0; i < mPCP.length; i++) {
                            if(mAudioAnalysis.getVolumeThresholdMet() && oneOfThreeMostIntenseNotes(i)) {
                                p.setColor(colors[i % 5]);
                               // Log.i(TAG, "Drawing Line : " + (float) scalePCPElement(mPCP[i]));
                                canvas.drawLine(canvasPortion * i + 50, (float) scalePCPElement(mPCP[i]), canvasPortion * i + 50, canvasHeight, p);
                               // Log.i(TAG, "Line Drawn");
                                p.setColor(colors2[i % 5]);
                                canvas.drawCircle(canvasPortion * i + 50, (float) (scalePCPElement(mPCP[i]) - 50), 35, p);
                                p.setColor(colors[i % 5]);
                                canvas.drawCircle(canvasPortion * i + 50, (float) (scalePCPElement(mPCP[i]) - 50), 20, p);
                            } else {
                                p.setColor(colors3[i % 5]);
                               // Log.i(TAG, "Drawing Line : " + (float) scalePCPElement(mPCP[i]));
                                canvas.drawLine(canvasPortion * i + 50, (float) scalePCPElement(mPCP[i]), canvasPortion * i + 50, canvasHeight, p);
                                // canvas.drawLine(canvasPortion*i+50, 900, canvasPortion*i+50, canvasHeight, p);
                              //  Log.i(TAG, "Line Drawn");
                                p.setColor(colors4[i % 5]);
                                canvas.drawCircle(canvasPortion * i + 50, (float) (scalePCPElement(mPCP[i]) - 50), 35, p);
                                p.setColor(colors3[i % 5]);
                                canvas.drawCircle(canvasPortion * i + 50, (float) (scalePCPElement(mPCP[i]) - 50), 20, p);
                            }
                        }
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                        ProcessAudio.setmNewPCP(false);
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

    double scalePCPElement(double elem) {
        double distanceFromOriginY = (1 - elem) * virtualCanvasMaxHeight;
        if(distanceFromOriginY <= 0 ) {
            Log.i("TAG", "Negative distance from Y");
        }
        double d = virtualCanvasOriginY + distanceFromOriginY;
        return d;
    }

    // TODO : Get rid of this, but first figure out if I need to reimplement something similar

    public void cleanPCP() {
        for(int i = 0; i < mPCP.length; i++) {
            if(mPCP[i] > RecordAudio.VOLUME_THRESHOLD) mPCP[i] = RecordAudio.VOLUME_THRESHOLD;
        }
    }

    public void setmEndRunnable(boolean endRunnable) {
        mEndRunnable = endRunnable;
    }

    private int[] indexesOfThreeMostIntenseNote() {
        int[] indexes = new int[3];
        indexes[0] = noteToIndex(mAudioAnalysis.getMostIntenseNote());
        indexes[1] = noteToIndex(mAudioAnalysis.getSeconMostIntenseNote());
        indexes[2] = noteToIndex(mAudioAnalysis.getThirdMostIntenseNote());
        return indexes;
    }

    private boolean oneOfThreeMostIntenseNotes(int i) {
        int[] indexes = indexesOfThreeMostIntenseNote();
        for (int j : indexes) {
            if(i == j) return true;
        }
        return false;
    }

    private int noteToIndex(String note) {
        if(note.equalsIgnoreCase("C")) {
            return 0;
        } else if (note.equalsIgnoreCase("C#")) {
            return 1;
        } else if (note.equalsIgnoreCase("D")) {
            return 2;
        } else if (note.equalsIgnoreCase("D#")) {
            return 3;
        } else if (note.equalsIgnoreCase("E")) {
            return 4;
        } else if (note.equalsIgnoreCase("F")) {
            return 5;
        } else if (note.equalsIgnoreCase("F#")) {
            return 6;
        } else if (note.equalsIgnoreCase("G")) {
            return 7;
        } else if (note.equalsIgnoreCase("G#")) {
            return 8;
        } else if (note.equalsIgnoreCase("A")) {
            return 9;
        } else if (note.equalsIgnoreCase("A#")) {
            return 10;
        } else if (note.equalsIgnoreCase("B")) {
            return 11;
        }

        return -1;
    }
}
