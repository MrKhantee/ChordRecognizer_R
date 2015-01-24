package com.jzap.chordrecognizer_r;

import android.graphics.Canvas;
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

    private AudioAnalysis mAudioAnalysis;
    private double[] mPCP;

    private SurfaceHolder mSurfaceHolder;

    public static final int[] mOPAQUE_DARK_COLORS = new int[5];
    public static final int[] mOPAQUE_LIGHT_COLORS = new int[5];

    public static final int[] mTRANSLUCENT_DARK_COLORS = new int[5];
    public static final int[] mTRANSLUCENT_LIGHT_COLORS = new int[5];

    // TODO : Make variable dynamic to screen size
    private int mVirtualCanvasOriginY = 900;
    private int mVirtualCanvasMaxHeight;

    private Paint mPaint;

    private Canvas mCanvas;
    private int mCanvasWidth;
    private int mCanvasHeight;
    private int mCanvasPortion;

    public NotesGraphView (android.content.Context context) {
        super(context);

        mEndRunnable = false;
        setZOrderOnTop(true);

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);

        mOPAQUE_DARK_COLORS[0] = getResources().getColor(R.color.O_BLUE);
        mOPAQUE_DARK_COLORS[1] = getResources().getColor(R.color.O_PURPLE);
        mOPAQUE_DARK_COLORS[2] = getResources().getColor(R.color.O_ORANGE);
        mOPAQUE_DARK_COLORS[3] = getResources().getColor(R.color.O_RED);
        mOPAQUE_DARK_COLORS[4] = getResources().getColor(R.color.O_GREEN);

        mOPAQUE_LIGHT_COLORS[0] = getResources().getColor(R.color.O_LIGHTBLUE);
        mOPAQUE_LIGHT_COLORS[1] = getResources().getColor(R.color.O_LIGHTPURPLE);
        mOPAQUE_LIGHT_COLORS[2] = getResources().getColor(R.color.O_LIGHTORANGE);
        mOPAQUE_LIGHT_COLORS[3] = getResources().getColor(R.color.O_LIGHTRED);
        mOPAQUE_LIGHT_COLORS[4] = getResources().getColor(R.color.O_LIGHTGREEN);

        mTRANSLUCENT_DARK_COLORS[0] = getResources().getColor(R.color.T_BLUE);
        mTRANSLUCENT_DARK_COLORS[1] = getResources().getColor(R.color.T_PURPLE);
        mTRANSLUCENT_DARK_COLORS[2] = getResources().getColor(R.color.T_ORANGE);
        mTRANSLUCENT_DARK_COLORS[3] = getResources().getColor(R.color.T_RED);
        mTRANSLUCENT_DARK_COLORS[4] = getResources().getColor(R.color.T_GREEN);

        mTRANSLUCENT_LIGHT_COLORS[0] = getResources().getColor(R.color.T_LIGHTBLUE);
        mTRANSLUCENT_LIGHT_COLORS[1] = getResources().getColor(R.color.T_LIGHTPURPLE);
        mTRANSLUCENT_LIGHT_COLORS[2] = getResources().getColor(R.color.T_LIGHTORANGE);
        mTRANSLUCENT_LIGHT_COLORS[3] = getResources().getColor(R.color.T_LIGHTRED);
        mTRANSLUCENT_LIGHT_COLORS[4] = getResources().getColor(R.color.T_LIGHTGREEN);

        mPaint = new Paint();
        mPaint.setStrokeWidth(10);
        mPaint.setTextSize(60);
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


        // Consider killing thread when record button is shut off, and restarting when turned back on (May save battery, may be good practice...)
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!mEndRunnable) {
                    // Log.i(TAG, "Running...");
                    if (ProcessAudio.getmNewPCP()) {
                        mCanvas = null;
                        mCanvas = mSurfaceHolder.lockCanvas();

                        mCanvasWidth = mCanvas.getWidth();
                        mCanvasHeight = mCanvas.getHeight();
                        mCanvasPortion = mCanvasWidth/12;

                        mVirtualCanvasMaxHeight = mCanvasHeight - mVirtualCanvasOriginY;

                        mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);

                        // TODO : Fix bug where after hitting home button then reopening app, the app crashes here - java.lang.NullPointerException
                        for (int i = 0; i < mPCP.length; i++) {
                            if(mAudioAnalysis.getVolumeThresholdMet()) {
                                if(oneOfThreeMostIntenseNotes(i)) {
                                    mPaint.setColor(mOPAQUE_DARK_COLORS[i % 5]);
                                    // Log.i(TAG, "Drawing Line : " + (float) scalePCPElement(mPCP[i]));
                                    mCanvas.drawLine(mCanvasPortion * i + 50, (float) scalePCPElement(mPCP[i]), mCanvasPortion * i + 50, mCanvasHeight, mPaint);
                                    // Log.i(TAG, "Line Drawn");
                                    mPaint.setColor(mOPAQUE_LIGHT_COLORS[i % 5]);
                                    mCanvas.drawCircle(mCanvasPortion * i + 50, (float) (scalePCPElement(mPCP[i]) - 50), 35, mPaint);
                                    mCanvas.drawText(ProcessAudio.indexToNote(i), mCanvasPortion * i + 65, mCanvasHeight, mPaint);
                                    mPaint.setColor(mOPAQUE_DARK_COLORS[i % 5]);
                                    mCanvas.drawText(ProcessAudio.indexToNote(i), mCanvasPortion * i + 75, (float) scalePCPElement(mPCP[i]), mPaint);
                                    mCanvas.drawCircle(mCanvasPortion * i + 50, (float) (scalePCPElement(mPCP[i]) - 50), 20, mPaint);
                                }
                                else {
                                    mPaint.setColor(mTRANSLUCENT_DARK_COLORS[i % 5]);
                                    // Log.i(TAG, "Drawing Line : " + (float) scalePCPElement(mPCP[i]));
                                    mCanvas.drawLine(mCanvasPortion * i + 50, (float) scalePCPElement(mPCP[i]), mCanvasPortion * i + 50, mCanvasHeight, mPaint);
                                    mCanvas.drawText(ProcessAudio.indexToNote(i), mCanvasPortion * i + 65, mCanvasHeight, mPaint);
                                    //  Log.i(TAG, "Line Drawn");
                                    mPaint.setColor(mTRANSLUCENT_LIGHT_COLORS[i % 5]);
                                    mCanvas.drawCircle(mCanvasPortion * i + 50, (float) (scalePCPElement(mPCP[i]) - 50), 35, mPaint);
                                    mPaint.setColor(mTRANSLUCENT_DARK_COLORS[i % 5]);
                                    mCanvas.drawCircle(mCanvasPortion * i + 50, (float) (scalePCPElement(mPCP[i]) - 50), 20, mPaint);
                                }
                            } else {
                                mPaint.setColor(getResources().getColor(R.color.GRAY));
                                // Log.i(TAG, "Drawing Line : " + (float) scalePCPElement(mPCP[i]));
                                mCanvas.drawLine(mCanvasPortion * i + 50, (float) scalePCPElement(mPCP[i]), mCanvasPortion * i + 50, mCanvasHeight, mPaint);
                                mCanvas.drawText(ProcessAudio.indexToNote(i), mCanvasPortion * i + 65, mCanvasHeight, mPaint);
                                // Log.i(TAG, "Line Drawn");
                                mPaint.setColor(getResources().getColor(R.color.LIGHTGRAY));
                                mCanvas.drawCircle(mCanvasPortion * i + 50, (float) (scalePCPElement(mPCP[i]) - 50), 35, mPaint);
                                mPaint.setColor(getResources().getColor(R.color.GRAY));
                                mCanvas.drawCircle(mCanvasPortion * i + 50, (float) (scalePCPElement(mPCP[i]) - 50), 20, mPaint);
                            }
                        }
                        mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                        ProcessAudio.setmNewPCP(false);
                    }
                }
            }
        }).start();
        //--
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {}
// End SurfaceHolder.Callback implementations

    double scalePCPElement(double elem) {
        double distanceFromOriginY = (1 - elem) * mVirtualCanvasMaxHeight;
        if(distanceFromOriginY <= 0 ) {
            Log.i("TAG", "Negative distance from Y");
        }
        double d = mVirtualCanvasOriginY + distanceFromOriginY;
        return d;
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
