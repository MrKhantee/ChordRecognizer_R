package com.jzap.chordrecognizer_r;

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
                Drawable drButton = mMainActivity.getResources().getDrawable(R.drawable.lightbutton);
                Drawable drReadyButton = mMainActivity.getResources().getDrawable(R.drawable.readybutton);
                Drawable drOneQuarterButton = mMainActivity.getResources().getDrawable(R.drawable.onequarterlight);
                Drawable drTwoQuarterButton = mMainActivity.getResources().getDrawable(R.drawable.twoquarterlight);
                Drawable drThreeQuarterButton = mMainActivity.getResources().getDrawable(R.drawable.threequarterlight);

                if(message.what == DISPLAY_RECORDING_STATUS) {
                    // TODO : Break out content of each conditoinal into methods
                    mMainActivity.getmIv_button().setImageDrawable(drButton);
                } else if(message.what == DISPLAY_RESULTS) {
                    mMainActivity.getmIv_button().setImageDrawable(drReadyButton);
                    mMainActivity.getmTv_chord().setText(((AudioAnalysis) message.obj).getChord());
                    mMainActivity.getmTv_mostIntenseNote().setText(((AudioAnalysis) message.obj).getMostIntenseNote());
                    mMainActivity.getmTv_secMostIntenseNote().setText(((AudioAnalysis) message.obj).getSeconMostIntenseNote());
                    mMainActivity.getmTv_thirdMostIntenseNote().setText(((AudioAnalysis) message.obj).getThirdMostIntenseNote());
                } else if(message.what == DISPLAY_VOLUME_STATUS){
                    int i = (Integer) message.obj;
                    //Log.d(TAG, String.valueOf(i));
                    switch(i) {
                        case 1 : mMainActivity.getmIv_button().setImageDrawable(drOneQuarterButton);
                            break;
                        case 2 : mMainActivity.getmIv_button().setImageDrawable(drTwoQuarterButton);
                            break;
                        case 3 : mMainActivity.getmIv_button().setImageDrawable(drThreeQuarterButton);
                            break;
                        default :
                            //Log.d(TAG, "Error with incoming Integer to handler : " + i);
                    }
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
                mHandler.obtainMessage(DISPLAY_RECORDING_STATUS).sendToTarget();
                audioAnalysis = recordAudio.doChordDetection();
                mHandler.obtainMessage(DISPLAY_RESULTS, audioAnalysis).sendToTarget();
                try {
                    Thread.sleep(200);
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
}
