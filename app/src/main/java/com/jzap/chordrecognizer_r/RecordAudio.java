package com.jzap.chordrecognizer_r;

import android.media.AudioRecord;

/**
 * Created by Justin on 11/15/2014.
 */
public class RecordAudio {

    private static final String TAG = "RecordAudio";
    private static final int VOLUME_THRESHOLD = 28000;

    private AudioRecord recorder;

    private MainActivity mMainActivity;

    //Constructor
    public RecordAudio(MainActivity mainActivity) {
        recorder = new AudioRecord(AudioConfig.audioSrc, AudioConfig.frequency,
                AudioConfig.channelConfiguration, AudioConfig.audioEncoding, AudioConfig.bufferSize);
        mMainActivity =  mainActivity;
    }
    // End Constructor

    public boolean volumeThresholdMet() {
        short[] testAudioInput = new short[100];
        recorder.startRecording();
        while(mMainActivity.getmRecording()) {
            recorder.read(testAudioInput, 0, testAudioInput.length);
            for(int i = 0; i < testAudioInput.length; i++) {
                if(Math.abs(testAudioInput[i]) >= VOLUME_THRESHOLD) {
                    recorder.stop();
                    return true;
                }
            }//end for
        }//end while
        recorder.stop();
        return false;
    }

    public AudioAnalysis doChordDetection() {
        short audioInput[] = new short[32000];
        recorder.startRecording();
        recorder.read(audioInput, 0, audioInput.length);
        recorder.stop();
        return new ProcessAudio().detectChord(audioInput);
    }

    public void destroyRecordAudio() {
        recorder.release();
    }
}
