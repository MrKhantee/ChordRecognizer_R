package com.jzap.chordrecognizer_r;

import android.media.AudioRecord;

/**
 * Created by Justin on 11/15/2014.
 */
public class RecordAudio {

    private static final String TAG = "RecordAudio";
    private static final int VOLUME_THRESHOLD = 30000;

    private AudioRecord recorder;

    private MainActivity mMainActivity;

    //Constructor
    public RecordAudio(MainActivity mainActivity) {
        int recorderState;
        recorder = new AudioRecord(AudioConfig.audioSrc, AudioConfig.frequency,
                AudioConfig.channelConfiguration, AudioConfig.audioEncoding, AudioConfig.bufferSize);
        recorderState = recorder.getState();
        mMainActivity =  mainActivity;
    }
    // End Constructor

    public boolean volumeThresholdMet() {
        short[] testAudioInput = new short[100];
        recorder.startRecording();
        while(mMainActivity.getmSwitch_autoDetect().isChecked()) {
            recorder.read(testAudioInput, 0, testAudioInput.length);
            for(int i = 0; i < testAudioInput.length; i++) {
                if(Math.abs(testAudioInput[i]) >= VOLUME_THRESHOLD) {
                    recorder.stop();
                    return true;
                }
            }//end for
        }//end while
        recorder.stop();
        recorder.release();
        return false;
    }

    public String[] doChordDetection() {
        short audioInput[] = new short[8000];
        recorder.startRecording();
        recorder.read(audioInput, 0, audioInput.length);
        recorder.stop();
        return PCP.getPCP(audioInput);
    }
}
