package com.jzap.chordrecognizer_r;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
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

    private MainActivity mMainActivity;

    public NotesGraphView (android.content.Context context) {
        super(context);

        mMainActivity = (MainActivity) context;

        mEndRunnable = false;
        setZOrderOnTop(false);

        setBackgroundColor(Color.GREEN);

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

        drawBackground(true);

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

                        if (mPCP != null) {
                            drawBackground(false);
                            for (int i = 0; i < mPCP.length; i++) {
                                if(mAudioAnalysis.getVolumeThresholdMet()) {
                                    if(oneOfThreeMostIntenseNotes(i)) {
                                       // drawThreeMostIntenseNotes(i);
                                        drawThreeMostIntenseNotesTriangle(i);
                                    }
                                    else {
                                        // drawNotesVolumeThresholdMet(i);
                                        drawVolumeThresholdMetTriangle(i);
                                    }
                                } else {
                                    // drawNotesVolumeThresholdNotMet(i);
                                    drawVolumeThresholdNotMetTriangle(i);
                                }
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


    // TODO : Figure out the math for this
    private void drawThreeMostIntenseNotesTriangle(int i) {
        //Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        WindowManager window = (WindowManager) mMainActivity.getSystemService(Context.WINDOW_SERVICE);
        Display display = window.getDefaultDisplay();

        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        int[] origin11 = new int[2];
        int[] origin2 = new int[2];


        // TODO : Make this common method
        mMainActivity.findViewById(R.id.rl_main).getLocationInWindow(origin11);
        mMainActivity.getmIv_button().getLocationInWindow(origin2);
        int halfViewLength = mMainActivity.getmIv_button().getHeight()/2;
        int halfViewWidth = mMainActivity.getmIv_button().getWidth()/2;

        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.GREEN);
        backgroundPaint.setStyle(Paint.Style.FILL);

       // mCanvas.drawRect(0 , 0 , outMetrics.widthPixels, outMetrics.heightPixels, backgroundPaint);

       // Log.i(TAG, "Window x = " + origin2[0] + ", Window y = " + origin2[1]);
        //Log.i(TAG, "Window x = " + origin11[0] + ", Window y = " + origin11[1]);

        Point origin = new Point(origin2[0] - origin11[0] + halfViewWidth, origin2[1]-origin11[1] + halfViewLength);

      //  Point origin = new Point(outMetrics.widthPixels/2, outMetrics.heightPixels/2);

        mPaint.setStrokeWidth(5);
        mPaint.setColor(mOPAQUE_DARK_COLORS[i % 5]);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setAntiAlias(true);

        // Vector2D side1 = new Vector2D(origin, scalePCPElement(mPCP[i] - 50), (360/12)*(i));
        // Vector2D side2 = new Vector2D(origin, scalePCPElement(mPCP[i] - 50), (360/12)*(i+1));

        Vector2D side1 = new Vector2D(origin, scalePCPElement(mPCP[i]), (360/12)*(i));
        Vector2D side2 = new Vector2D(origin, scalePCPElement(mPCP[i]), (360/12)*(i+1));


        Log.i(TAG,  "Angle " + String.valueOf(i) + " : " + String.valueOf((360/12)*(i)));
        Log.i(TAG,  "Angle " + String.valueOf(i+1) + " : " + String.valueOf((360/12)*(i+1)));

        Point point1_draw = new Point(origin.x, origin.y);
        Point point2_draw = new Point(side1.getEndPoint().x, side1.getEndPoint().y);
        Point point3_draw = new Point(side2.getEndPoint().x, side2.getEndPoint().y);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(point1_draw.x,point1_draw.y);
        path.lineTo(point2_draw.x,point2_draw.y);
        path.lineTo(point3_draw.x,point3_draw.y);
        path.lineTo(point1_draw.x,point1_draw.y);
        path.close();

        mCanvas.drawPath(path, mPaint);
    }

    private void drawVolumeThresholdMetTriangle(int i) {
        WindowManager window = (WindowManager) mMainActivity.getSystemService(Context.WINDOW_SERVICE);
        Display display = window.getDefaultDisplay();

        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        int[] origin11 = new int[2];
        int[] origin2 = new int[2];

        mMainActivity.findViewById(R.id.rl_main).getLocationInWindow(origin11);
        mMainActivity.getmIv_button().getLocationInWindow(origin2);
        int halfViewLength = mMainActivity.getmIv_button().getHeight()/2;
        int halfViewWidth = mMainActivity.getmIv_button().getWidth()/2;

        Log.i(TAG, "Window x = " + origin2[0] + ", Window y = " + origin2[1]);
        Log.i(TAG, "Window x = " + origin11[0] + ", Window y = " + origin11[1]);

        Point origin = new Point(origin2[0] - origin11[0] + halfViewWidth, origin2[1]-origin11[1] + halfViewLength);

        //  Point origin = new Point(outMetrics.widthPixels/2, outMetrics.heightPixels/2);

        mPaint.setStrokeWidth(5);
        mPaint.setColor(mTRANSLUCENT_DARK_COLORS[i % 5]);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setAntiAlias(true);

        // Vector2D side1 = new Vector2D(origin, scalePCPElement(mPCP[i] - 50), (360/12)*(i));
        // Vector2D side2 = new Vector2D(origin, scalePCPElement(mPCP[i] - 50), (360/12)*(i+1));

        Vector2D side1 = new Vector2D(origin, scalePCPElement(mPCP[i]), (360/12)*(i));
        Vector2D side2 = new Vector2D(origin, scalePCPElement(mPCP[i]), (360/12)*(i+1));

        Log.i(TAG,  "Angle " + String.valueOf(i) + " : " + String.valueOf((360/12)*(i)));
        Log.i(TAG,  "Angle " + String.valueOf(i+1) + " : " + String.valueOf((360/12)*(i+1)));

        Point point1_draw = new Point(origin.x, origin.y);
        Point point2_draw = new Point(side1.getEndPoint().x, side1.getEndPoint().y);
        Point point3_draw = new Point(side2.getEndPoint().x, side2.getEndPoint().y);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(point1_draw.x,point1_draw.y);
        path.lineTo(point2_draw.x,point2_draw.y);
        path.lineTo(point3_draw.x,point3_draw.y);
        path.lineTo(point1_draw.x,point1_draw.y);
        path.close();

        mCanvas.drawPath(path, mPaint);
    }

    private void drawVolumeThresholdNotMetTriangle(int i) {

        WindowManager window = (WindowManager) mMainActivity.getSystemService(Context.WINDOW_SERVICE);
        Display display = window.getDefaultDisplay();

        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        int[] origin11 = new int[2];
        int[] origin2 = new int[2];

        mMainActivity.findViewById(R.id.rl_main).getLocationInWindow(origin11);
        mMainActivity.getmIv_button().getLocationInWindow(origin2);
        int halfViewLength = mMainActivity.getmIv_button().getHeight()/2;
        int halfViewWidth = mMainActivity.getmIv_button().getWidth()/2;

        // TODO : Make common method
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.GREEN);
        backgroundPaint.setStyle(Paint.Style.FILL);

       // mCanvas.drawRect(0 , 0 , outMetrics.widthPixels, outMetrics.heightPixels, backgroundPaint);

        Log.i(TAG, "Window x = " + origin2[0] + ", Window y = " + origin2[1]);
        Log.i(TAG, "Window x = " + origin11[0] + ", Window y = " + origin11[1]);

        mPaint.setColor(Color.BLACK);
        Point origin = new Point(origin2[0] - origin11[0] + halfViewWidth, origin2[1]-origin11[1] + halfViewLength);
        //mCanvas.drawText("A", origin2[0] - origin11[0] + halfViewWidth, origin2[1]-origin11[1] + halfViewLength, mPaint);

        //  Point origin = new Point(outMetrics.widthPixels/2, outMetrics.heightPixels/2);

        mPaint.setStrokeWidth(5);
        mPaint.setColor(mOPAQUE_LIGHT_COLORS[i % 5]);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setAntiAlias(true);

        // Vector2D side1 = new Vector2D(origin, scalePCPElement(mPCP[i] - 50), (360/12)*(i));
        // Vector2D side2 = new Vector2D(origin, scalePCPElement(mPCP[i] - 50), (360/12)*(i+1));

        Vector2D side1 = new Vector2D(origin, scalePCPElement(mPCP[i]), (360/12)*(i));
        Vector2D side2 = new Vector2D(origin, scalePCPElement(mPCP[i]), (360/12)*(i+1));

        Log.i(TAG, "Angle " + String.valueOf(i) + " : " + String.valueOf((360 / 12) * (i)));
        Log.i(TAG, "Angle " + String.valueOf(i + 1) + " : " + String.valueOf((360 / 12) * (i + 1)));

        Point point1_draw = new Point(origin.x, origin.y);
        Point point2_draw = new Point(side1.getEndPoint().x, side1.getEndPoint().y);
        Point point3_draw = new Point(side2.getEndPoint().x, side2.getEndPoint().y);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(point1_draw.x,point1_draw.y);
        path.lineTo(point2_draw.x,point2_draw.y);

        // TODO : Make these arcs
        path.lineTo(point3_draw.x, point3_draw.y);
        path.lineTo(point1_draw.x,point1_draw.y);

        path.close();

        mCanvas.drawPath(path, mPaint);
    }


    private void drawThreeMostIntenseNotes(int i) {
        mPaint.setColor(mOPAQUE_DARK_COLORS[i % 5]);
        // Log.i(TAG, "Drawing Line : " + (float) scalePCPElement(mPCP[i]));
        mCanvas.drawLine(mCanvasPortion * i + 50, (float) scalePCPElement(mPCP[i]), mCanvasPortion * i + 50, mCanvasHeight, mPaint);

        // Experiment
        mCanvas.drawRect((mCanvasPortion * i), mCanvasHeight, (mCanvasPortion * i + mCanvasPortion), 0, mPaint);
        // End Experiment

        // Log.i(TAG, "Line Drawn");
        mPaint.setColor(mOPAQUE_LIGHT_COLORS[i % 5]);
        mCanvas.drawCircle(mCanvasPortion * i + 50, (float) (scalePCPElement(mPCP[i]) - 50), 80, mPaint); //radius was 35
        mCanvas.drawText(ProcessAudio.indexToNote(i), mCanvasPortion * i + 65, mCanvasHeight, mPaint);
        mPaint.setColor(mOPAQUE_DARK_COLORS[i % 5]);
        mCanvas.drawText(ProcessAudio.indexToNote(i), mCanvasPortion * i + 75, (float) scalePCPElement(mPCP[i]), mPaint);
        mCanvas.drawCircle(mCanvasPortion * i + 50, (float) (scalePCPElement(mPCP[i]) - 50), 20, mPaint); // radius was 20
    }

    private void drawNotesVolumeThresholdMet(int i) {
        mPaint.setColor(mTRANSLUCENT_DARK_COLORS[i % 5]);
        // Log.i(TAG, "Drawing Line : " + (float) scalePCPElement(mPCP[i]));
        mCanvas.drawLine(mCanvasPortion * i + 50, (float) scalePCPElement(mPCP[i]), mCanvasPortion * i + 50, mCanvasHeight, mPaint);


        // Experiment
        mCanvas.drawRect((mCanvasPortion * i), mCanvasHeight, (mCanvasPortion * i + mCanvasPortion), 0, mPaint);
        // End Experiment


        mCanvas.drawText(ProcessAudio.indexToNote(i), mCanvasPortion * i + 65, mCanvasHeight, mPaint);
        //  Log.i(TAG, "Line Drawn");
        mPaint.setColor(mTRANSLUCENT_LIGHT_COLORS[i % 5]);
        mCanvas.drawCircle(mCanvasPortion * i + 50, (float) (scalePCPElement(mPCP[i]) - 50), 80, mPaint); //radius was 35
        mPaint.setColor(mTRANSLUCENT_DARK_COLORS[i % 5]);
        mCanvas.drawCircle(mCanvasPortion * i + 50, (float) (scalePCPElement(mPCP[i]) - 50), 20, mPaint); // radius was 20

    }

    private void drawNotesVolumeThresholdNotMet(int i) {
        mPaint.setColor(getResources().getColor(R.color.GRAY));
        // Log.i(TAG, "Drawing Line : " + (float) scalePCPElement(mPCP[i]));
        mCanvas.drawLine(mCanvasPortion * i + 50, (float) scalePCPElement(mPCP[i]), mCanvasPortion * i + 50, mCanvasHeight, mPaint);

        // Experiment
        mPaint.setColor(mOPAQUE_DARK_COLORS[i % 5]);
        mCanvas.drawRect((mCanvasPortion * i), mCanvasHeight, (mCanvasPortion * i + mCanvasPortion), 0, mPaint);
        // End Experiment

        mCanvas.drawText(ProcessAudio.indexToNote(i), mCanvasPortion * i + 65, mCanvasHeight, mPaint);
        // Log.i(TAG, "Line Drawn");
        mPaint.setColor(getResources().getColor(R.color.LIGHTGRAY));
        mCanvas.drawCircle(mCanvasPortion * i + 50, (float) (scalePCPElement(mPCP[i]) - 50), 80, mPaint); //radius was 35
        mPaint.setColor(getResources().getColor(R.color.GRAY));
        mCanvas.drawCircle(mCanvasPortion * i + 50, (float) (scalePCPElement(mPCP[i]) - 50), 20, mPaint); // radius was 20
    }

    private void drawBackground(boolean creatingThread) {

        if (creatingThread) {
            mCanvas = mSurfaceHolder.lockCanvas();
        }

        WindowManager window = (WindowManager) mMainActivity.getSystemService(Context.WINDOW_SERVICE);
        Display display = window.getDefaultDisplay();

        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        int[] origin11 = new int[2];
        int[] origin2 = new int[2];

        // TODO : Make this common method
        mMainActivity.findViewById(R.id.rl_main).getLocationInWindow(origin11);
        mMainActivity.getmIv_button().getLocationInWindow(origin2);
        int halfViewLength = mMainActivity.getmIv_button().getHeight()/2;
        int halfViewWidth = mMainActivity.getmIv_button().getWidth()/2;

        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.GREEN);
        backgroundPaint.setStyle(Paint.Style.FILL);

        mCanvas.drawRect(0 , 0 , outMetrics.widthPixels, outMetrics.heightPixels, backgroundPaint);
        if(creatingThread) {
            setBackgroundColor(Color.TRANSPARENT);
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    // TODO : Make this dynamic to screen size and distance to boarder
    double scalePCPElement(double elem) {
        // TODO : Put all this dupllicate code in common methods/variables
        WindowManager window = (WindowManager) mMainActivity.getSystemService(Context.WINDOW_SERVICE);
        Display display = window.getDefaultDisplay();

        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        Log.i("scalePCP", "elem = " + String.valueOf(elem));

        int halfViewWidth = mMainActivity.getmIv_button().getWidth()/2;
        Log.i("scalePCP", "halfViewWidth = " + String.valueOf(halfViewWidth));
        int viewBuffer = 20;
        int scaleMin = halfViewWidth + viewBuffer;
        Log.i("scalePCP", "scaleMin = " + String.valueOf(scaleMin));
        int scaleMax = outMetrics.widthPixels/2;
        Log.i("scalePCP", "scaleMax = " + String.valueOf(scaleMax));
        int scaleRange = scaleMax - scaleMin;
        Log.i("scalePCP", "scaleRange = " +  String.valueOf(scaleRange));
        double aboveScaleMin = elem * scaleRange;
        Log.i("scalePCP", "aboveScaleMin = " + String.valueOf(aboveScaleMin));
        Log.i("scalePCP", "length = " +  String.valueOf(scaleMin + aboveScaleMin));
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
