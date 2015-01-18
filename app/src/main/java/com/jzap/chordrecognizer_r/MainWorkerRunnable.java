package com.jzap.chordrecognizer_r;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Switch;

/**
 * Created by Justin on 11/15/2014.
 */
public class MainWorkerRunnable implements Runnable {

    private static final String TAG = "MainWorkerRunnable";
    private static final int DISPLAY_RECORDING_STATUS = 1;
    private static final int DISPLAY_RESULTS = 2;
    public static final int DISPLAY_VOLUME_STATUS = 3;
    public static final int DISPLAY_BUTTON_OFF = 4;

    private MainActivity mMainActivity;
    private Handler mHandler;

    private boolean mEndRunnable = false;

    // Constructor
    public MainWorkerRunnable(MainActivity mainActivity) {
        mMainActivity = mainActivity;
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {

                // TODO : Look into not creating this for every message
                Drawable drDormantButton = mMainActivity.getResources().getDrawable(R.drawable.button);
                //Drawable drButton = mMainActivity.getResources().getDrawable(R.drawable.lightbutton);
                Drawable drButton = mMainActivity.getResources().getDrawable(R.drawable.button);
                Drawable drReadyButton = mMainActivity.getResources().getDrawable(R.drawable.readybutton);
                Drawable drOneQuarterButton = mMainActivity.getResources().getDrawable(R.drawable.onequarterlight);
                Drawable drTwoQuarterButton = mMainActivity.getResources().getDrawable(R.drawable.twoquarterlight);
                Drawable drThreeQuarterButton = mMainActivity.getResources().getDrawable(R.drawable.threequarterlight);

                // Experiment 1a
                Bitmap bmpButton = BitmapFactory.decodeResource(mMainActivity.getResources(), R.drawable.plainbutton);


                // End Experiment 1a

                if(message.what == DISPLAY_RECORDING_STATUS) {
                    // TODO : Break out content of each conditional into methods
                    /*
                    int chordColor = lookupChordColor(((AudioAnalysis) message.obj).getChord());
                    ColorFilter filter = new LightingColorFilter(010101, chordColor);
                    drButton.setColorFilter(filter);
                    mMainActivity.getmIv_button().setImageDrawable(drButton);
                    */
                    // Experiment 1b
                    Bitmap bmpAfter = Bitmap.createBitmap(bmpButton.getWidth(), bmpButton.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bmpAfter);
                    Paint paint = new Paint();
                    int chordColor = lookupChordColor(((AudioAnalysis) message.obj).getChord());
                    paint.setColorFilter(new LightingColorFilter(010101, chordColor));
                    canvas.drawBitmap(bmpButton, 0, 0, paint);
                    paint = new Paint();
                    paint.setAntiAlias(true);
                    paint.setTextSize(400);
                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                    int[] loc = new int[2];
                    mMainActivity.getmIv_button().getLocationInWindow(loc);
                    // TODO : Locate "center" dynamically
                    canvas.drawText(((AudioAnalysis) message.obj).getChord(), loc[0] + 200, loc[1] + 700, paint);

                    mMainActivity.getmIv_button().setImageBitmap(bmpAfter);






                    // End Experiment 1b

                } else if(message.what == DISPLAY_RESULTS) {
                    //mMainActivity.getmIv_button().setImageDrawable(drReadyButton);
                    mMainActivity.getmTv_chord().setText(((AudioAnalysis) message.obj).getChord());
                    mMainActivity.getmTv_mostIntenseNote().setText(((AudioAnalysis) message.obj).getMostIntenseNote());
                    mMainActivity.getmTv_secMostIntenseNote().setText(((AudioAnalysis) message.obj).getSeconMostIntenseNote());
                    mMainActivity.getmTv_thirdMostIntenseNote().setText(((AudioAnalysis) message.obj).getThirdMostIntenseNote());
                } else if(message.what == DISPLAY_VOLUME_STATUS){
                    int i = (Integer) message.obj;
                    //Log.d(TAG, String.valueOf(i));
                    switch(i) {
                        case 0 : mMainActivity.getmIv_button().setImageDrawable(drReadyButton);
                           // Log.i(TAG, "Set Image View to Ready button");
                            break;
                        case 1 : mMainActivity.getmIv_button().setImageDrawable(drOneQuarterButton);
                            break;
                        case 2 : mMainActivity.getmIv_button().setImageDrawable(drTwoQuarterButton);
                            break;
                        case 3 : mMainActivity.getmIv_button().setImageDrawable(drThreeQuarterButton);
                            break;
                        default :
                            //Log.d(TAG, "Error with incoming Integer to handler : " + i);
                    }
                } else if(message.what == DISPLAY_BUTTON_OFF) {
                    Log.i(TAG, "************ Set Image View to Button ***********");
                    mMainActivity.getmIv_button().setImageDrawable(drDormantButton);
                }//end if/else
            }//end handleMessage()
        };//end mHandler initialization
    }
    // End Constructor

// Runnable Interface Implementations
    @Override
    public void run() {
        AudioAnalysis audioAnalysis;
        RecordAudio recordAudio = new RecordAudio(mMainActivity, mHandler);
        while(!mEndRunnable) {
            if(mMainActivity.getmRecording() && recordAudio.volumeThresholdMet() ) {
                // There's a tradeoff in getting the audioAnalysis here, instead of after the next line - app seems less responsive
                audioAnalysis = recordAudio.doChordDetection();
                mHandler.obtainMessage(DISPLAY_RECORDING_STATUS, audioAnalysis).sendToTarget();
               // This displays the results in plain text
               // mHandler.obtainMessage(DISPLAY_RESULTS, audioAnalysis).sendToTarget();
                try {
                    Thread.sleep(1000);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }//end try/catch
            }//end if
        }//end while
        recordAudio.destroyRecordAudio();
    }//end run()
// End Runnable Interface Implementations

// Accessors/Modifiers
    public MainActivity getmMainActivity() {
        return mMainActivity;
    }

    public void setmEndRunnable(boolean endRunnable) {
        mEndRunnable = endRunnable;
    }
//End Accessors/Modifiers

    // TODO : Get this right
    int lookupChordColor(String chord) {
        if(chord.contains("C") && !chord.contains("C#") || chord.contains("A")) {
            return NotesGraphView.colors[0];
        } else if(chord.contains("D") || chord.contains("B")) {
            return NotesGraphView.colors[1];
        } else if(chord.contains("E")) {
            return NotesGraphView.colors[2];
        } else if(chord.contains("F")) {
            return NotesGraphView.colors[3];
        }else if(chord.contains("G")) {
            return NotesGraphView.colors[4];
        }
        return -1;
    }
}
