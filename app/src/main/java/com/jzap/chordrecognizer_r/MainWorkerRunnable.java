package com.jzap.chordrecognizer_r;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Switch;

/**
 * Created by Justin on 11/15/2014.
 */
public class MainWorkerRunnable implements Runnable {

    private static final String TAG = "MainWorkerRunnable";
    private static final int DISPLAY_RECORDING_STATUS = 1;
    private static final int DISPLAY_RESULTS = 2;

    private MainActivity mMainActivity;
    private Handler mHandler;

    private boolean mEndRunnable = false;

    // Constructor
    public MainWorkerRunnable(MainActivity mainActivity) {
        mMainActivity = mainActivity;
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {

                Drawable drButton = mMainActivity.getResources().getDrawable(R.drawable.lightbutton);
                Drawable drReadyButton = mMainActivity.getResources().getDrawable(R.drawable.readybutton);

                if(message.what == DISPLAY_RECORDING_STATUS) {
                    // TODO : Break out into methods
                    mMainActivity.getmIv_button().setImageDrawable(drButton);
                } else if(message.what == DISPLAY_RESULTS) {
                    mMainActivity.getmIv_button().setImageDrawable(drReadyButton);
                    mMainActivity.getmTv_chord().setText(((AudioAnalysis) message.obj).getChord());
                    mMainActivity.getmTv_mostIntenseNote().setText(((AudioAnalysis) message.obj).getMostIntenseNote());
                    mMainActivity.getmTv_secMostIntenseNote().setText(((AudioAnalysis) message.obj).getSeconMostIntenseNote());
                    mMainActivity.getmTv_thirdMostIntenseNote().setText(((AudioAnalysis) message.obj).getThirdMostIntenseNote());
                }//end if/else
            }//end handleMessage()
        };//end mHandler initialization
    }
    // End Constructor

// Runnable Interface Implementations
    @Override
    public void run() {
        AudioAnalysis audioAnalysis;
        RecordAudio recordAudio = new RecordAudio(mMainActivity);
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
