package com.jzap.chordrecognizer_r;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

/**
 * Created by Justin on 12/6/2014.
 */
public class NotesGraphView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "NotesGraphView";
    private static final int BACKGROUND_COLOR = 0xFF33b5e6;
    private static int mSpin;

    private boolean mEndRunnable;

    private AudioAnalysis mAudioAnalysis;
    private double[] mPCP;

    private SurfaceHolder mSurfaceHolder;

    public static final int[] mOPAQUE_DARK_COLORS = new int[5];
    public static final int[] mOPAQUE_LIGHT_COLORS = new int[5];

    public static final int[] mTRANSLUCENT_DARK_COLORS = new int[5];
    public static final int[] mTRANSLUCENT_LIGHT_COLORS = new int[5];

    private Paint mPaint;

    private Canvas mCanvas;

    private Point mButtonCenter;
    private DisplayMetrics mDisplayMetrics;
    private int mHalfButtonWidth;
    private int mHalfButtonHeight;
    int[] mButtonOrigin = new int[2];

    private MainActivity mMainActivity;

    public NotesGraphView (android.content.Context context) {
        super(context);

        mMainActivity = (MainActivity) context;

        mSpin = 0;

        mEndRunnable = false;
        setZOrderOnTop(false);

        setBackgroundColor(BACKGROUND_COLOR);

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);

        mOPAQUE_DARK_COLORS[0] = getResources().getColor(R.color.O_BLUE);
        mOPAQUE_DARK_COLORS[1] = getResources().getColor(R.color.O_PURPLE);
        mOPAQUE_DARK_COLORS[2] = getResources().getColor(R.color.O_GREEN);
        mOPAQUE_DARK_COLORS[3] = getResources().getColor(R.color.O_ORANGE);
        mOPAQUE_DARK_COLORS[4] = getResources().getColor(R.color.O_RED);

        mOPAQUE_LIGHT_COLORS[0] = getResources().getColor(R.color.O_LIGHTBLUE);
        mOPAQUE_LIGHT_COLORS[1] = getResources().getColor(R.color.O_LIGHTPURPLE);
        mOPAQUE_LIGHT_COLORS[2] = getResources().getColor(R.color.O_LIGHTGREEN);
        mOPAQUE_LIGHT_COLORS[3] = getResources().getColor(R.color.O_LIGHTORANGE);
        mOPAQUE_LIGHT_COLORS[4] = getResources().getColor(R.color.O_LIGHTRED);

        mTRANSLUCENT_DARK_COLORS[0] = getResources().getColor(R.color.T_BLUE);
        mTRANSLUCENT_DARK_COLORS[1] = getResources().getColor(R.color.T_PURPLE);
        mTRANSLUCENT_DARK_COLORS[2] = getResources().getColor(R.color.T_GREEN);
        mTRANSLUCENT_DARK_COLORS[3] = getResources().getColor(R.color.T_ORANGE);
        mTRANSLUCENT_DARK_COLORS[4] = getResources().getColor(R.color.T_RED);

        mTRANSLUCENT_LIGHT_COLORS[0] = getResources().getColor(R.color.T_LIGHTBLUE);
        mTRANSLUCENT_LIGHT_COLORS[1] = getResources().getColor(R.color.T_LIGHTPURPLE);
        mTRANSLUCENT_LIGHT_COLORS[2] = getResources().getColor(R.color.T_LIGHTGREEN);
        mTRANSLUCENT_LIGHT_COLORS[3] = getResources().getColor(R.color.T_LIGHTORANGE);
        mTRANSLUCENT_LIGHT_COLORS[4] = getResources().getColor(R.color.T_LIGHTRED);

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

        setScreenMeasurements();
        drawBackground(true);

        // TODO : Consider killing thread when record button is shut off, and restarting when turned back on (May save battery, may be good practice...)
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!mEndRunnable) {
                    // Log.i(TAG, "Running...");
                    if (ProcessAudio.getmNewPCP()) {
                        mCanvas = null;
                        mCanvas = mSurfaceHolder.lockCanvas();
                        mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
                        if (mPCP != null) {
                            setScreenMeasurements();
                            drawBackground(false);
                            for (int i = 0; i < mPCP.length; i++) {
                                if(mAudioAnalysis.getVolumeThresholdMet()) {
                                    if(oneOfThreeMostIntenseNotes(i)) {
                                        drawTriangles(i, true, true);
                                    }
                                    else {
                                        drawTriangles(i, true, false);
                                    }
                                } else {
                                    drawTriangles(i, false, false);
                                }
                            }
                        }
                        mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                        ProcessAudio.setmNewPCP(false);
                        mSpin = mSpin + 10;
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



    private void drawTriangles(int i, boolean volumeThresholdMet, boolean oneOfThreeMostIntenseNotes) {
        mPaint.setStrokeWidth(5);
        if (volumeThresholdMet) {
            if (oneOfThreeMostIntenseNotes) {
                mPaint.setColor(mOPAQUE_DARK_COLORS[i % 5]);
            } else {
                mPaint.setColor(mTRANSLUCENT_DARK_COLORS[i % 5]);
            }
        } else {
            // TODO : You know what to do
            mPaint.setColor(0xAAFFFFFF);
        }
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setAntiAlias(true);

        int startAngle = (360/12)*(i) + mSpin;
        int endAngle = (360/12)*(i+1) + mSpin;
        //int sweepAngle = 270; looks pretty cool
        int sweepAngle = 30;

        Log.i(TAG, "startAngle1 = " + startAngle);

        float length = (float) scalePCPElement(mPCP[i]);

        Vector2D side1 = new Vector2D(mButtonCenter, length, startAngle);
        Vector2D side2 = new Vector2D(mButtonCenter, length, endAngle);

        Vector2D circumscribedRectLeftCorner = new Vector2D(mButtonCenter, length * (Math.sqrt(2.0)), 225);
        Vector2D circumscribedRectRightCorner = new Vector2D(mButtonCenter, length * (Math.sqrt(2.0)), 45);

        Point point1_draw = new Point(mButtonCenter.x, mButtonCenter.y);
        Point point2_draw = new Point(side1.getEndPoint().x, side1.getEndPoint().y);
        Point point3_draw = new Point(side2.getEndPoint().x, side2.getEndPoint().y);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(point1_draw.x,point1_draw.y);
        path.lineTo(point2_draw.x,point2_draw.y);

        //startAngle = (int) (180 / Math.PI * Math.atan2(point2_draw.y - point1_draw.y, point2_draw.x - point1_draw.x));
        Log.i(TAG, "startAngle2 = " + startAngle);
       // RectF rect = new RectF(mButtonCenter.x + length, mButtonCenter.y + length, mButtonCenter.x - length, mButtonCenter.y - length);
        RectF rect = new RectF(circumscribedRectLeftCorner.getEndPoint().x, circumscribedRectLeftCorner.getEndPoint().y, circumscribedRectRightCorner.getEndPoint().x, circumscribedRectRightCorner.getEndPoint().y);
       // RectF rect = new RectF(point2_draw.x, point2_draw.y, point3_draw.x, point3_draw.y);
      //  mPaint.setColor(0xEFFFFFFF);
       // mCanvas.drawRect(rect, mPaint);
        path.arcTo(rect, startAngle, sweepAngle);

       // path.lineTo(point3_draw.x,point3_draw.y);
        path.lineTo(point1_draw.x,point1_draw.y);
        path.close();

        mPaint.setColor(0xAAFFFFFF);

        mCanvas.drawPath(path, mPaint);
    }

    private void setScreenMeasurements() {
        WindowManager window = (WindowManager) mMainActivity.getSystemService(Context.WINDOW_SERVICE);
        Display display = window.getDefaultDisplay();

        mDisplayMetrics = new DisplayMetrics();
        display.getMetrics(mDisplayMetrics);

        int[] mainLayoutOrigin= new int[2];
        mButtonOrigin = new int[2];

        mMainActivity.findViewById(R.id.rl_main).getLocationInWindow(mainLayoutOrigin);
        mMainActivity.getmIv_button().getLocationInWindow(mButtonOrigin);

        mHalfButtonWidth =  mMainActivity.getmIv_button().getWidth()/2;
        mHalfButtonHeight = mMainActivity.getmIv_button().getHeight()/2;

        mButtonCenter = new Point(mButtonOrigin[0] - mainLayoutOrigin[0] + mHalfButtonHeight, mButtonOrigin[1]-mainLayoutOrigin[1] + mHalfButtonWidth);
    }

    private void drawBackground(boolean creatingThread) {
        if (creatingThread) {
            mCanvas = mSurfaceHolder.lockCanvas();
        }

        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(BACKGROUND_COLOR);
        backgroundPaint.setStyle(Paint.Style.FILL);

        mCanvas.drawRect(0, 0, mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels, backgroundPaint);
        if(creatingThread) {
            setBackgroundColor(Color.TRANSPARENT);
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    double scalePCPElement(double elem) {
        int viewBuffer = 20;
        int scaleMin = mHalfButtonWidth + viewBuffer;
        int scaleMax = mDisplayMetrics.widthPixels/2;
        int scaleRange = scaleMax - scaleMin;
        double aboveScaleMin = elem * scaleRange;
        return scaleMin + aboveScaleMin;
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
