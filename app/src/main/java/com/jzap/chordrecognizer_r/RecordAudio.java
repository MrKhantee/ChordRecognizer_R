package com.jzap.chordrecognizer_r;

import android.media.AudioRecord;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justin on 11/15/2014.
 */
public class RecordAudio {

    private static final String TAG = "RecordAudio";
    public static final int VOLUME_THRESHOLD = 28000;
    private static final int graphPCPInterval = 320;

    private static int count = 1;


    private AudioRecord recorder;

    private MainActivity mMainActivity;
    private Handler mHandler;

    //Constructor
    public RecordAudio(MainActivity mainActivity, Handler handler) {
        recorder = new AudioRecord(AudioConfig.audioSrc, AudioConfig.frequency,
                AudioConfig.channelConfiguration, AudioConfig.audioEncoding, AudioConfig.bufferSize);
        mMainActivity =  mainActivity;
        mHandler = handler;
    }
    // End Constructor

    public boolean volumeThresholdMet() {
        // TODO : Rename to something more accurate
        ArrayList<Short> cumulativeTestAudioInput = new ArrayList<Short>();
        short[] testAudioInput = new short[100];
        recorder.startRecording();
        while(mMainActivity.getmRecording()) {
            int greatestSample = 0;
            recorder.read(testAudioInput, 0, testAudioInput.length);
            accumulateAudio(testAudioInput, cumulativeTestAudioInput);
            for(int i = 0; i < testAudioInput.length; i++) {
                if(testAudioInput[i] > greatestSample) {
                    greatestSample = testAudioInput[i];
                }
                if(Math.abs(testAudioInput[i]) >= VOLUME_THRESHOLD) {
                    //Log.i(TAG, String.valueOf(greatestSample));
                    recorder.stop();
                    return true;
                }
            }//end for
            //Log.i(TAG, String.valueOf(greatestSample));
            showVolume(greatestSample);

            // TODO : This is wasteful, redesign

            if (count >= graphPCPInterval) {
                new ProcessAudio(mMainActivity).detectChord(toPrimitive(cumulativeTestAudioInput), false);
                cumulativeTestAudioInput = new ArrayList<Short>();
                count = 0;
            }
            count++;

        }//end while
        // TODO : Fix this hack to reset button
        showVolume(-1);
        recorder.stop();
        return false;
    }

    public void accumulateAudio(short[] audio, List<Short> cumulativeAudio) {
        for(int i = 0; i < audio.length; i++) {
            cumulativeAudio.add(audio[i]);
        }
    }

    public short[] toPrimitive(ArrayList<Short> theList) {
        short[] result = new short[theList.size()];
        for(int i = 0; i < result.length; i++) {
            result[i] = theList.get(i);
        }
        return result;
    }

    public AudioAnalysis doChordDetection() {
        short audioInput[] = new short[32000];
        recorder.startRecording();
        recorder.read(audioInput, 0, audioInput.length);
        recorder.stop();

        // TODO : Introduce functionality to update NotesGraphView

        //return new ProcessAudio().detectChord(audioInput);
        return new ProcessAudio(mMainActivity).detectChord(audioInput, true);
    }

    public void destroyRecordAudio() {
        recorder.release();
    }


    public void showVolume(int greatestSample) {
        Integer i;
        int quarterVolThrshld = VOLUME_THRESHOLD/4;

        if(greatestSample < 0) {
            // TODO : This is part of the hack from above
            Log.i(TAG, "greatestSample = -1");
            mHandler.obtainMessage(MainWorkerRunnable.DISPLAY_BUTTON_OFF).sendToTarget();
            return;
        } else if((greatestSample > 0) && (greatestSample < 500)) {
            i = new Integer(0);
        } else if(greatestSample < (VOLUME_THRESHOLD - quarterVolThrshld * 3)) {
            i = new Integer(1);
        } else if(greatestSample < VOLUME_THRESHOLD - quarterVolThrshld * 2) {
            i = new Integer(2);
        } else {
            i = new Integer(3);
        }
       // mHandler.obtainMessage(MainWorkerRunnable.DISPLAY_VOLUME_STATUS, i).sendToTarget();
    }


}
