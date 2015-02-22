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
    private static final int SPIN_INCREMENT = 1;
    private static final double VOL_INC_SMOOTHING_FACTOR = 0.03; // was .008
    private static final double VOL_DEC_SMOOTHING_FACTOR = 0.008;
    private static int direction = -1;
    private static double mNormVolume = 0;
    private static double[] mNoteIntensities;



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

        mNoteIntensities = new double[12];
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
        mPaint.setStrokeWidth(0);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setAntiAlias(true);
    }

    synchronized public void setmPCP(double[] PCP) {
        mPCP = PCP;
    }

    synchronized public void setmAudioAnalysis(AudioAnalysis audioAnalysis) {
        mAudioAnalysis = audioAnalysis;
        setmPCP(mAudioAnalysis.getmPCP());
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
                            drawVolumeSector();
                            for (int i = 0; i < mPCP.length; i++) {
                                drawNoteSector(i);
                            }
                        }
                        mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                        mSpin = mSpin + SPIN_INCREMENT;
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

    private void drawNoteSector(int i) {
        mPaint.setColor(Color.WHITE);
        mPaint.setAlpha((int)(mAudioAnalysis.getmPCP()[i]*255));

        Path notePath = createSector(30, i);
        mCanvas.drawPath(notePath, mPaint);
    }

    private void drawVolumeSector() {
        mPaint.setColor(0x33FFFFFF);
        Path volumePath = createSector(90);
        Path volumePath2 = createSector(300);
        mCanvas.drawPath(volumePath, mPaint);
        mCanvas.drawPath(volumePath2, mPaint);
    }

    // Used for note sectors
    private Path createSector(int sweepAngle, int noteIndex) {
        int startAngle = (360/12)*(noteIndex) + mSpin;

        // TODO : efficiency
      //  float length = (float) scaleSectorLength(mPCP[noteIndex], scaleSectorLength(mAudioAnalysis.getNormMaxVolume(), 0));
        float length = (float) scaleSectorLength(smoothNote(noteIndex), scaleSectorLength(mAudioAnalysis.getNormMaxVolume(), 0));

        Vector2D sectorSideStart = new Vector2D(mButtonCenter, length, startAngle);

        Vector2D circumscribedRectLeftCorner = new Vector2D(mButtonCenter, length * (Math.sqrt(2.0)), 225);
        Vector2D circumscribedRectRightCorner = new Vector2D(mButtonCenter, length * (Math.sqrt(2.0)), 45);

        Point centerPoint = new Point(mButtonCenter.x, mButtonCenter.y);
        Point secondPoint = new Point(sectorSideStart.getEndPoint().x, sectorSideStart.getEndPoint().y);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(centerPoint.x,centerPoint.y);
        path.lineTo(secondPoint.x,secondPoint.y);

        RectF rect = new RectF(circumscribedRectLeftCorner.getEndPoint().x, circumscribedRectLeftCorner.getEndPoint().y, circumscribedRectRightCorner.getEndPoint().x, circumscribedRectRightCorner.getEndPoint().y);
        path.arcTo(rect, startAngle, sweepAngle);

        path.lineTo(centerPoint.x, centerPoint.y);
        path.close();

        return path;
    }

    // Used for volume sectors
    private Path createSector(int sweepAngle) {
        int startAngle = (mSpin * 5) * direction;
  
        direction = direction * -1;

        float length = (float) scaleSectorLength(smoothVolume(), 50);

        Vector2D sectorSideStart = new Vector2D(mButtonCenter, length, startAngle);

        Vector2D circumscribedRectLeftCorner = new Vector2D(mButtonCenter, length * (Math.sqrt(2.0)), 225);
        Vector2D circumscribedRectRightCorner = new Vector2D(mButtonCenter, length * (Math.sqrt(2.0)), 45);

        Point centerPoint = new Point(mButtonCenter.x, mButtonCenter.y);
        Point secondPoint = new Point(sectorSideStart.getEndPoint().x, sectorSideStart.getEndPoint().y);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(centerPoint.x,centerPoint.y);
        path.lineTo(secondPoint.x,secondPoint.y);

        RectF rect = new RectF(circumscribedRectLeftCorner.getEndPoint().x, circumscribedRectLeftCorner.getEndPoint().y, circumscribedRectRightCorner.getEndPoint().x, circumscribedRectRightCorner.getEndPoint().y);
        path.arcTo(rect, startAngle, sweepAngle);

        path.lineTo(centerPoint.x, centerPoint.y);
        path.close();

        return path;
    }

    /*
    private double smoothVolume() {
       // Log.i(TAG, "mNormVolume = " + String.valueOf(mNormVolume));
       // Log.i(TAG, "audioAnalysisVolume  = " + String.valueOf(mAudioAnalysis.getNormMaxVolume()));
        if(Math.abs(mNormVolume - mAudioAnalysis.getNormMaxVolume()) > VOL_SMOOTHING_FACTOR) {
            mNormVolume = ((mNormVolume > mAudioAnalysis.getNormMaxVolume()) ? (mNormVolume - VOL_SMOOTHING_FACTOR) : (mNormVolume + VOL_SMOOTHING_FACTOR));
        } else {
            mNormVolume = mAudioAnalysis.getNormMaxVolume();
        }
       // Log.i(TAG, String.valueOf(mNormVolume));
        return mNormVolume;
    }
*/
    private double smoothVolume() {
        // Log.i(TAG, "mNormVolume = " + String.valueOf(mNormVolume));
        // Log.i(TAG, "audioAnalysisVolume  = " + String.valueOf(mAudioAnalysis.getNormMaxVolume()));
        if(mNormVolume - mAudioAnalysis.getNormMaxVolume() > VOL_DEC_SMOOTHING_FACTOR) {
            mNormVolume = mNormVolume - VOL_DEC_SMOOTHING_FACTOR;
        } else if (mAudioAnalysis.getNormMaxVolume() - mNormVolume > VOL_INC_SMOOTHING_FACTOR) {
            mNormVolume = mNormVolume + VOL_INC_SMOOTHING_FACTOR;
        } else {
            mNormVolume = mAudioAnalysis.getNormMaxVolume();
        }
        // Log.i(TAG, String.valueOf(mNormVolume));
        return mNormVolume;
    }

    private double smoothNote(int index) {
        if(Math.abs(mNoteIntensities[index] - mPCP[index]) > VOL_INC_SMOOTHING_FACTOR) {
            mNoteIntensities[index] = ((mNoteIntensities[index] > mPCP[index]) ? (mNoteIntensities[index] - VOL_INC_SMOOTHING_FACTOR) : (mNoteIntensities[index] + VOL_INC_SMOOTHING_FACTOR));
        } else {
            mNoteIntensities[index] = mPCP[index];
        }
        // Log.i(TAG, String.valueOf(mNormVolume));
        return mNoteIntensities[index];
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

    // Used for volume
    double scaleSectorLength(double normIntensity, int buffer) {
       // Log.i(TAG, String.valueOf(normIntensity));
        int viewBuffer = 50 + buffer; // was 20
        int scaleMin = mHalfButtonWidth + viewBuffer;
        int scaleMax = mDisplayMetrics.widthPixels/2;
        int scaleRange = scaleMax - scaleMin;
        double aboveScaleMin = normIntensity * scaleRange;
        return scaleMin + aboveScaleMin;
    }

    // Used for note
    double scaleSectorLength(double normIntensity, double max) {
        int viewBuffer = 50;
        int scaleMin = mHalfButtonWidth + viewBuffer;
        double scaleMax = max;
        double scaleRange = scaleMax - scaleMin;
        double aboveScaleMin = normIntensity * scaleRange;
        return scaleMin + aboveScaleMin;
    }

    public void setmEndRunnable(boolean endRunnable) {
        mEndRunnable = endRunnable;
    }

    private int[] indexesOfThreeMostIntenseNote() {
        int[] indexes = new int[3];
        indexes[0] = noteToIndex(mAudioAnalysis.getmMostIntenseNote());
        indexes[1] = noteToIndex(mAudioAnalysis.getmSeconMostIntenseNote());
        indexes[2] = noteToIndex(mAudioAnalysis.getmThirdMostIntenseNote());
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
