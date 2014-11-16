package com.jzap.chordrecognizer_r;

/**
 * Created by Justin on 11/15/2014.
 */

public class AudioAnalysis {

    private String chord;
    private String mostIntenseNote;
    private String seconMostIntenseNote;
    private String thirdMostIntenseNote;

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
    // End Accessors/Modifiers
}
