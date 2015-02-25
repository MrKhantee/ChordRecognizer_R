package com.jzap.chordrecognizer_r;

/**
 * Created by Justin on 11/15/2014.
 */

public class AudioAnalysis {

    private String mChord;
    private String mMostIntenseNote;
    private String mSeconMostIntenseNote;
    private String mThirdMostIntenseNote;
    private double[] mPCP;
    private boolean mVolumeThresholdMet;
    private int mMaxVolume;
    private double mNormMaxVolume;

    // Accessors/Modifiers
    public String getmChord() {
        return mChord;
    }
    public String getmMostIntenseNote() {
        return mMostIntenseNote;
    }
    public String getmSeconMostIntenseNote() {
        return mSeconMostIntenseNote;
    }
    public String getmThirdMostIntenseNote() {
        return mThirdMostIntenseNote;
    }
    public double[] getmPCP() {
        return mPCP;
    }
    public boolean getmVolumeThresholdMet() {
        return mVolumeThresholdMet;
    }
    public int getmMaxVolume() { return mMaxVolume; }
    public double getNormMaxVolume() {
        double normMaxVolume;
        if (getmVolumeThresholdMet()) {
            mNormMaxVolume = 1;
        }
        else {
            mNormMaxVolume = (double) getmMaxVolume() / (double) RecordAudio.VOLUME_THRESHOLD;
        }
        if(mNormMaxVolume > 1) {
           mNormMaxVolume = 1;
        }
        return mNormMaxVolume;
    }
    public void setmChord(String mChord) {
        this.mChord = mChord;
    }
    public void setmMostIntenseNote(String mMostIntenseNote) {
        this.mMostIntenseNote = mMostIntenseNote;
    }
    public void setmSeconMostIntenseNote(String mSeconMostIntenseNote) {
        this.mSeconMostIntenseNote = mSeconMostIntenseNote;
    }
    public void setmThirdMostIntenseNote(String mThirdMostIntenseNote) {
        this.mThirdMostIntenseNote = mThirdMostIntenseNote;
    }
    public void setmPCP(double[] mPCP) {
        this.mPCP = mPCP;
    }
    public void setmVolumeThresholdMet(boolean mVolumeThresholdMet) {
        this.mVolumeThresholdMet = mVolumeThresholdMet;
    }
    public void setmMaxVolume(int mMaxVolume) {
        this.mMaxVolume = mMaxVolume;
    }
    public void setmNormMaxVolume(int volume) {
        mNormMaxVolume = volume;
    }
    // End Accessors/Modifiers
}
