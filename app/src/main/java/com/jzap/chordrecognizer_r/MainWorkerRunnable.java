package com.jzap.chordrecognizer_r;

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

    // Constructor
    public MainWorkerRunnable(MainActivity mainActivity) {
        mMainActivity = mainActivity;
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                if(message.what == DISPLAY_RECORDING_STATUS) {
                    mMainActivity.getmRb_recording().setChecked(true);
                } else if(message.what == DISPLAY_RESULTS) {
                    //TODO : DISPLAY RESULTS
                    mMainActivity.getmRb_recording().setChecked(false);
                }//end if/else
            }//end handleMessage()
        };//end mHandler initialization
    }
    // End Constructor

// Runnable Interface Implementations
    @Override
    public void run() {
        String[] chordDetectionResults = new String[4];
        Switch switch_autoDetect = getmMainActivity().getmSwitch_autoDetect();
        RecordAudio recordAudio = new RecordAudio(mMainActivity);
        while(true) {
            if(switch_autoDetect.isChecked() && recordAudio.volumeThresholdMet() ) {
                mHandler.obtainMessage(DISPLAY_RECORDING_STATUS).sendToTarget();
                chordDetectionResults = recordAudio.doChordDetection();
                mHandler.obtainMessage(DISPLAY_RESULTS, chordDetectionResults).sendToTarget();
                try {
                    Thread.sleep(3000);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }//end try/catch
            }//end if
        }//end while
    }//end run()
// End Runnable Interface Implementations

// Accessors/Modifiers
    public MainActivity getmMainActivity() {
        return mMainActivity;
    }
//End Accessors/Modifiers
}
