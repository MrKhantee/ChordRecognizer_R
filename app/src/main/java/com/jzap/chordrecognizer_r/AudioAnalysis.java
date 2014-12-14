package com.jzap.chordrecognizer_r;

/**
 * Created by Justin on 11/15/2014.
 */

public class AudioAnalysis {

    private String chord;
    private String mostIntenseNote;
    private String seconMostIntenseNote;
    private String thirdMostIntenseNote;
    private double[] PCP;
    private boolean volumeThresholdMet;

    // Accessors/Modifiers
    public String getChord() {
        return chord;
    }
    public String getMostIntenseNote() {
        return mostIntenseNote;
    }
    public String getSeconMostIntenseNote() {
        return seconMostIntenseNote;
    }
    public String getThirdMostIntenseNote() {
        return thirdMostIntenseNote;
    }
    public double[] getPCP() {
        return PCP;
    }
    public boolean getVolumeThresholdMet() {
        return volumeThresholdMet;
    }
    public void setChord(String chord) {
        this.chord = chord;
    }
    public void setMostIntenseNote(String mostIntenseNote) {
        this.mostIntenseNote = mostIntenseNote;
    }
    public void setSeconMostIntenseNote(String seconMostIntenseNote) {
        this.seconMostIntenseNote = seconMostIntenseNote;
    }
    public void setThirdMostIntenseNote(String thirdMostIntenseNote) {
        this.thirdMostIntenseNote = thirdMostIntenseNote;
    }
    public void setPCP(double[] PCP) {
        this.PCP = PCP;
    }
    public void setVolumeThresholdMet(boolean volumeThresholdMet) {
        this.volumeThresholdMet = volumeThresholdMet;
    }

    // End Accessors/Modifiers
}
